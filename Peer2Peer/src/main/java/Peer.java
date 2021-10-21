/**
 * Peer.java - Main class in p2p application
 *
 * Programmer: Clint Skouson 2020
 */

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;

import org.json.*;

/**
 * This is the main class for the peer2peer program.
 * It starts a client with a username and port. Next the peer can decide who to listen to. 
 * So this peer2peer application is basically a subscriber model, we can "blurt" out to anyone who wants to listen and 
 * we can decide who to listen to. We cannot limit in here who can listen to us. So we talk publicly but listen to only the other peers
 * we are interested in. 
 * 
 */

public class Peer {
	static String username;
	private BufferedReader bufferedReader;
	private ServerThread serverThread;

	//game vars
	static MutableBoolean isHost = new MutableBoolean(false);
	static MutableBoolean gameActive = new MutableBoolean(false);
	static String currentAnswer = "";
	static Winner winner = new Winner();
	

	public Peer(BufferedReader bufReader, String username, ServerThread serverThread){
		this.username = username;
		this.bufferedReader = bufReader;
		this.serverThread = serverThread;
	}


	/**
	 * Main method saying hi and also starting the Server thread where other peers can subscribe to listen
	 *
	 * @param args[0] username
	 * @param args[1] port for server
	 */
	public static void main (String[] args) throws Exception {

		if(args.length != 2){
			System.out.println("Incorrect args");
			System.exit(0);
		}

		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
		String username = args[0];
		System.out.println("Hello " + username + " and welcome! Your port will be " + args[1]);

		// starting the Server Thread, which waits for other peers to want to connect
		ServerThread serverThread = new ServerThread(args[1], currentAnswer, isHost, username, winner);
		serverThread.start();
		Peer peer = new Peer(bufferedReader, args[0], serverThread);
		peer.updateListenToPeers();
	}
	

	/**
	 * User is asked to define who they want to subscribe/listen to
	 * By default we listen to no one
	 *
	 */
	public void updateListenToPeers() throws Exception {
		System.out.println("> Who do you want to listen to? Enter host:port");
		String input = bufferedReader.readLine();
		System.out.println(input);
		String[] setupValue = input.split(" ");
		for (int i = 0; i < setupValue.length; i++) {
			String[] address = setupValue[i].split(":");
			Socket socket = null;
			try {
				socket = new Socket(address[0], Integer.valueOf(address[1]));
				new ClientThread(socket, username, serverThread).start();
			} catch (Exception c) {
				if (socket != null) {
					socket.close();
				} else {
					System.out.println("Cannot connect, wrong input");
					System.out.println("Exiting: I know really user friendly");
					System.exit(0);
				}
			}
		}

		beginGame();
	}
	

	/**
	 * Client waits for user to input their message or quit
	 *
	 * @param bufReader bufferedReader to listen for user entries
	 * @param username name of this peer
	 * @param serverThread server thread that is waiting for peers to sign up
	 */
	public void beginGame() throws Exception {
		try {
			
			System.out.println("> You can now start chatting (exit to exit)\nType $ to start game...");
			
			while(true) {

				String message = bufferedReader.readLine();
				
				if (message.equals("exit")) {
					System.out.println("bye, see you next time");
					break;
				} else if(message.equals("$")){ //start game ////////////////////////////////////////////////////////////////////////////////
					gameActive.t();
					
					System.out.println("Do you want to be host?");//determine host
					message = bufferedReader.readLine();

					if(message.equals("yes")){ 
						System.out.println("  --You are host--");
						System.out.println("Enter q to send your question");
						isHost.t();
					} else{
						System.out.println("  --You are pawn--");
						isHost.f();
					}

				} else if(gameActive.getVal() == true) { //in game /////////////////////////////////////////////////////////////////////////////////
					//if host, send question
					if(isHost.getVal() == true){
						serverThread.sendQuestion();
					} else{ //currently for answers
						serverThread.sendMessage("{'username': '"+ username +"','payload':'" + message + "'}");
					}

				} else{ //no game active; just chatting pre game;
					serverThread.sendMessage("{'username': '"+ username +"','payload':'" + message + "'}");
				}

			}//end game loop
			System.exit(0);
		
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
