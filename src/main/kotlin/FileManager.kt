package com

import java.io.File

class FileManager{
    data class QuoteData(
        val index: Int,
        val author: String,
        val text: String
    )
    private val baseDir = File("db/wiseSaying")

    init {
        if (!baseDir.exists()) baseDir.mkdirs()
    }

    // --------------------------------------------------------------
    // 명언 저장 및 불러오기 메서드
    // --------------------------------------------------------------

    fun saveQuote(quote: QuoteData) {
        val file = File(baseDir, "${quote.index}.json")
        val jsonString = quoteToJson(quote)
        file.writeText(jsonString)
    }

    fun loadQuote(index: Int): QuoteData? {
        val file = File(baseDir, "$index.json")
        if (!file.exists()) return null
        val jsonString = file.readText()
        return jsonToQuote(jsonString)
    }

    fun deleteQuote(index: Int) {
        val file = File(baseDir, "$index.json")
        if (file.exists()) {
            file.delete()
        }
    }

    fun saveLastId(id: Int) {
        val file = File(baseDir, "lastId.txt")
        file.writeText(id.toString())
    }

    fun loadLastId(): Int {
        val file = File(baseDir, "lastId.txt")
        if (!file.exists()) return 0
        return file.readText().toIntOrNull() ?: 0
    }

    fun clearQuotes() {
        baseDir.listFiles()?.forEach { it.delete() }
        saveLastId(1) // 초기화 후 마지막 ID를 1로 설정
    }

    // --------------------------------------------------------------
    // json 변환 메서드
    // --------------------------------------------------------------
    // 명언 -> JSON 변환
    fun quoteToJson(quote: QuoteData): String {
        // 특수문자 간단 이스케이프 처리
        fun escape(str: String) = str
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")

        return """
        {
            "index": ${quote.index},
            "author": "${escape(quote.author)}",
            "text": "${escape(quote.text)}"
        }
    """.trimIndent()
    }

    // JSON -> 명언 변환
    fun jsonToQuote(json: String): QuoteData? {
        // 정규식으로 매우 간단히 key-value 추출 (정확한 JSON 파싱 아님)
        val regex = Regex("\"index\"\\s*:\\s*(\\d+).*\"author\"\\s*:\\s*\"([^\"]*)\".*\"text\"\\s*:\\s*\"([^\"]*)\"", RegexOption.DOT_MATCHES_ALL)
        val match = regex.find(json) ?: return null

        val (indexStr, author, text) = match.destructured
        return QuoteData(indexStr.toIntOrNull() ?: return null, author, text)
    }
}