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
    protected static double virusrange = 111302.62;
    protected static String password = "";
    protected static Connection connection =   null;
    protected static Statement sql    = null;
    protected static ResultSet apotik = null;
    protected static ResultSet virus = null;
    protected static String url = "jdbc:postgresql://localhost:5432/indonesia_map";

    public static boolean CheckInfection(double x, double y){
        try{
            connection = DriverManager.getConnection(url, username,password);

            sql = connection.createStatement();

            virus = sql.executeQuery("SELECT ST_AsText(geom) FROM virus_tarikmang WHERE ST_Distance(ST_Transform(ST_GeomFromText('POINT(" + x + " " + y + ")',4326),26986),ST_Transform(geom,26986)) <= " + virusrange + ";");
            if (virus.next()){
                return true;
            }
        }    
        catch (SQLException e){
          System.out.println("Error " +e);
        }
        return false;
    }

    public static String FindShortest(double x, double y){
        String result = null;
        try{
            connection = DriverManager.getConnection(url, username,password);

            sql = connection.createStatement();

            apotik = sql.executeQuery("SELECT distinct name, ST_AsText(geom), (SELECT min(ST_Distance(ST_Transform(ST_GeomFromText('POINT(" + x + " " + y + ")',4326),26986),ST_Transform(geom,26986))) FROM apotik)FROM apotik WHERE ST_Distance(ST_Transform(ST_GeomFromText('POINT(" + x + " " + y + ")',4326),26986),ST_Transform(geom,26986)) = (SELECT min(ST_Distance(ST_Transform(ST_GeomFromText('POINT(" + x + " " + y + ")',4326),26986),ST_Transform(geom,26986))) FROM apotik);");
            if (apotik.next()){
                result = apotik.getString(1) + "," + apotik.getString(2) + "," + apotik.getString(3);
            }
        }    
        catch (SQLException e){
          System.out.println("Error " +e);
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

            if (CheckInfection(y, x)){
                String temp = FindShortest(y, x);
                System.out.println(temp);
            }
        }else if (args[0].equals("addViruses")){
            double x = Double.parseDouble(args[1]);
            double y = Double.parseDouble(args[2]);
            AddVirus(x, y);
        } 
   }
}
    
    

