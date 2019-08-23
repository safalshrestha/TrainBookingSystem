/**
 * @(#)Password.java
 *
 *
 * @author
 * @version 1.00 2012/12/17
 */

import java.util.*;
import java.io.*;

class TFObject implements java.io.Serializable {
    String option;
    int sno;
    //Option = login
    String username;
    String password;
    //Option = book
    String to;
    String from;
    String date;
    int maxHop;
    int seatCapacity;
    int routeID;
    int duration;
    double price;
    
    //Option = check or cancel
    String bookingRef;
    String name;
    int age;
    

    

    public TFObject() {
        option = "";username = ""; password = ""; 
        to=""; from=""; date=""; bookingRef=""; 
        name=""; routeID = 0; age = 0; maxHop = 1; seatCapacity = 0; duration = 0; price = 0.0; }
    
    public TFObject(String n, String p) {
        username = n;
        password = p;
    }
    
    public TFObject(String o, String n, String p) {
        option = o;
        username = n;
        password = p;
    }
    
    public TFObject(String o, String f, String t, String d, int m) {
        option = o;
        to = t;
        from = f;
        date = d;
        maxHop = m;
    }
    
    public TFObject(String o, String br, String n, int a) {
        option = o;
        bookingRef = br;
        name = n;
        age = a;
    }
    
    public TFObject(String br, String f, String t, int rID, String d) {
        bookingRef = br;
        to = t;
        from = f;
        routeID = rID;
        date = d;
    }
    

    public TFObject(int s, int r,  String f, String t, int d, double p, String dt, int h) {
        sno = s;
        routeID = r;
        from = f;
        to = t;
        duration = d;
        price = p   ;
        date = dt;
        maxHop = h;
    }
    
    
    public TFObject(int s, String n, int a) {
        sno = s;
        name = n;
        age = a;
    }
    
    public boolean equals(Object p) {
        TFObject pw = (TFObject)p;
        return username.equals(pw.username) && password.equals(pw.password);
    }
    
    public String getUser() { return username; }
    public String getOption() { return option; }
    public String getBookingRef(){ return bookingRef; }
    public String getName() { return name; }
    public String getTo() { return to; }
    public String getFrom() { return from; }
    public int getRouteID() {return routeID;}
    public String getDate() { return date; }
    public int getAge() { return age; }
    public int getMaxHop() { return maxHop; }
    public int getDuration() { return duration; }
    public double getPrice() { return price; }
    public int getSno() { return sno; }
    
}