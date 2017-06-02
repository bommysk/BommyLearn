/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author shubham.kahal
 */
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.ManagedBean;
import javax.faces.application.FacesMessage;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.inject.Named;

@Named(value = "class")
@SessionScoped
@ManagedBean
public class Class implements Serializable {
    private DBConnect dbConnect = new DBConnect();
    private Integer id;
    private String name;
    private String description;
    private String daySchedule;
    private String startTime;
    private String endTime;

    public DBConnect getDbConnect() {
        return dbConnect;
    }

    public void setDbConnect(DBConnect dbConnect) {
        this.dbConnect = dbConnect;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }        
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDaySchedule() {
        return daySchedule;
    }

    public void setDaySchedule(String daySchedule) {
        this.daySchedule = daySchedule;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }   
        
    public String createClass() throws SQLException, ParseException {
        Connection con = dbConnect.getConnection();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }
        
        con.setAutoCommit(false);

        PreparedStatement preparedStatement = con.prepareStatement("insert into class(name, description, day_schedule, start_time, end_time) values(?,?,?,?,?)");
        preparedStatement.setString(1, name);
        preparedStatement.setString(2, description);
        preparedStatement.setString(3, daySchedule);
        preparedStatement.setString(4, startTime);
        preparedStatement.setString(5, endTime);               
        preparedStatement.executeUpdate();
                
        con.commit();
        con.close();        
        
        clear();
        return "addClass";
    }
    
    public List<Class> getClassList() throws SQLException {
        Connection con = dbConnect.getConnection();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }

        PreparedStatement preparedStatement
                = con.prepareStatement("select * from class");
        
        ResultSet result = preparedStatement.executeQuery();

        List<Class> classList = new ArrayList<>();
    
        while (result.next()) {
            Class cl = new Class();
            
            cl.setId(result.getInt("id"));
            
            cl.setName(result.getString("name"));
            
            cl.setDescription(result.getString("description"));
            
            cl.setDaySchedule(result.getString("day_schedule"));
            
            cl.setStartTime(result.getString("start_time"));
            
            cl.setEndTime(result.getString("end_time"));          
   
            //store all data into a List
            classList.add(cl);
        }
        
        result.close();
        con.close();
    
        return classList;
    }
    
    public List<String> getEndTimes() {
        List<String> endTimes = new ArrayList<>();
        
        if (this.startTime == null || this.startTime.equals("Start Time")) {
            return endTimes;
        }
        
        String[] times = new String[]{ "07:10 AM", "08:10 AM", "09:10 AM", "10:10 AM",
                                "11:10 AM", "12:10 PM", "01:10 PM", "02:10 PM", "03:10 PM",
                                "04:10 PM", "05:10 PM", "06:10 PM", "07:10 PM", "08:10 PM",
                                "09:10 PM", "10:10 PM" };
        
        for (String time : times) {
            if (this.startTime.split(" ")[1].equals("AM") && time.split(" ")[1].equals("PM")) {
                endTimes.add(time);
            }
            else if (this.startTime.split(" ")[1].equals("PM") && time.split(" ")[1].equals("AM")) {
                
            }
            else if (time.equals("12:10 PM")) {
              
            }
            else if (this.startTime.equals("12:10 PM") || this.startTime.split(" ")[0].compareTo(time.split(" ")[0]) < 0) {
                endTimes.add(time);
            }
        }
        
        return endTimes;
    }
    
    public String makeLabel(String name, String daySchedule, String startTime, String endTime) {
        return name + " -- " + daySchedule + ": " + startTime + "-" + endTime;
    }
    
    public void clear() {
        setName(null);
        setDescription(null);
        setStartTime(null);
        setEndTime(null);
    }
}
