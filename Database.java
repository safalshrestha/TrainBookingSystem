import java.sql.*;
import java.util.ArrayList;
import java.util.*;

public class Database {
   // JDBC driver name and database URL
   static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
   static final String DB_URL = "jdbc:mysql://localhost/trainreservation";

   //  Database credentials
   static final String USER = "root";
   static final String PASS = "";
   
   //CHECK NUMBER OF BOOKINGS
   public static int checkNumBooking(TFObject tf) {
       Connection conn = null;
       Statement stmt = null;
       try{
          //STEP 2: Register JDBC driver
          Class.forName("com.mysql.jdbc.Driver");
    
          //STEP 3: Open a connection
          conn = DriverManager.getConnection(DB_URL,USER,PASS);
    
          //STEP 4: Execute a query

          String sql;
          int count = 0;
          stmt = conn.createStatement();

          sql = "SELECT COUNT(*)as COUNT FROM persondetail WHERE bookingRef = '"+tf.getBookingRef()+"' AND age="+tf.getAge()+" AND name='"+tf.getName()+ "'";
          ResultSet rs = stmt.executeQuery(sql);
          rs.next();
          if (rs.getInt("COUNT") == 1) {
                 sql = "SELECT COUNT(*) as COUNT FROM bookinginfo WHERE bookingRef = '"+tf.getBookingRef()+"'";
                 ResultSet rs1 = stmt.executeQuery(sql);
                 rs1.next();
                 count = rs1.getInt("COUNT");
                 rs1.close();
          }
          rs.close();
          stmt.close();
          conn.close();
          return count;
       }catch(SQLException se){
          //Handle errors for JDBC
          se.printStackTrace();
       }catch(Exception e){
          //Handle errors for Class.forName
          e.printStackTrace();
       }finally{
          //finally block used to close resources
          try{
             if(stmt!=null)
                stmt.close();
          }catch(SQLException se2){
          }// nothing we can do
          try{
             if(conn!=null)
                conn.close();
          }catch(SQLException se){
             se.printStackTrace();
          }//end finally try
       }//end try
       return 0;
    }//end main
   
    //CHECK BOOKINGS
   public static TFObject checkBooking(TFObject tf, int setNo) {
       Connection conn = null;
       Statement stmt = null;
       try{
          //STEP 2: Register JDBC driver
          Class.forName("com.mysql.jdbc.Driver");
    
          //STEP 3: Open a connection
          conn = DriverManager.getConnection(DB_URL,USER,PASS);
    
          //STEP 4: Execute a query
          stmt = conn.createStatement();
          String sql;
          sql = "SELECT COUNT(*) as COUNT FROM persondetail WHERE bookingRef = '"+tf.getBookingRef()+"' AND age="+tf.getAge()+" AND name='"+tf.getName()+ "'";
          ResultSet rs = stmt.executeQuery(sql);
          rs.next();
          if (rs.getInt("COUNT") == 1) {
                 sql = "SELECT * FROM bookinginfo WHERE bookingRef = '"+tf.getBookingRef()+"'";
                 ResultSet rs1 = stmt.executeQuery(sql);
                 rs1.next();
                 int routeID;
                 String bookingRef, date, to, from;
        
              
                 int i = 1;
                 while (i<=setNo)
                    if(i == setNo) {
                        bookingRef = rs1.getString("bookingRef");
                        routeID = rs1.getInt("routeID");
                        date = rs1.getString("bookingdate");
                        
                        sql = "SELECT * FROM routes WHERE routeID=" + routeID;
                        ResultSet rs2 = stmt.executeQuery(sql); 
                        rs2.next();
                        to = rs2.getString("dest");
                        from = rs2.getString("frm");
                        return new TFObject(bookingRef, from, to, routeID, date);
                    }
                    else {
                        rs1.next();
                        i++;
                    }
                rs1.close();
                }    
                  
          //}
          rs.close();
          stmt.close();
          conn.close(); 
        
       
       }catch(SQLException se){
          //Handle errors for JDBC
          se.printStackTrace();
       }catch(Exception e){
          //Handle errors for Class.forName
          e.printStackTrace();
       }finally{
          //finally block used to close resources
          try{
             if(stmt!=null)
                stmt.close();
          }catch(SQLException se2){
          }// nothing we can do
          try{
             if(conn!=null)
                conn.close();
          }catch(SQLException se){
             se.printStackTrace();
          }//end finally try
       }//end try
       return new TFObject();
    }//end main
   
