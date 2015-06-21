/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package spatialdatabase;

import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.postgis.*;
import java.util.regex.*;

/**
 *
 * @author KEVIN
 */
public class SpatialDatabase {
    protected static String username = "postgres";
    protected static String password = "";
    protected static Connection connection =   null;
    protected static Statement sql    = null;
    protected static ResultSet apotik = null;
    protected static ResultSet virus = null;
    protected static String url = "jdbc:postgresql://localhost:5432/indonesia_map";

    public static boolean CheckInfection(ResultSet data, double x, double y){
        boolean infected = false;
        Point currentpoint = new Point(x,y);
        double virusrange = 111302.62;
        try{
            while (data.next() && !(infected)){
                Point temppoint = ((PGgeometry) data.getObject(2)).getGeometry().getFirstPoint();
                double tempdistance = currentpoint.distance(temppoint);
                if (tempdistance <= virusrange)
                    infected = true;
            }
        }
        catch (SQLException ex){
        }
        return infected;
    }
        
    public static String FindShortest(ResultSet data, double x, double y){
        String result = null;
        Point currentpoint = new Point(x,y);
        try {
            data.next();
            double shortestdistance = currentpoint.distance(((PGgeometry)data.getObject(2)).getGeometry().getFirstPoint());
            result = data.getString(1) + "," + data.getString(3) + "," + shortestdistance;
            while (data.next()){
                Point temppoint = ((PGgeometry) data.getObject(2)).getGeometry().getFirstPoint();
                double tempdistance = currentpoint.distance(temppoint);
                if (shortestdistance > tempdistance){
                    shortestdistance = tempdistance;
                    result = data.getString(1) + "," + data.getString(3) + "," + shortestdistance;
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(SpatialDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }
    return result;
    }
    
    public static void AddVirus(double x , double y){
        try{
            connection = DriverManager.getConnection(url, username,password);
            
            sql = connection.createStatement();
            String stm = "INSERT INTO virus_tarikmang(geom) values( ST_GeomFromText('Point(" + x + " "+ y + ")',4326))";
            sql.executeQuery(stm);
        }
        catch (SQLException ex){
        }
    }
    
    public static void main(String[] args) {
        try{
            Class.forName("org.postgresql.Driver");    
        }catch(Exception e){
            System.out.println("Error " +e);
        }

        if (args[0].equals("getApoteks")){
            try{
                connection = DriverManager.getConnection(url, username,password);

                sql = connection.createStatement();

                apotik = sql.executeQuery("SELECT name, ST_AsText(geom) FROM apotik;");
                
                while (apotik.next()) {
                    System.out.println(apotik.getString(1)+ ","+ apotik.getString(2));   
                }
            }    
            catch (SQLException e){
              System.out.println("Error " +e);
            }
        }else if (args[0].equals("getViruses")){
            try{
                connection = DriverManager.getConnection(url, username,password);
                sql = connection.createStatement();

                virus = sql.executeQuery("SELECT ST_AsText(geom) FROM virus_tarikmang;");
                
                while (virus.next()) {
                    System.out.println(virus.getString(1));
                }
            }    
            catch (SQLException e){
              System.out.println("Error " +e);
            }
        }else if (args[0].equals("getInfected")){
            double x = Double.parseDouble(args[1]);
            double y = Double.parseDouble(args[2]);

            try{
                connection = DriverManager.getConnection(url, username,password);

                sql = connection.createStatement();

                apotik = sql.executeQuery("SELECT name, geom, ST_AsText(geom) FROM  apotik");
                if (CheckInfection(apotik, y, x)){
                    String temp = FindShortest(apotik, y, x);
                    System.out.println(temp);
                }
            }    
            catch (SQLException e){
              System.out.println("Error " +e);
            }

        }else if (args[0].equals("addViruses")){
            double x = Double.parseDouble(args[1]);
            double y = Double.parseDouble(args[2]);
            AddVirus(x, y);
        } 
   }
}
    
    

