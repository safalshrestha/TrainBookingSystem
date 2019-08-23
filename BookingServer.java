/**
 * @(#)PasswordServer.java
 *
 *
 * @author
 * @version 1.00 2012/12/4
 */
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;


class BookingServer {

    final static int portNum = 1234;
    final static int numThreads = 10;
    static ExecutorService pool;
    public static void main(String[]args) {
        ArrayList<TFObject> passwords = new ArrayList<TFObject>();
        loadPasswords(passwords);
        pool = Executors.newFixedThreadPool(numThreads);
        System.out.println("Server running ... ");
       
        try {
            ServerSocket servesock = new ServerSocket(portNum);
            //for service requests on port portSqrt
            while (true) {
                //wait for a service request on port portSqrt
                Socket socket = servesock.accept();
                //submit request to pool
                pool.submit(new Initialize(socket, passwords));
            }
        } catch(IOException e) {}
    }

    static void loadPasswords(ArrayList<TFObject> p) {
        String s;
        StringTokenizer t;
        try {
            FileReader fr = new FileReader("Password.txt");
            BufferedReader in = new BufferedReader(fr);
            s = in.readLine();
            while (s != null) {
                t = new StringTokenizer(s);
                TFObject pWord = new TFObject(t.nextToken(), t.nextToken());
                p.add(pWord);
                s = in.readLine();
            }
            in.close();
        }
        catch(IOException e) {
            System.out.println(e.getMessage());
        }
    }
}

class Initialize implements Runnable {
    Socket socket;
    ArrayList<TFObject> pWords;
    public Initialize(Socket s, ArrayList<TFObject> p) {
        socket = s;
        pWords = p;
    }
    public void run() {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(socket.getOutputStream());
            oos.flush();
            DataInputStream din = new DataInputStream(socket.getInputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
            while(true) {
                
                TFObject obj = (TFObject)in.readObject();
                
                if (obj.getOption().equals("login")) {
                    if (pWords.contains(obj)) {//password found 
                        System.out.println("Server Stat:" + obj.getUser() + " Connected");
                        out.writeBoolean(true);
                    }
                    else
                        out.writeBoolean(false);
                }
                
                else if (obj.getOption().equals("check")) {
                    checkBooking(obj, out, oos);
                }
                else if (obj.getOption().equals("cancel")) {
                    cancelBooking(obj,  out);
                }
                else if (obj.getOption().equals("find")) {
                    findBooking(obj, out, oos, in);
                }
            }
            //socket.close();
        }
        catch (IOException e) {}
        catch (ClassNotFoundException e1) {}
    }
    
    public void checkBooking(TFObject t, DataOutputStream out, ObjectOutputStream oos) {
        TFObject tfObj = new TFObject();
        tfObj = t;
        
        int numRes = Database.checkNumBooking(tfObj);
        
        try {
            out.writeInt(numRes);
            for (int i= 1; i<=numRes; i++) {
                oos.writeObject(Database.checkBooking(tfObj, i));
            }
        }
        catch (IOException e) {}

        
    }
    
    public void cancelBooking(TFObject t, DataOutputStream out) {
        TFObject tfObj = new TFObject();
        tfObj = t;
       
        
        try {
            out.writeBoolean(Database.cancelBooking(tfObj));
        }
        catch (IOException e) {}

    }
    
    public void findBooking(TFObject t, DataOutputStream out, ObjectOutputStream oos, ObjectInputStream in) {
        TFObject tfObj = new TFObject();
        tfObj = t;
        ArrayList<TFObject> TFList = new ArrayList<TFObject>();
        TFList = Database.findRoute(tfObj);
        
        try {
            if (TFList.size() == 0) {
                oos.writeObject(TFList);
                oos.flush();
            }
            else {
                oos.writeObject(TFList);
                oos.flush();
                tfObj = (TFObject)in.readObject();
                int bookingRef = Database.makeBooking(TFList, tfObj);
                out.writeInt(bookingRef);
            }
        }
        catch (IOException e) {}
        catch (ClassNotFoundException e) {}

        
    }
    
    
}