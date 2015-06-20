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

/**
 *
 * @author KEVIN
 */
public class SpatialDatabase {
    
    
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
            result = data.getString(1) + "," + data.getString(2) + "," + shortestdistance;
            while (data.next()){
                Point temppoint = ((PGgeometry) data.getObject(2)).getGeometry().getFirstPoint();
                double tempdistance = currentpoint.distance(temppoint);
                if (shortestdistance > tempdistance){
                    shortestdistance = tempdistance;
                    result = data.getString(1) + "," + data.getString(2) + "," + shortestdistance;
                }
            }
        } catch (SQLException ex) {
            Logger.getLogger(SpatialDatabase.class.getName()).log(Level.SEVERE, null, ex);
        }
    return result;
    }
    
    public static void AddVirus(double x , double y){
        try{
        Connection connection =   null;
        Statement sql    = null;
        String url = "jdbc:postgresql://localhost:5432/indonesia_map";
        String username = "postgres";
        String password = "kenk3n1x";
        connection = DriverManager.getConnection(url, username,password);
        
        sql = connection.createStatement();
        String stm = "INSERT INTO virus_tarikmang(geom) values( ST_GeomFromText('Point(" + x + " "+ y + ")',4326))";
        sql.executeQuery(stm);
        }
        catch (SQLException ex){
        }
    }
    
    public static void main(String[] args) {
        // TODO code application logic here
         Connection connection =   null;

    Statement sql    = null;
    ResultSet apotik = null;
    ResultSet virus = null;
    String url = "jdbc:postgresql://localhost:5432/indonesia_map";

    String username = "postgres";

    String password = "kenk3n1x";

    try{

    connection = DriverManager.getConnection(url, username,password);

    sql = connection.createStatement();

    virus = sql.executeQuery("SELECT name, ST_AsText(geom) FROM virus_tarikmang;");
    while (virus.next()) {
        
        
         System.out.println(virus.getString(1)+ ","+ virus.getString(2));
            
         }

    apotik = sql.executeQuery("SELECT name, geom FROM  apotik");
    String temp = FindShortest(apotik, 101, -6.);
    System.out.println(temp);
    }    

    catch (SQLException e)
        
    {

      System.out.println("Error " +e);

    }
   }
}
    
    

