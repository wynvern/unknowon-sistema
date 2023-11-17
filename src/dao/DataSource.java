/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import classes.ConfigFile;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 *
 * @author wynvern
 */
public class DataSource {
    // variáveis para a conexão
    private String hostname;
    private int    porta;
    private String database;
    private String username;
    private String password;
    
    // variável de conexão
    private Connection connection;
    
    // pedido/abre de conexão
    public DataSource(){
        try{
            Properties variaveis = ConfigFile.getVariables();

            hostname = variaveis.getProperty("hostname");
            porta = Integer.parseInt(variaveis.getProperty("port"));
            database = variaveis.getProperty("database");
            username = variaveis.getProperty("username");
            password = variaveis.getProperty("password");
            
            // string de conexão
            String url = "jdbc:mysql://"+hostname+":"+porta+"/"+database+"?useTimezone=true&serverTimezone=UTC";
            
            // registrar o driver
            DriverManager.registerDriver(new com.mysql.cj.jdbc.Driver());
            
            // faz a conexão
            connection = DriverManager.getConnection(url, username, password);
            
            //System.out.println("Conectou!");
        }
        catch (SQLException ex){
            System.err.println("ERRO na conexão "+ex.getMessage());
        }
        catch (Exception ex){
            System.err.println("ERRO geral "+ex.getMessage());
        }
    }
    
    // pega a conexão ativa
    public Connection getConnection(){
        return this.connection;
    }
    
    // fechamento da conexão
    public void closeDataSource(){
        try{
            connection.close();
            //System.out.println("Conexão fechada!");
        }
        catch (Exception ex){
            System.err.println("ERRO ao desconectar "+ex.getMessage());
        }
    }    
}
