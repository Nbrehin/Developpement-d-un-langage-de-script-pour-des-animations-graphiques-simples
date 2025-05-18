package ihm;

import java.io.IOException;

public interface Client {

	void send(Object message) throws IOException;

	void startListening() throws ClassNotFoundException, IOException;

	void close() throws IOException;
}