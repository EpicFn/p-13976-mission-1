package com
data class Quote (var text: String, var author: String)

open class QuoteDictionary {
    // --------------------------------------------------------
    // 필드
    // --------------------------------------------------------
    private val quotes = mutableMapOf<Int, Quote>()
    private var nextIndex = 1 // 다음에 추가할 인덱스 번호
    private val fileManager = FileManager()

    // --------------------------------------------------------
    // 초기화
    // --------------------------------------------------------
    init {
        // 파일에서 명언 불러오기
        val lastId = fileManager.loadLastId()

        for (i in 1..lastId) {
            val quoteData = fileManager.loadQuote(i)
            if (quoteData != null) {
                quotes[i] = Quote(quoteData.text, quoteData.author)
            }
        }

        nextIndex = lastId + 1 // 다음 인덱스는 마지막 인덱스 + 1
    }


    // --------------------------------------------------------
    // 기능 메소드
    // --------------------------------------------------------

    // 명언 추가
    open fun addQuote(text: String, author: String): Int {
        val index = nextIndex++
        quotes[index] = Quote(text, author)

        // 파일에 저장
        fileManager.saveQuote(FileManager.QuoteData(index, author, text))
        fileManager.saveLastId(index) // 마지막 ID 저장

        return index
    }

    // 명언 목록 조회
    open fun getQuotes(): List<Map<String, String>> {
        return quotes.entries.map { (index, quote) ->
            mapOf(
                "index" to index.toString(),
                "text" to quote.text,
                "author" to quote.author
            )
        }
    }

    // 명언 삭제
    open fun deleteQuote(index: Int): Boolean {
        if (!quotes.containsKey(index)) {
            return false // 해당 인덱스의 명언이 존재하지 않음
        }

        // 파일에서 삭제
        fileManager.deleteQuote(index)

        return quotes.remove(index) != null
    }

    // 명언 수정
    open fun updateQuote(index: Int, newText: String, newAuthor: String) : Boolean {
        if (quotes.containsKey(index)) {
            quotes[index] = Quote(newText, newAuthor)

            // 파일에 수정된 명언 저장
            fileManager.saveQuote(FileManager.QuoteData(index, newAuthor, newText))

            return true
        } else {
            return false
        }
    }

    // 명언 목록 초기화
    open fun clearQuotes() {
        quotes.clear()
        nextIndex = 0

        fileManager.clearQuotes() // 파일에서 명언 삭제
    }

    // 명언 개수 조회
    fun getQuoteCount(): Int {
        return quotes.size
    }

    // 특정 인덱스의 명언 조회
    open fun getQuote(index: Int): Quote? {
        return quotes[index] ?: run {
            null
        }
    }

    // 빌드 실행
    open fun buildDataJson() {
        fileManager.buildDataJson(quotes.map { FileManager.QuoteData(it.key, it.value.author, it.value.text) })
    }
}