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
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.annotation.ManagedBean;
import javax.faces.bean.SessionScoped;
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
        
        HashMap<String, String> timeMap = new HashMap<>();
        
        timeMap.put("07:10 AM", "07:10");
        timeMap.put("08:10 AM", "08:10");
        timeMap.put("09:10 AM", "09:10");
        timeMap.put("10:10 AM", "10:10");
        timeMap.put("11:10 AM", "11:10");
        timeMap.put("12:10 PM", "12:10");
        timeMap.put("01:10 PM", "13:10");
        timeMap.put("02:10 PM", "14:10");
        timeMap.put("03:10 PM", "15:10");
        timeMap.put("04:10 PM", "16:10");
        timeMap.put("05:10 PM", "17:10");
        timeMap.put("06:10 PM", "18:10");
        timeMap.put("07:10 PM", "19:10");
        timeMap.put("08:10 PM", "20:10");
        timeMap.put("09:10 PM", "21:10");
        timeMap.put("10:10 PM", "22:10");
        
        for (String time : times) {
            if (timeMap.get(this.startTime).compareTo(timeMap.get(time)) < 0) {
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
