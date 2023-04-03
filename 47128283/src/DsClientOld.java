import java.net.*;
import java.io.*;

public class DsClientOld {
    Socket s;
    DataOutputStream outStream;
    BufferedReader inputStream;

    String lastMessage = "foo";

    String maxType = new String();
    int noOfServers = 0;
    String maxRecord = "";
    String[] maxRecordArray = {"0","0","0","0","0","0","0","0","0","0"};
    // Constructor

    public DsClientOld(String address, int port) throws Exception {
        s = new Socket(address, port);
        outStream = new DataOutputStream(s.getOutputStream());
        inputStream = new BufferedReader(new InputStreamReader(s.getInputStream()));

    }

    public static void main(String[] args) throws Exception {
        DsClientOld c = new DsClientOld("127.0.0.1", 50000);
        c.byClient();

        c.s.close();
        c.inputStream.close();
        c.outStream.close();
    }

    public void byClient() throws Exception {
        sendMessage("HELO"); //send HELO
        recieveMessage(); //recieve OK
        sendMessage("AUTH" + System.getProperty("user.name")); //send AUTH along with the user
        recieveMessage(); //recieve OK

        boolean firstLoop = true;
        int currentServerID = 0;
        while(lastMessage.contains("NONE") == false) {
            sendMessage("REDY"); //send REDY
            String currentMessage = recieveMessage(); //recieve a message
            if(currentMessage.contains("NONE")) break;
            if(currentMessage.contains("JCPL")) {
                System.out.println("JCPL " + currentMessage);
                continue;
            }
            String[] currentMessageArray = currentMessage.split(" ");
            if(firstLoop) { //Identify the largest server type; you may do this only once
                getLargest();
                firstLoop = false;
            } 
            if(currentMessage.contains("JOBN")) { //if the message recieved at step 10 is of type JOBN
                System.out.println(currentMessage);
                sendMessage("SCHD " + currentMessageArray[2]+ " " + maxType + " " + currentServerID%noOfServers); //not complete
                recieveMessage();
                currentServerID++;
            }

            lastMessage = currentMessage;
        }
        sendMessage("QUIT");
        recieveMessage();

    }

    public void getLargest() throws Exception {
        sendMessage("GETS All"); //send GETS All
        String dataString = recieveMessage(); // recieve DATA
        String[] dataArray = dataString.split(" ");
        int nRecs = Integer.parseInt(dataArray[1]);
        int recSize = Integer.parseInt(dataArray[1]);
        sendMessage("OK"); //send OK

        for(int i = 0;i<nRecs;i++) {
            String currentRecord = recieveMessage(); //recieve each record
            String[] currentRecordArray = currentRecord.split(" ");
            //for(int j = 0;j<currentRecordArray.length;j++) {
            //    System.out.print(currentRecordArray[j] + " ");
            //}
            //System.out.println("");
            if((Integer.parseInt(currentRecordArray[4])>Integer.parseInt(maxRecordArray[4]))||(i==0)) { //keep track of largest server type
                maxRecord = currentRecord;
                maxRecordArray = currentRecordArray;
                maxType = maxRecordArray[0];
                noOfServers = 1;
            } else {
                noOfServers++; //and the number of servers of that type
            }
        }
        System.out.println("max " + maxRecord);
        sendMessage("OK"); //send OK
        recieveMessage(); //recieve .
    }


    public void sendMessage(String message) throws Exception {
        this.outStream.write((message + "\n").getBytes("UTF-8"));
    }
    public String recieveMessage() throws Exception {
        return this.inputStream.readLine();
    }
}