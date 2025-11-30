package minios.pcb;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class AddressSpace {

    private final byte[] code;
    private final byte[] staticData;
    private final Deque<Object> stack = new ArrayDeque<>();
    private final List<Object> heap = new ArrayList<>();

    public AddressSpace(final byte[] code, final byte[] staticData) {
        this.code = code;
        this.staticData = staticData;
    }

    public void pushStack(Object value) {
        stack.push(value);
    }

    public Object popStack() {
        return stack.pop();
    }

    // 힙 영역에 데이터 추가/할당
    public void malloc(Object object) {
        heap.add(object);
    }

    public byte[] getCode() {
        return code;
    }

    public byte[] getStaticData() {
        return staticData;
    }
}
