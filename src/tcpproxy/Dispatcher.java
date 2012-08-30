package tcpproxy;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

public interface Dispatcher {

    void start() throws IOException, ExecutionException, InterruptedException;

}