   public static boolean cancelBooking(TFObject tf) {
       Connection conn = null;
       Statement stmt = null;
       Statement stmt2 = null;
       try{
          //STEP 2: Register JDBC driver
          Class.forName("com.mysql.jdbc.Driver");
    
          //STEP 3: Open a connection
          conn = DriverManager.getConnection(DB_URL,USER,PASS);
    
          //STEP 4: Execute a query
          stmt = conn.createStatement();
          stmt2 = conn.createStatement();
              
              String sql;
              sql = "SELECT COUNT(*) as COUNT FROM persondetail WHERE bookingRef = '"+tf.getBookingRef()+"' AND age="+tf.getAge()+" AND name='"+tf.getName()+ "'";
              ResultSet rs = stmt.executeQuery(sql);
              rs.next();
              if (rs.getInt("COUNT") > 0) {
                     sql = "DELETE FROM bookinginfo WHERE bookingRef = '"+tf.getBookingRef()+"'";
                     int deleteFromBI = stmt.executeUpdate(sql);
                     sql = "DELETE FROM persondetail WHERE bookingRef = '"+tf.getBookingRef()+"'";
                     int deleteFromPD = stmt2.executeUpdate(sql);
                     if (deleteFromBI > 0 && deleteFromPD > 0) {
                         return true;
                     }
                     else {
                         return false;
                     }
               
            
              }
              rs.close();
              stmt.close();
              conn.close();
           
          }catch(SQLException se){
          //Handle errors for JDBC
          se.printStackTrace();
       }catch(Exception e){
          //Handle errors for Class.forName
          e.printStackTrace();
       }finally{
          //finally block used to close resources
          try{
             if(stmt!=null)
                stmt.close();
          }catch(SQLException se2){
          }// nothing we can do
          try{
             if(conn!=null)
                conn.close();
          }catch(SQLException se){
             se.printStackTrace();
          }//end finally try
       }//end try
       return false;
    }//end main
    
    public static ArrayList<TFObject> findRoute(TFObject tf) {
       Connection conn = null;
       Statement stmt = null;
       Statement stmt1 = null;
       Statement stmt2 = null;
       Statement stmt3 = null;
       Statement stmt4 = null;
       Statement stmt5 = null;
       ArrayList<TFObject> arrayTF = new ArrayList<TFObject>();
       try{
          //STEP 2: Register JDBC driver
          Class.forName("com.mysql.jdbc.Driver");
    
          //STEP 3: Open a connection
          conn = DriverManager.getConnection(DB_URL,USER,PASS);
    
          //STEP 4: Execute a query
          stmt = conn.createStatement();
          stmt1 = conn.createStatement();
          stmt2 = conn.createStatement();
          stmt3 = conn.createStatement();
          stmt4 = conn.createStatement();
          stmt5 = conn.createStatement();

          String sql;
          
          int i = 0;
          ResultSet rs, rs1, rs2, rs3, rs4, rs5;
          
          
          if (tf.getMaxHop() >= 1) {
              sql = "SELECT COUNT(*) AS C, seatCapacity, routeID, duration, price FROM routes WHERE frm = '" + tf.getFrom() + "' AND dest = '" +tf.getTo() + "'";
              rs = stmt.executeQuery(sql); 
              rs.next();
              if (rs.getInt("C") == 1) {
                  sql = "SELECT COUNT(*) AS C FROM bookinginfo WHERE routeID=" + rs.getInt("routeID") +" AND bookingdate = '" +  tf.getDate()+ "'";
                  rs1 = stmt1.executeQuery(sql);
                  rs1.next();
                  if (rs1.getInt("C") < rs.getInt("seatCapacity")) {
                      arrayTF.add(new TFObject(i, rs.getInt("routeID"), tf.getFrom(), tf.getTo(), rs.getInt("duration"), rs.getDouble("price"), tf.getDate(), 1));
                      i++;
                  }
              }
          }
          if (tf.getMaxHop() >= 2) {
              sql = "SELECT * FROM routes WHERE frm = '" + tf.getFrom() + "'";
              rs2 = stmt2.executeQuery(sql);
              
              while (rs2.next()) {
                  sql = "SELECT * FROM routes WHERE frm = '" + rs2.getString("dest") + "' AND dest= '" + tf.getTo() + "'";
                  rs3 = stmt3.executeQuery(sql);
                  while (rs3.next()){
                  
                      sql = "SELECT COUNT(*) AS C FROM bookinginfo WHERE routeID=" + rs2.getInt("routeID") +" AND bookingdate = '" +  tf.getDate() + "'";
                      rs4 = stmt4.executeQuery(sql);
                      rs4.next();
                      sql = "SELECT COUNT(*) AS C FROM bookinginfo WHERE routeID=" + rs3.getInt("routeID") +" AND bookingdate = '" +  tf.getDate() + "'";
                      rs5 = stmt5.executeQuery(sql);
                      rs5.next();
                      if (rs4.getInt("C") < rs2.getInt("seatCapacity") && rs5.getInt("C") < rs3.getInt("seatCapacity")) {
                          arrayTF.add(new TFObject(i, rs2.getInt("routeID"), tf.getFrom(), rs2.getString("dest"), rs2.getInt("duration"), rs2.getDouble("price"), tf.getDate(), 2));
                          arrayTF.add(new TFObject(i, rs3.getInt("routeID"), rs3.getString("frm"), tf.getTo(), rs3.getInt("duration"), rs3.getDouble("price"), tf.getDate(), 2));
                          i++;
                      }
                 }
                  
              }
          }
         
          return arrayTF;
                
            
        
       }catch(SQLException se){
          //Handle errors for JDBC
          se.printStackTrace();
       }catch(Exception e){
          //Handle errors for Class.forName
          e.printStackTrace();
       }finally{
          //finally block used to close resources
          try{
             if(stmt!=null)
                stmt.close();
          }catch(SQLException se2){
          }// nothing we can do
          try{
             if(conn!=null)
                conn.close();
          }catch(SQLException se){
             se.printStackTrace();
          }//end finally try
       }//end try
       return arrayTF;
    }//end main
     
