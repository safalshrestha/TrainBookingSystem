/**
 * @(#)LoginPassword.java
 *
 *
 * @author
 * @version 1.00 2012/12/17
 */

//THE CLIENT

import java.io.*;
import java.net.*;
import java.util.*;

class BookingClient {
    private final static int port = 1234;
    public static void main(String args[]) {
        try {
                Socket socket;
                socket = new Socket(InetAddress.getLocalHost(), port);
                DataInputStream in = new DataInputStream(socket.getInputStream());
                DataOutputStream dout = new DataOutputStream(socket.getOutputStream());

                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
                int quitCounter = 0;

                boolean valid = false;
                while (!valid) {
                    Scanner keyIn = new Scanner(System.in);
                    System.out.print("User name:");
                    String user = keyIn.next();
                    System.out.print("Password:");
                    String pw = keyIn.next();
                    TFObject pWord = new TFObject("login",user,pw);
                    
                    //write object and flush any buffered data
                    out.writeObject(pWord);
                    out.flush();
                    //wait for reply from server
                    valid = in.readBoolean(); 
                    int option = 0;
                    if (valid) {
                        System.out.println("");
                        System.out.flush();
                        System.out.println("Connection Established");
                        System.out.println();
                        while (option != 4) {
    
                           displayMenu(user);
                           option = keyIn.nextInt();
                           if (option == 1) {
                               checkBooking(keyIn, ois, out, in);
                           }
                           else if (option == 2) {
                               cancelBooking(keyIn, ois, out, in);
                           }
                           else if (option == 3) {
                               findBooking(keyIn, ois, out, dout, in);
                           }
                           System.out.println();
                           System.out.println();
                           System.out.println();

                          
                        }
                        System.out.println("Exiting.....");
                        System.exit(1);
                    }
                    else {
                        System.out.println("Unauthorised User! Access Denied! Try Again Later");
                        quitCounter++;
                        //socket.close();
                       // in.close();
                       // out.close();
                   
                    }
                    
                    //quit counter to unauthorized user out of server
                    if (quitCounter == 2) {
                        System.out.println("Last Try before System Exits");
                    }
                    else if(quitCounter>2) {
                        System.exit(1);
                    }
                        
            }
               // System.out.println("Welcome "+ user+ ", please select option:");
                
                //socket.close();
                //in.close();
                //out.close();           
        }
        catch (IOException e) {
            System.out.println(e);
        }
    }
    
    public static void displayMenu(String user) {
        System.out.println("Welcome, "+user);
        System.out.println();
        System.out.println("Please Select Option");
        System.out.println("---------------------------------");
        System.out.println("1. Check Existing Booking");
        System.out.println("2. Cancel Booking");
        System.out.println("3. Book tickets");
        System.out.println("4. Exit");
        System.out.println();
        System.out.println("Enter Option [1-4] ");
    }
    
    public static void checkBooking(Scanner keyIn, ObjectInputStream ois, ObjectOutputStream out,DataInputStream in) {
        try {
            System.out.println("");
            String bookingRef, name;
            int age;
            System.out.println("Check Exisiting Booking");
            System.out.println("---------------------------------");
            System.out.println("Enter Booking Reference");
            bookingRef = keyIn.next();
            
            System.out.println("Enter Name");
            name = keyIn.next();
            
            System.out.println("Your Age");
            age = keyIn.nextInt();
            
            TFObject check = new TFObject("check", bookingRef, name, age);
                        
            out.writeObject(check);
            out.flush();
            int num = in.readInt();
            if (num == 0) {
                System.out.println("Cannot find any booking confirmation with that detail");
            }
            else {
                System.out.println(num+" Records Found");
                System.out.format("%-15s%-15s%-15s%-15s%-20s", "Booking Ref",  "From", "To", "Route ID", "Date");
                for (int i=1; i<=num; i++) { 
                    check = (TFObject)ois.readObject(); 
                    System.out.println();
                    System.out.format("%-15s%-15s%-15s%-15s%-20s", check.getBookingRef(), check.getFrom(), check.getTo(), check.getRouteID(), (String)check.getDate());   
                }
            }
        }
        catch (IOException e) {
            System.out.println(e);
        }
        catch (ClassNotFoundException e) {
            System.out.println(e);
        }
        
    }
    
