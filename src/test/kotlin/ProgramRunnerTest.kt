import com.ProgramRunner
import org.junit.jupiter.api.*
import java.io.*
import kotlin.test.assertTrue

class ProgramRunnerRunTest{

    private lateinit var runner: ProgramRunner // 테스트 모드로 실행
    private val originalIn = System.`in`
    private val originalOut = System.out

    private lateinit var testIn: ByteArrayInputStream
    private lateinit var testOut: ByteArrayOutputStream

    @BeforeEach
    fun setUp() {
        runner = ProgramRunner(testMode = true)
        testOut = ByteArrayOutputStream()
        System.setOut(PrintStream(testOut))
    }

    @AfterEach
    fun tearDown() {
        System.setIn(originalIn)
        System.setOut(originalOut)
        testOut.close()
        testIn.close()
        runner.quoteDictionary.clearQuotes() // 테스트 후 명언 목록 초기화
    }

    private fun provideInput(input: String) {
        testIn = ByteArrayInputStream(input.toByteArray())
        System.setIn(testIn)
    }

    @Test
    fun testRun_registerAndListAndExit() {
        // 시뮬레이션할 사용자 입력
        val input = buildString {
            appendLine("등록")       // 명령어: 등록
            appendLine("테스트 명언")  // 명언 입력
            appendLine("테스트 작가")  // 작가 입력
            appendLine("목록")       // 명령어: 목록
            appendLine("종료")       // 명령어: 종료
        }

        provideInput(input)

        runner.run()

        val output = testOut.toString()

        println("=== OUTPUT START ===")
        println(output)
        println("=== OUTPUT END ===")


        // 출력에 등록 완료 메시지 포함 여부 확인
        assertTrue(output.contains("1번 명언이 등록되었습니다."))
        // 목록에 등록된 명언과 작가가 포함되어야 함
        assertTrue(output.contains("번호 / 작가 / 명언"))
        assertTrue(output.contains("---------------------"))
        assertTrue(output.contains("1 / 테스트 작가 / 테스트 명언"))
        // 종료 메시지 확인
        assertTrue(output.contains("앱을 종료합니다."))
    }

    @Test
    fun testRun_registerAndDeleteAndExit() {
        // 시뮬레이션할 사용자 입력
        val input = buildString {
            appendLine("등록")       // 명령어: 등록
            appendLine("테스트 명언")  // 명언 입력
            appendLine("테스트 작가")  // 작가 입력
            appendLine("삭제?id=1")     // 명령어: 삭제
            appendLine("목록")       // 명령어: 목록
            appendLine("종료")       // 명령어: 종료
        }

        provideInput(input)

        runner.run()

        val output = testOut.toString()

        println("=== OUTPUT START ===")
        println(output)
        println("=== OUTPUT END ===")

        // 출력에 등록 완료 메시지 포함 여부 확인
        assertTrue(output.contains("1번 명언이 등록되었습니다."))
        // 삭제 완료 메시지 확인
        assertTrue(output.contains("1번 명언이 삭제되었습니다."))
        // 목록에 아무것도 없음을 나타내는 메시지 확인
        assertTrue(output.contains("번호 / 작가 / 명언"))
        assertTrue(output.contains("---------------------"))
        assertTrue(output.contains("등록된 명언이 없습니다."))
        // 종료 메시지 확인
        assertTrue(output.contains("앱을 종료합니다."))
    }

    @Test
    fun testRun_registerAndUpdateAndExit() {
        // 시뮬레이션할 사용자 입력
        val input = buildString {
            appendLine("등록")       // 명령어: 등록
            appendLine("테스트 명언")  // 명언 입력
            appendLine("테스트 작가")  // 작가 입력
            appendLine("수정?id=1")     // 명령어: 수정
            appendLine("수정된 명언")  // 수정된 명언 입력
            appendLine("수정된 작가")  // 수정된 작가 입력
            appendLine("목록")       // 명령어: 목록
            appendLine("종료")       // 명령어: 종료
        }

        provideInput(input)

        runner.run()

        val output = testOut.toString()

        println("=== OUTPUT START ===")
        println(output)
        println("=== OUTPUT END ===")

        // 출력에 등록 완료 메시지 포함 여부 확인
        assertTrue(output.contains("1번 명언이 등록되었습니다."))
        // 수정 완료 메시지 확인
        assertTrue(output.contains("1번 명언이 수정되었습니다."))
        // 목록에 수정된 명언과 작가가 포함되어야 함
        assertTrue(output.contains("번호 / 작가 / 명언"))
        assertTrue(output.contains("---------------------"))
        assertTrue(output.contains("1 / 수정된 작가 / 수정된 명언"))
        // 종료 메시지 확인
        assertTrue(output.contains("앱을 종료합니다."))
    }
}
