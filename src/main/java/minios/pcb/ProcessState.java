package minios.pcb;

public enum ProcessState {
    NEW, // 프로세스가 막 생성된 상태
    READY,
    RUNNING,
    BLOCKED,
    TERMINATED
}
