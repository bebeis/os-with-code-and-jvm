package processapi;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MiniShell {

    static void main() throws IOException, InterruptedException {
        example1();
        example2();
        example3();
    }

    private static void example1() throws IOException, InterruptedException {
        Process p = new ProcessBuilder("ls", "-al")
                .inheritIO()
                .start();

        int exitCode = p.waitFor();
        System.out.println("exitCode = " + exitCode);
    }

    private static void example2() throws IOException, InterruptedException {
        Process p = new ProcessBuilder("wc", "p3.c")
                .redirectOutput(new File("out.txt"))
                .start();

        int exitCode = p.waitFor();
        System.out.println("exitCode = " + exitCode);
    }

    private static void example3() throws IOException, InterruptedException {
        Process p1 = new ProcessBuilder("grep", "foo", "file.txt")
                .redirectError(ProcessBuilder.Redirect.INHERIT)
                .start();

        Process p2 = new ProcessBuilder("wc", "-l")
                .redirectOutput(ProcessBuilder.Redirect.INHERIT)
                .redirectError(ProcessBuilder.Redirect.INHERIT)
                .start();

        try (InputStream in = p1.getInputStream();
             OutputStream out = p2.getOutputStream()) {
            in.transferTo(out);
        }

        int ec1 = p1.waitFor();
        int ec2 = p2.waitFor();
        System.out.println("grep exit = " + ec1);
        System.out.println("wc exit = " + ec2);
    }
}
