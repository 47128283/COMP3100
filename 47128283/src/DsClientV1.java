import java.net.*;
import java.io.*;

public class DsClient {
    Socket s;
    DataOutputStream outStream;
    BufferedReader inputStream;
    String[] firstMax = new String[10];
    String[] currentServer = new String[0];
    int groupCount = 1;
    String groupType = "";
    String temp = new String();

    // Constructor

    public DsClient(String address, int port) throws Exception {
        s = new Socket(address, port);
        outStream = new DataOutputStream(s.getOutputStream());
        inputStream = new BufferedReader(new InputStreamReader(s.getInputStream()));

    }

    public static void main(String[] args) throws Exception {
        DsClient c = new DsClient("127.0.0.1", 50000);
        c.byClient();

        c.s.close();
        c.inputStream.close();
        c.outStream.close();
    }

    public void byClient() throws Exception {
        sendMessage("HELO");
        System.out.println(this.inputStream.readLine()); //recieve ok
        String username = System.getProperty("user.name");
        sendMessage("AUTH " + username);
        System.out.println(this.inputStream.readLine()); //recieve ok
        String lastMessage = "";
        String currentMessage = lastMessage;
        boolean isFirstLoop = true;
        int s = 0;
        while (lastMessage.equals("NONE") == false) {
            sendMessage("REDY");
            currentMessage = this.inputStream.readLine(); //recieve message
            System.out.println(currentMessage);
            if(currentMessage.equals("NONE")) break;
            String[] currentJob = currentMessage.split(" "); //split the message
            //System.out.print("Server says: ");
            for(int i = 0;i<currentJob.length;i++) {
                //System.out.print(currentJob[i] + " ");
            }
            //System.out.println("");

            if(isFirstLoop) {
                findLargest();
                isFirstLoop = false;
            }
            sendMessage("OK");
            if(currentJob[0].equals("JOBN")) {
                //System.out.print("schd reached\n");
                sendMessage("SCHD " + currentJob[2] + " " + groupType + " " + s);
                System.out.println(this.inputStream.readLine()); //recieve ok
                s++;
                if(s>groupCount) {
                    s = 0;
                }
            }
            lastMessage = currentMessage;
        }

        sendMessage("QUIT");
    }

    public void findLargest() throws Exception{
        sendMessage("GETS All");
        
        temp = this.inputStream.readLine();
        String[] dataReadOut = temp.split(" ");
        for (int i = 0; i < dataReadOut.length; i++) {
            //System.out.print(dataReadOut[i] + " ");
        }
        System.out.print("\n");
        sendMessage("OK");

        temp = this.inputStream.readLine();
        firstMax = temp.split(" ");
        
        for (int i = 0; i < firstMax.length; i++) {
            //System.out.println(firstMax[i] + " ");
        }
        System.out.print("yeet\n");
        for (int i = 1; i < Integer.parseInt(dataReadOut[1]) - 1; i++) {
            temp = this.inputStream.readLine();
            String[] current = temp.split(" ");
            for (int j = 0; j < current.length; j++) {
                //System.out.print(current[j] + " ");
            }
            
            System.out.print("\n");
            if (Integer.parseInt(current[4]) > Integer.parseInt(firstMax[4])) {
                firstMax = current;
                groupCount = 1;
                groupType = current[0];
            } else {
                groupCount++;
            }
        }
        currentServer = firstMax;
        System.out.print("First Biggest ");
        for (int i = 0; i < firstMax.length; i++) {
            System.out.print(firstMax[i] + " ");
        }
        System.out.print("\n");
        System.out.println(groupCount);
        System.out.println(groupType);
        
    }

    public void sendMessage(String message) throws Exception {
        this.outStream.write((message + "\n").getBytes("UTF-8"));
    }
}