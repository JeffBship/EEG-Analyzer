package eeg_analyzer;


/**
 * A Simple Java TCP Server and TCP Client. 
 * 
 * https://systembash.com/a-simple-java-tcp-server-and-tcp-client/
 * originally from "Computer Networking: A Top Down Approach", 
 * @author Kurose and Ross
 */

import java.io.*;
import java.net.*;

public class TCPClient {
    
 public static void main(String argv[]) throws Exception {
  String sentence;
  String modifiedSentence;
  BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
  Socket clientSocket = new Socket("localhost", 6789);
  DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
  BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
  sentence = inFromUser.readLine();
  outToServer.writeBytes(sentence + '\n');
  modifiedSentence = inFromServer.readLine();
  System.out.println("FROM SERVER: " + modifiedSentence);
  clientSocket.close();
 }
}