package minios.program;

public enum InstructionType {
    CPU, // CPU 계산 수행
    IO, // I/O 요청 -> BLOCKED 상태로 전이
    TERMINATE // 프로세스 종료
}
