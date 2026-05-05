package connection;

import java.sql.Connection;
import java.sql.DriverManager;

public class UtilDB {
   private String url;
   private String user;
   private String password;
   private Connection con;

   public void setUrl(String url)           { this.url      = url;      }
   public void setUser(String user)         { this.user     = user;     }
   public void setPassword(String password) { this.password = password; }

   public String getUrl()      { return url;      }
   public String getUser()     { return user;     }
   public String getPassword() { return password; }

   public Connection getConnection() {
       try {
           con = DriverManager.getConnection(url, user, password);
       } catch (Exception e) {
           e.printStackTrace();
       }
       return con;
   }
}