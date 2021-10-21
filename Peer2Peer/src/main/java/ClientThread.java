import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import org.json.*;

/**
 * Client 
 * This is the Client thread class, there is a client thread for each peer we are listening to.
 * We are constantly listening and if we get a message we print it. 
 */

public class ClientThread extends Thread {
	private BufferedReader bufferedReader;
	String username;
	//String currentAnswer;
	//MutableBoolean isHost;
	//Winner winner;
	ServerThread serverThread;

	

	public ClientThread(Socket socket, String user, ServerThread serverThread) throws IOException {
		bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		this.username = user;
		this.serverThread = serverThread;
	}


	public void run() {
		while (true) {
			try {
			    JSONObject json = new JSONObject(bufferedReader.readLine());

				if(serverThread.isHost.getVal() == true){ //if host, get answer responses here
					System.out.println("[" + json.getString("username")+"] " + json.getString("payload"));
					//System.out.println("DEBUG: current answer is "+ serverThread.currentAnswer);
					
					//check and handle if correct answer
					if(json.getString("payload").equals(serverThread.currentAnswer)){
						System.out.println(json.getString("username") +" guessed the correct answer!!!");
						serverThread.winner.makeWinner(json.getString("username"));
						//System.out.println("DEBUG: winner is "+ serverThread.winner.getWinner());
						serverThread.announceWinner();
					}

				} else{ //if pawn get question or other pawn responses here
					if(json.has("header") == false){
						System.out.println("[" + json.getString("username")+"] " + json.getString("payload"));
					} else{
						JSONObject header = json.getJSONObject("header");
						//if win packet
						if(header.getString("type").equals("win")){
							System.out.println("["+ header.getString("username") +"] The winner is "+ json.getString("payload"));
							//if i am the winner, make myself host
							if(json.getString("payload").equals(username)){
								serverThread.isHost.t();
								System.out.println("-- I am host now (Enter q to send question) --");
							}
						} else if(header.getString("type").equals("question")){
							System.out.println("["+ header.getString("username") +"] Question: "+ json.getString("payload"));
						} else{
							System.out.println("invalid json pass");
						}
					}	
				}

			    //System.out.println("[" + json.getString("username")+"]: " + json.getString("message"));
				//currentAnswer = json.getString("message");
			} catch (Exception e) {
				interrupt();
				break;
			}
		}
	}

}
