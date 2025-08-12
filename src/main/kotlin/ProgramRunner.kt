package com

class ProgramRunner(
    var testMode: Boolean = false // 테스트 모드 여부
){
    var cmd : String = "default"
    val quoteDictionary = if (testMode) {
        TestQuoteDictionary() // 테스트 모드에서는 TestQuoteDictionary 사용
    } else {
        QuoteDictionary() // 일반 모드에서는 QuoteDictionary 사용
    }

    fun run() {
        println("== 명언 앱 ==")

        do {
            print("명령) ")
            val input = readLine() ?: "" // null-safe 처리
            cmd = input.trim() // 앞뒤 공백 제거

            when {
                cmd == "종료" -> {
                    println("앱을 종료합니다.")
                    break
                }
                cmd == "등록" -> registerQuote()
                cmd.startsWith("목록") -> listQuotes(getCmdRq())
                cmd.startsWith("삭제") -> deleteQuote(getCmdRq()["id"]?.toIntOrNull() ?: -1)
                cmd.startsWith("수정") -> updateQuote(getCmdRq()["id"]?.toIntOrNull() ?: -1)
                cmd == "빌드" -> build()

                else -> println("알 수 없는 명령입니다.")
            }
        } while (true)
    }

    // -----------------------------------------------------------------------
    // command 별 처리 함수
    // -----------------------------------------------------------------------

    // 명언 등록
    fun registerQuote() {
        // 입력 받기
        print("명언 : ")
        val quoteText = readLine() ?: ""
        print("작가 : ")
        val quoteAuthor = readLine() ?: ""

        // 명언 등록
        val idx = quoteDictionary.addQuote(quoteText, quoteAuthor)
        println("${idx}번 명언이 등록되었습니다.")
    }

    // 명언 목록 조회
    fun listQuotes(rq : Map<String, String>) {
        val quotes : List<Map<String, String>>


        // 페이징 확인
        var page = 1 // 기본 페이지 번호 설정
        if(rq.containsKey("page")) {
            // 페이지 번호가 유효하지 않으면 1로 설정
            page = rq["page"]?.toIntOrNull() ?: 1
        }

        // 키워드가 있는 경우 필터링
        if (rq.containsKey("keyword") && rq.containsKey("keywordType")) {
            val keyword = rq["keyword"] ?: ""
            val type = rq["keywordType"] ?: ""
            quotes = quoteDictionary.getQuotes(type, keyword, page)
        } else {
            // 키워드가 없는 경우 전체 목록 조회
            quotes = quoteDictionary.getQuotes(page = page)
        }

        // 출력
        println("번호 / 작가 / 명언")
        println("---------------------")
        if (quotes.isEmpty()) {
            println("등록된 명언이 없습니다.")
        } else {
            for (quote in quotes.asReversed()) {
                println("${quote["index"]} / ${quote["author"]} / ${quote["text"]}")
            }
        }
    }

    // 명언 삭제
    fun deleteQuote(idx: Int) {
        if (quoteDictionary.deleteQuote(idx)) {
            println("${idx}번 명언이 삭제되었습니다.")
        } else {
            println("${idx}번 명언은 존재하지 않습니다.")
        }
    }

    // 명언 수정
    fun updateQuote(idx : Int) {
        // 존재 여부 확인
        val quote = quoteDictionary.getQuote(idx)
        if (quote == null) {
            println("${idx}번 명언은 존재하지 않습니다.")
            return
        }

        // 입력 받기
        println("명언(기존) : ${quote.text}")
        print("명언 : ")
        val newQuoteText = readLine() ?: ""
        println("작가(기존) : ${quote.author}")
        print("작가 : ")
        val newQuoteAuthor = readLine() ?: ""

        // 명언 수정
        if (quoteDictionary.updateQuote(idx, newQuoteText, newQuoteAuthor)) {
            println("${idx}번 명언이 수정되었습니다.")
        } else {
            println("${idx}번 명언은 존재하지 않습니다.")
        }
    }

    // 빌드
    fun build() {
        quoteDictionary.buildDataJson()
        println("data.json 파일의 내용이 갱신되었습니다.")
    }

    // ---------------------------------------------------------------------------
    // 기타 메소드
    // ---------------------------------------------------------------------------
    fun getCmdRq(): Map<String, String> {
        // cmd가 null이거나 비어있으면 빈 Map 반환
        if (cmd.isEmpty()) {
            return emptyMap()
        }

        val result = mutableMapOf<String, String>()

        val parts = cmd!!.split("?", limit = 2)
        result["cmd"] = parts[0] // ? 앞의 부분은 명령어

        if (parts.size > 1) {
            // ? 뒤의 부분을 key=value 형식으로 나눠서 Map에 추가
            parts[1].split("&").forEach { param ->
                val kv = param.split("=", limit = 2)
                if (kv.size == 2) {
                    result[kv[0]] = kv[1]
                }
            }
        }

        return result
    }



}