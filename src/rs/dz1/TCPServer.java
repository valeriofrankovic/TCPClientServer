package rs.dz1;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class TCPServer implements Runnable{
	
	private static final int NUMBER_OF_THREADS = 10;
	
	private ServerSocket serverSocket;
	private final ExecutorService executor;
	private int port;
	private final AtomicBoolean runningFlag;
	private final AtomicInteger activeConnections;
	private ActiveTimeCounter activeTimeCounter;
	private ArrayList<Readings> measurements;
	
	public TCPServer(ArrayList<Readings> measurements, ActiveTimeCounter activeTimeCounter) {
		this.activeTimeCounter = activeTimeCounter;
		this.executor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);
		this.activeConnections = new AtomicInteger(0);
		this.runningFlag = new AtomicBoolean(true);
		this.measurements = measurements;
		this.serverSocket = null;
		this.port = 0;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		try(ServerSocket serverSocket = new ServerSocket(0)) {
			this.serverSocket = serverSocket;
			System.out.println(serverSocket.getInetAddress().getHostAddress());
			this.port = serverSocket.getLocalPort();
			System.out.println("Port je " + this.port);
			serverSocket.setSoTimeout(500);
			System.out.println("Waiting for clients");
			
			//start the main loop for accepting client requests 
            loop();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void loop() {
        while (runningFlag.get()) {
            try {
                // create a new socket, accept and listen for a connection to be
                // made to this socket
                Socket clientSocket = serverSocket.accept();/*ACCEPT*/
                
                // execute a tcp request handler in a new thread
                Runnable worker = new Worker(clientSocket, runningFlag, activeConnections, measurements, activeTimeCounter);
                executor.execute(worker);
                activeConnections.set(activeConnections.get() + 1);
            } catch (SocketTimeoutException ste) {
                // do nothing, check runningFlag flag
            } catch (IOException ex) {
                System.err.println("Exception caught when waiting for a connection: " + ex);
            }
        }
    }
	
	public void shutdown() {
		executor.shutdownNow();
    }
	
	
	
	/**
	 * @return the serverSocket
	 */
	public ServerSocket getServerSocket() {
		return this.serverSocket;
	}

	public void setRunningFlag(boolean running) {
        this.runningFlag.set(running);
    }

    public boolean getRunningFlag() {
        return runningFlag.get();
    }


}
