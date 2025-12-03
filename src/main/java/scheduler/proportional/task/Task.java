package scheduler.proportional.task;

public class Task {
    private final String name;
    private final int tickets; // 추첨권 개수 = CPU 비율

    public Task(final String name, final int tickets) {
        if (tickets <= 0) {
            throw new IllegalArgumentException("ticket 개수는 0보다 커야합니다.");
        }

        this.name = name;
        this.tickets = tickets;
    }

    public String getName() {
        return name;
    }

    public int getTickets() {
        return tickets;
    }

    @Override
    public String toString() {
        return "Task{" +
                "name='" + name + '\'' +
                ", tickets=" + tickets +
                '}';
    }


}