    public static void cancelBooking(Scanner keyIn, ObjectInputStream ois, ObjectOutputStream out, DataInputStream in) {
         try {
            System.out.println("");
            String bookingRef, name;
            int age;
            System.out.println("Cancel Exisiting Booking");
            System.out.println("---------------------------------");
            System.out.println("Enter Booking Reference");
            bookingRef = keyIn.next();
            
            System.out.println("Enter Name");
            name = keyIn.next();
            
            System.out.println("Your Age");
            age = keyIn.nextInt();
            TFObject cancel = new TFObject("cancel", bookingRef, name, age);
                        
            out.writeObject(cancel);
            out.flush();
            System.out.println();
           
            if (in.readBoolean()) 
                System.out.println("Booking Cancelled");
            else {
                System.out.println("Error finding booking Information. Please try again!");
            }
        }
        catch (IOException e) {
            System.out.println(e);
        }
        
    }
    
    public static void findBooking(Scanner keyIn, ObjectInputStream ois, ObjectOutputStream out, DataOutputStream dout, DataInputStream in) {
        try {
            System.out.println("");
            String bookingRef, name;
            int age, maxHop, ticketChoice;
            String to, from, date;
            System.out.println("Make a Booking");
            System.out.println("---------------------------------");
            System.out.println("Enter date, YYYY-MM-DD");
            date = keyIn.next();
            
            System.out.println("From:");
            from = keyIn.next();
            
            System.out.println("To: ");
            to = keyIn.next();
            
            System.out.println("Maximum stopover/hop?");
            maxHop = keyIn.nextInt();
            
            TFObject make = new TFObject("find", from, to, date, maxHop);
                        
            out.writeObject(make);
            out.flush();

            int i = 0;
            ArrayList<TFObject> TFList = (ArrayList<TFObject>)ois.readObject();
            if (TFList.size() > 0) {
                System.out.format("%-15s%-15s%-15s%-15s%-15s%-15s", "Sno",  "Route ID", "From", "To", "Duraton", "Price");
                System.out.println();
                while (i < TFList.size()) {
                    if (TFList.get(i).getMaxHop() == 1) {
                        System.out.println();
                        System.out.format("%-15s%-15s%-15s%-15s%-15s%-15s", TFList.get(i).getSno(), TFList.get(i).getRouteID(), TFList.get(i).getFrom(),TFList.get(i).getTo(),TFList.get(i).getDuration(),TFList.get(i).getPrice());
                        System.out.println();
                    }
                    else if ((TFList.get(i).getMaxHop() == 2)) {
                        System.out.println();
                        System.out.format("%-15s%-15s%-15s%-15s%-15s%-15s", TFList.get(i).getSno(), TFList.get(i).getRouteID(), TFList.get(i).getFrom(),TFList.get(i).getTo(),TFList.get(i).getDuration(),TFList.get(i).getPrice());
                        i++;
                        System.out.println();
                        System.out.format("%-15s%-15s%-15s%-15s%-15s%-15s", TFList.get(i).getSno(), TFList.get(i).getRouteID(), TFList.get(i).getFrom(),TFList.get(i).getTo(),TFList.get(i).getDuration(),TFList.get(i).getPrice());
                        System.out.println();
                        System.out.format("%-15s%-15s%-15s%-15s%-15s%-15s", "Total:", TFList.get(i-1).getRouteID()+ " AND " + TFList.get(i).getRouteID(), TFList.get(i-1).getFrom(),TFList.get(i).getTo(),TFList.get(i).getDuration() + TFList.get(i-1).getDuration(), TFList.get(i-1).getPrice() + TFList.get(i).getPrice());
                        System.out.println();
                    }
                    i++;
                }
                ticketChoice = TFList.get(i-1).getSno();
                System.out.println("---------------------------------------------------------------------------------------------");
                System.out.println("Enter the Serial no of Ticket you want to book");
                ticketChoice = keyIn.nextInt();
                
                while (ticketChoice > TFList.get(i-1).getSno()) {
                    System.out.println("---------------------------------------------------------------------------------------------");
                    System.out.println("That serial number doesn't exists, please enter a valid number");
                    ticketChoice = keyIn.nextInt();
                }
                
                System.out.println("Enter name of person travelling");
                name = keyIn.next();
                System.out.println("Enter age of person travelling");
                age = keyIn.nextInt();
                System.out.println();
                
                make = new TFObject(ticketChoice, name, age);
                out.writeObject(make);
                out.flush();
                int bRef = in.readInt();
                if (bRef == 0) {
                    System.out.println("Ticket Booking Unsuccessful, try booking again!");
                }
                else {
                    System.out.println("Ticket Succeessfully Booked! Your Booking Ref: "+bRef);
                }
                
            }
            else if (TFList.size() == 0) {
                System.out.println("Routes are unavailable or fully booked");

            }

            
        }
        catch (IOException e) {
            System.out.println(e);
        }
        catch (ClassNotFoundException e) {
            System.out.println(e);
        }
    }
    
}