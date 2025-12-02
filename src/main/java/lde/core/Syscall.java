package lde.core;

/**
 * 아주 단순화한 시스템 콜 종류.
 * READ: I/O 요청 -> BLOCKED
 * WRITE: I/O 요청 -> 바로 처리했다고 치고 READY
 * YIELD: 자발적 CPU 양보
 * EXIT: 프로세스 종료
 * NONE: 일반 연산(컴퓨트)용
 */
public enum Syscall {
    READ,
    WRITE,
    YIELD,
    EXIT,
    NONE
}
