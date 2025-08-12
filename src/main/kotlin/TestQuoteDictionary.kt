package com

class TestQuoteDictionary : QuoteDictionary() {

    // --------------------------------------------------------
    // 필드
    // --------------------------------------------------------
    private val quotes = mutableMapOf<Int, Quote>()
    private var nextIndex = 1 // 다음에 추가할 인덱스 번호


    // --------------------------------------------------------
    // 기능 메소드
    // --------------------------------------------------------

    // 명언 추가
    override
    fun addQuote(text: String, author: String): Int {
        val index = nextIndex++
        quotes[index] = Quote(text, author)
        return index
    }

    // 명언 목록 조회
    override
    fun getQuotes(keywordType : String, keyword : String): List<Map<String, String>> {

        return quotes.map { (index, quote) ->
            val map = mutableMapOf<String, String>()
            map["index"] = index.toString()
            map["author"] = quote.author
            map["text"] = quote.text

            // 키워드 필터링
            if (keywordType.isNotEmpty() && keyword.isNotEmpty()) {
                when (keywordType.lowercase()) {
                    "author" -> if (!quote.author.contains(keyword, ignoreCase = true)) return@map null
                    "content" -> if (!quote.text.contains(keyword, ignoreCase = true)) return@map null
                }
            }

            map
        }.filterNotNull() // null 제거
    }

    // 명언 삭제
    override
    fun deleteQuote(index: Int): Boolean {
        if (!quotes.containsKey(index)) {
            return false // 해당 인덱스의 명언이 존재하지 않음
        }

        return quotes.remove(index) != null
    }

    // 명언 수정
    override
    fun updateQuote(index: Int, newText: String, newAuthor: String) : Boolean {
        if (quotes.containsKey(index)) {
            quotes[index] = Quote(newText, newAuthor)
            return true
        } else {
            return false
        }
    }

    // 명언 목록 초기화
    override
    fun clearQuotes() {
        quotes.clear()
        nextIndex = 0
    }

    // 특정 인덱스의 명언 조회
    override
    fun getQuote(index: Int): Quote? {
        return quotes[index] ?: run {
            null
        }
    }



    // 빌드 실행
    override
    fun buildDataJson() {

    }
}