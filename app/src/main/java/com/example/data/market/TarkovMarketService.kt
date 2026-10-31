package com.example.data.market

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class TarkovMarketService(
    private val endpointUrl: String = "https://api.tarkov.dev/graphql"
) {

    suspend fun fetchMarketPrices(): Result<List<MarketItem>> = withContext(Dispatchers.IO) {
        try {
            val url = URL(endpointUrl)
            val connection = (url.openConnection() as HttpURLConnection).apply {
                requestMethod = "POST"
                doOutput = true
                doInput = true
                connectTimeout = 8000
                readTimeout = 10000
                setRequestProperty("Content-Type", "application/json; charset=utf-8")
                setRequestProperty("Accept", "application/json")
                setRequestProperty("User-Agent", "Mozilla/5.0 (Linux; Android 14; Mobile; TarkovRadar/2.0)")
            }

            val query = """
                {
                  items(lang: en) {
                    id
                    name
                    shortName
                    basePrice
                    avg24hPrice
                    lastLowPrice
                    changeLast48hPercent
                    width
                    height
                    updated
                    sellFor {
                      price
                      source
                    }
                  }
                }
            """.trimIndent()

            val requestJson = JSONObject().apply {
                put("query", query)
            }

            OutputStreamWriter(connection.outputStream, "UTF-8").use { writer ->
                writer.write(requestJson.toString())
                writer.flush()
            }

            val responseCode = connection.responseCode
            if (responseCode != HttpURLConnection.HTTP_OK) {
                return@withContext Result.failure(
                    Exception("Tarkov.dev API returned HTTP $responseCode")
                )
            }

            val responseText = BufferedReader(InputStreamReader(connection.inputStream, "UTF-8")).use { reader ->
                reader.readText()
            }

            if (!responseText.trimStart().startsWith("{")) {
                return@withContext Result.failure(
                    Exception("Unexpected non-JSON response from server")
                )
            }

            val rootJson = JSONObject(responseText)
            if (rootJson.has("errors")) {
                val errorMsg = rootJson.getJSONArray("errors").opt(0)?.toString() ?: "GraphQL error"
                return@withContext Result.failure(Exception(errorMsg))
            }

            val dataObj = rootJson.optJSONObject("data")
                ?: return@withContext Result.failure(Exception("Missing 'data' in GraphQL response"))

            val itemsArray = dataObj.optJSONArray("items")
                ?: return@withContext Result.failure(Exception("Missing 'items' in GraphQL data"))

            val itemsList = ArrayList<MarketItem>(itemsArray.length())
            for (i in 0 until itemsArray.length()) {
                val itemObj = itemsArray.getJSONObject(i)
                val id = itemObj.optString("id", "")
                val name = itemObj.optString("name", "Unknown Item")
                val shortName = itemObj.optString("shortName", name)
                val basePrice = itemObj.optInt("basePrice", 0)
                val avg24h = itemObj.optInt("avg24hPrice", 0)
                val lastLow = itemObj.optInt("lastLowPrice", 0)
                val change48h = itemObj.optDouble("changeLast48hPercent", 0.0)
                val width = itemObj.optInt("width", 1)
                val height = itemObj.optInt("height", 1)
                val slots = (width * height).coerceAtLeast(1)
                val updated = itemObj.optString("updated", "Live")

                // Extract best trader price from sellFor array
                var bestTrader: String? = null
                var bestTraderPrice = 0
                val sellForArray = itemObj.optJSONArray("sellFor")
                if (sellForArray != null) {
                    for (j in 0 until sellForArray.length()) {
                        val sellObj = sellForArray.getJSONObject(j)
                        val source = sellObj.optString("source", "")
                        val price = sellObj.optInt("price", 0)
                        if (source.lowercase() != "flea-market" && source.lowercase() != "flea_market") {
                            if (price > bestTraderPrice) {
                                bestTraderPrice = price
                                bestTrader = source.replaceFirstChar { it.uppercase() }
                            }
                        }
                    }
                }

                val category = categorizeItem(name)

                itemsList.add(
                    MarketItem(
                        id = id,
                        name = name,
                        shortName = shortName,
                        basePrice = basePrice,
                        avg24hPrice = avg24h,
                        lastLowPrice = lastLow,
                        changeLast48hPercent = change48h,
                        bestTraderName = bestTrader,
                        bestTraderPriceRUB = bestTraderPrice,
                        category = category,
                        slots = slots,
                        updated = updated
                    )
                )
            }

            Result.success(itemsList)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun categorizeItem(name: String): String {
        val lower = name.lowercase()
        return when {
            lower.contains("key") || lower.contains("keycard") -> "Keys & Cards"
            lower.contains("bitcoin") || lower.contains("graphics card") || lower.contains("ledx") ||
                    lower.contains("vpx") || lower.contains("virtex") || lower.contains("intelligence") ||
                    lower.contains("moonshine") || lower.contains("flash drive") -> "Valuables & Tech"
            lower.contains("salewa") || lower.contains("med") || lower.contains("defibrillator") ||
                    lower.contains("ophthalmoscope") || lower.contains("ibuprofen") || lower.contains("golden star") ||
                    lower.contains("propital") || lower.contains("morphine") -> "Medical"
            lower.contains("mm") || lower.contains("ammo") || lower.contains("slick") ||
                    lower.contains("helmet") || lower.contains("armor") || lower.contains("plate") -> "Ammo & Armor"
            else -> "General"
        }
    }
}
