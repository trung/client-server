package client;

import org.mdkt.sample.lib.HelloClient;

public class Main {

	public static void main(String[] args) {
		HelloClient client = HelloClient.Builder.newBuilder().enableSsl(false).servers("localhost:8080").build();
		System.out.println(client.hello());
	}

}
