package rs.dz1;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class TCPClient {

	private Socket clientSocket;
	private PrintWriter outToServer;
	private BufferedReader inFromServer;
	
	public TCPClient(String serverName, int port) {
		try {
			this.clientSocket = new Socket(serverName, port);
			this.outToServer = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()), true);
			this.inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void send(String message) {
		this.outToServer.println(message);
		this.outToServer.flush();
	}
	
	public String receive() {
		String rcvString = null;
		try {
			rcvString = this.inFromServer.readLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return rcvString;
	}
	
}