    public static int makeBooking(ArrayList<TFObject> arraytf, TFObject tf) {
       Connection conn = null;
       Statement stmt = null;
       Statement stmt1 = null;

       
       //ArrayList<TFObject> arrayTF = new ArrayList<TFObject>();
       try{
          //STEP 2: Register JDBC driver
          Class.forName("com.mysql.jdbc.Driver");
    
          //STEP 3: Open a connection
          conn = DriverManager.getConnection(DB_URL,USER,PASS);
          conn.setAutoCommit(false);
    
          //STEP 4: Execute a query
          stmt = conn.createStatement();
          stmt1 = conn.createStatement();
          
          String sql;
          
          int i = 0;
          ResultSet rs, rs1;
          int randomNum;
          int insertToBI = 0;
          int insertToPD = 0;
          Random rand = new Random(); 
          randomNum = rand.nextInt(999999999);
          while (i < arraytf.size()) {
              
              if (arraytf.get(i).getSno() == tf.getSno()) {

                  sql = "INSERT INTO bookinginfo (bookingref,routeID, bookingdate) VALUES (" + randomNum + "," + arraytf.get(i).getRouteID() + ",'" + arraytf.get(i).getDate() + "')";
                  insertToBI = stmt.executeUpdate(sql);
                  sql = "SELECT * FROM routes WHERE routeID = " + arraytf.get(i).getRouteID();
                  rs = stmt.executeQuery(sql);
                  rs.next();
                  sql = "SELECT COUNT(*) AS C FROM bookinginfo WHERE routeID = " + arraytf.get(i).getRouteID() +" AND bookingdate = '" +  arraytf.get(i).getDate() + "'";
                  rs1 = stmt1.executeQuery(sql);
                  rs1.next();
                  
                  if (rs1.getInt("C") > rs.getInt("seatCapacity")) { 
                      conn.rollback();
                      i = arraytf.size();
                      insertToBI = 0;
                  }
                }
                  i++;
               
            }
          sql = "INSERT INTO persondetail (bookingRef, name, age) VALUES (" + randomNum + ",'" + tf.getName() + "'," + tf.getAge() +")";
          insertToPD = stmt1.executeUpdate(sql);
          
          if (insertToBI >0 && insertToPD > 0) {
              conn.commit();
              return randomNum;
            }
                  else return 0;
         

        
       }catch(SQLException se){
          //Handle errors for JDBC
          se.printStackTrace();
       }catch(Exception e){
          //Handle errors for Class.forName
          e.printStackTrace();
       }finally{
          //finally block used to close resources
          try{
             if(stmt!=null)
                stmt.close();
          }catch(SQLException se2){
          }// nothing we can do
          try{
             if(conn!=null)
                conn.close();
          }catch(SQLException se){
             se.printStackTrace();
          }//end finally try
       }//end try
       return 0;
    }//end main
}//end 