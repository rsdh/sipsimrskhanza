/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fungsi;

import AESsecurity.EnkripsiAES;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.Properties;
import javax.swing.JOptionPane;

/**
 *
 * @author khanzasoft
 */
public final class koneksiDB {
    public koneksiDB(){}    
    private static Connection connection=null;
    private static final Properties prop = new Properties();  
    private static MysqlDataSource dataSource=new MysqlDataSource();
    private static String caricepat="",var="";
    public static Connection condb(){      
        if(connection == null){
            try{
                prop.loadFromXML(new FileInputStream("setting/database.xml"));
                
                if (prop.getProperty("OVERSSH").equalsIgnoreCase("aktif")) {
                    int lport = Integer.parseInt(
                            EnkripsiAES.decrypt(
                                    prop.getProperty("LPORT")
                            )
                    );
                    String rhost = EnkripsiAES.decrypt(
                            prop.getProperty("HOST")
                    );
                    int rport = Integer.parseInt(
                            EnkripsiAES.decrypt(
                                    prop.getProperty("PORT")
                            )
                    );
                    
                    String host = EnkripsiAES.decrypt(
                            prop.getProperty("SSHIP")
                    );
                    int port = Integer.parseInt(
                            EnkripsiAES.decrypt(
                                    prop.getProperty("SSHPORT")
                            )
                    );
                    String user = EnkripsiAES.decrypt(
                            prop.getProperty("SSHUSER")
                    );
                    String pass = EnkripsiAES.decrypt(
                            prop.getProperty("SSHPASS")
                    );
                    String dbUserName = EnkripsiAES.decrypt(
                            prop.getProperty("USER")
                    );                    
                    String dbPassword = EnkripsiAES.decrypt(
                            prop.getProperty("PAS")
                    );
                    
                    String url = "jdbc:mysql://127.0.0.1:" + lport + "/" + EnkripsiAES.decrypt(prop.getProperty("DATABASE")) + "?zeroDateTimeBehavior=convertToNull";
                    String driverName = "com.mysql.jdbc.Driver";
                    
                    java.util.Properties config = new java.util.Properties();
                    config.put("StrictHostKeyChecking", "no");
                    
                    JSch jsch = new JSch();
                    System.out.println(user+"+"+pass+"+" + host+"+"+port);
                    Session session = jsch.getSession(user, host, port);
                    session.setPassword(pass);
                    session.setConfig(config);
                    session.connect();
                    int assignt_port = session.setPortForwardingL(lport, rhost, rport);
                    System.out.println("127.0.0.1:" + assignt_port + " -> " + rhost + ":" + rport);
                    System.out.println("Port Forwaded");
                    Class.forName(driverName).newInstance();
                    connection = DriverManager.getConnection(url, dbUserName, dbPassword);
                    
                    System.out.println("Connected Over SSH");
                } else {
                    dataSource.setURL("jdbc:mysql://"+EnkripsiAES.decrypt(prop.getProperty("HOST"))+":"+EnkripsiAES.decrypt(prop.getProperty("PORT"))+"/"+EnkripsiAES.decrypt(prop.getProperty("DATABASE"))+"?zeroDateTimeBehavior=convertToNull");
                    dataSource.setUser(EnkripsiAES.decrypt(prop.getProperty("USER")));
                    dataSource.setPassword(EnkripsiAES.decrypt(prop.getProperty("PAS")));
                    connection = dataSource.getConnection();       
                    System.out.println("Connected Over Local Network");
                }
                
                
                System.out.println("  Koneksi Berhasil. Sorry bro loading, silahkan baca dulu.... \n\n"+
                        "	Software ini adalah Software Menejemen Rumah Sakit/Klinik/\n" +
                        "  Puskesmas yang  gratis dan boleh digunakan siapa saja tanpa dikenai \n" +
                        "  biaya apapun. Dilarang keras memperjualbelikan/mengambil \n" +
                        "  keuntungan dari Software ini dalam bentuk apapun tanpa seijin pembuat \n" +
                        "  software (Khanza.Soft Media). Bagi yang sengaja memperjualbelikan/\n" +
                        "  mengambil keuntangan dari softaware ini tanpa ijin, kami sumpahi sial \n" +
                        "  1000 turunan, miskin sampai 500 turunan. Selalu mendapat kecelakaan \n" +
                        "  sampai 400 turunan. Anak pertamanya cacat tidak punya kaki sampai 300 \n" +
                        "  turunan. Susah cari jodoh sampai umur 50 tahun sampai 200 turunan.\n" +
                        "  Ya Alloh maafkan kami karena telah berdoa buruk, semua ini kami lakukan\n" +
                        "  karena kami tidak pernah rela karya kami dibajak tanpa ijin.\n\n"+
                        "                                                                           \n"+
                        "  #    ____  ___  __  __  ____   ____    _  __ _                              \n" +
                        "  #   / ___||_ _||  \\/  ||  _ \\ / ___|  | |/ /| |__    __ _  _ __   ____ __ _ \n" +
                        "  #   \\___ \\ | | | |\\/| || |_) |\\___ \\  | ' / | '_ \\  / _` || '_ \\ |_  // _` |\n" +
                        "  #    ___) || | | |  | ||  _ <  ___) | | . \\ | | | || (_| || | | | / /| (_| |\n" +
                        "  #   |____/|___||_|  |_||_| \\_\\|____/  |_|\\_\\|_| |_| \\__,_||_| |_|/___|\\__,_|\n" +
                        "  #                                                                           \n"+
                        "                                                                           \n"+
                        "  Licensi yang dianut di software ini https://en.wikipedia.org/wiki/Aladdin_Free_Public_License \n"+
                        "  Informasi dan panduan bisa dicek di halaman https://github.com/mas-elkhanza/SIMRS-Khanza/wiki \n"+
                        "                                                                           ");
            }catch(Exception e){
                JOptionPane.showMessageDialog(null,"Koneksi Putus : "+e);
            }
        }
        
        if (!prop.getProperty("OVERSSH").equalsIgnoreCase("aktif")) {
            while (true) {
                try {
                    PreparedStatement ps = connection.prepareStatement("SELECT 1");
                    if (!ps.execute()) {
                        prop.loadFromXML(new FileInputStream("setting/database.xml"));
                        try {
                            dataSource.getConnection().close();
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        
                        try {
                            dataSource = null;
                            connection = null;
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                        
                        dataSource = new MysqlDataSource();
                        dataSource.setURL("jdbc:mysql://"+EnkripsiAES.decrypt(prop.getProperty("HOST"))+":"+EnkripsiAES.decrypt(prop.getProperty("PORT"))+"/"+EnkripsiAES.decrypt(prop.getProperty("DATABASE"))+"?zeroDateTimeBehavior=convertToNull");
                        dataSource.setUser(EnkripsiAES.decrypt(prop.getProperty("USER")));
                        dataSource.setPassword(EnkripsiAES.decrypt(prop.getProperty("PAS")));
                        connection=dataSource.getConnection();
                    }
                    break;
                } catch (Exception ex) {
                    ex.printStackTrace();
                    int msg = JOptionPane.showConfirmDialog(
                            null, 
                            "Koneksi ada gangguan klick ok jika mau close aplikasinya", 
                            "Pesan", 
                            JOptionPane.OK_CANCEL_OPTION
                    );
                    if (msg == JOptionPane.OK_OPTION) {
                        System.exit(1);
                        break;
                    }
                    try {
                        try {
                            dataSource.getConnection().close();
                        } catch (Exception ex_1) {
                            ex_1.printStackTrace();
                        }
                        
                        try {
                            dataSource = null;
                            connection = null;
                        } catch (Exception ex_1) {
                            ex_1.printStackTrace();
                        }
                        
                        dataSource=new MysqlDataSource();
                        dataSource.setURL("jdbc:mysql://"+EnkripsiAES.decrypt(prop.getProperty("HOST"))+":"+EnkripsiAES.decrypt(prop.getProperty("PORT"))+"/"+EnkripsiAES.decrypt(prop.getProperty("DATABASE"))+"?zeroDateTimeBehavior=convertToNull");
                        dataSource.setUser(EnkripsiAES.decrypt(prop.getProperty("USER")));
                        dataSource.setPassword(EnkripsiAES.decrypt(prop.getProperty("PAS")));
                        connection=dataSource.getConnection();
                    } catch (Exception ex1) {
                        ex1.printStackTrace();
                    }
                }
            }
        }
        return connection;        
    }
    
    public static String cariCepat(){
        try{
            prop.loadFromXML(new FileInputStream("setting/database.xml"));
            caricepat=prop.getProperty("CARICEPAT");
        }catch(Exception e){
            caricepat="tidak aktif"; 
        }
        return caricepat;
    }
    
    public static String HOST(){
        try{
            prop.loadFromXML(new FileInputStream("setting/database.xml"));
            var=EnkripsiAES.decrypt(prop.getProperty("HOSTHYBRIDWEB"));
        }catch(Exception e){
            var="localhost"; 
        }
        return var;
    }
    
    public static String PORT(){
        try{
            prop.loadFromXML(new FileInputStream("setting/database.xml")); 
            var=EnkripsiAES.decrypt(prop.getProperty("PORT"));
        }catch(Exception e){
            var="3306"; 
        }
        return var;
    }
    
    public static String DATABASE(){
        try{
            prop.loadFromXML(new FileInputStream("setting/database.xml"));
            var=EnkripsiAES.decrypt(prop.getProperty("DATABASE"));
        }catch(Exception e){
            var="sik"; 
        }
        return var;
    }
    
    
}
