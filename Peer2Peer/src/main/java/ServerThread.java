import java.io.IOException;
import java.io.PrintWriter;
import java.io.FileInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;
import java.util.Random;

import org.json.*;

/**
 * SERVER
 * This is the ServerThread class that has a socket where we accept clients contacting us.
 * We save the clients ports connecting to the server into a List in this class. 
 * When we wand to send a message we send it to all the listening ports
 */

public class ServerThread extends Thread{
	private ServerSocket serverSocket;
	private Set<Socket> listeningSockets = new HashSet<Socket>();
	String username;
	String currentAnswer;
	MutableBoolean isHost;
	boolean[] usedQuestions = new boolean[15];
	JSONArray jArr;
	Winner winner;

	
	public ServerThread(String portNum, String currentAnswer, MutableBoolean isHost, String user, Winner winner) throws IOException {
		serverSocket = new ServerSocket(Integer.valueOf(portNum));
		this.currentAnswer = currentAnswer;
		this.isHost = isHost;
		this.username = user;
		this.winner = winner;

		//setup list of questions
		try{
			FileInputStream questionIn = new FileInputStream("questions.json");
			this.jArr = new JSONArray(new JSONTokener(questionIn));
		} catch (Exception ex){
			ex.printStackTrace();
		}
	}
	

	/**
	 * Starting the thread, we are waiting for clients wanting to talk to us, then save the socket in a list
	 */
	public void run() {
		try {
			while (true) {
				Socket sock = serverSocket.accept();
				listeningSockets.add(sock);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	

	/**
	 * Sending the message to the OutputStream for each socket that we saved
	 */
	void sendMessage(String message) {
		try {
			for (Socket s : listeningSockets) {
				PrintWriter out = new PrintWriter(s.getOutputStream(), true);
				out.println(message);
		     }
		} catch(Exception e) {
			e.printStackTrace();
		}
	}


	public void sendQuestion(){
		try{
			//pull random question
			while(true){
				Random rand = new Random();
				int pos = rand.nextInt(15);
				if(usedQuestions[pos] != true){
					JSONObject o = jArr.getJSONObject(pos); //pull question object
					System.out.println("SENDING: " + o.getString("question"));
					currentAnswer = o.getString("answer");
					//System.out.println("DEBUG: current answer set to "+ currentAnswer);

					//fill protocol packet
					JSONObject sendJson = new JSONObject();//main
					JSONObject header = new JSONObject(); //header
					header.put("type", "question");
					header.put("role", "host");
					header.put("username", username);
					sendJson.put("payload", o.getString("question"));
					sendJson.put("header", header);

					//send question to all pawns					
					for(Socket s : listeningSockets){
						PrintWriter out = new PrintWriter(s.getOutputStream(), true);
						out.println(sendJson.toString());//send json packet as String
					}

					//end question round
					break;
				}
			}
			
		} catch (Exception ex){
			ex.printStackTrace();
		}
	}


	public void announceWinner(){
		try{
			if(winner.doWeHaveWinner()==true){
				//winner json packet
				JSONObject winJson = new JSONObject();
				JSONObject winHeader = new JSONObject();				
				winHeader.put("type", "win");
				winHeader.put("role", "host");
				winHeader.put("username", username);
				winJson.put("payload", winner.getWinner());
				winJson.put("header", winHeader);

				//give up host role
				isHost.f();	

				//announce the winner to pawns
				for(Socket s : listeningSockets){
					PrintWriter out = new PrintWriter(s.getOutputStream(), true);
					out.println(winJson.toString());
				}
			} else{
				System.out.println("No winner found in serverthread object..........");
			}
		} catch (Exception ex){
			ex.printStackTrace();
		}

	}

}//end class
