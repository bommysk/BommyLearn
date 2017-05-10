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
import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.inject.Named;

@Named(value = "class")
@SessionScoped
@ManagedBean
public class Class implements Serializable {
    private DBConnect dbConnect = new DBConnect();
    private String name;
    private String description;
    private String daySchedule;
    private Time startTime;
    private Time endTime;
    private Date startDate;
    private Date endDate;

    public DBConnect getDbConnect() {
        return dbConnect;
    }

    public void setDbConnect(DBConnect dbConnect) {
        this.dbConnect = dbConnect;
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

    public Time getStartTime() {
        return startTime;
    }

    public void setStartTime(Time startTime) {
        this.startTime = startTime;
    }

    public Time getEndTime() {
        return endTime;
    }

    public void setEndTime(Time endTime) {
        this.endTime = endTime;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
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
            
            cl.setName(result.getString("name"));
            
            cl.setDescription(result.getString("description"));
            
            cl.setDaySchedule(result.getString("day_schedule"));
            
            cl.setStartTime(result.getTime("start_time"));
            
            cl.setEndTime(result.getTime("end_time"));
            
            cl.setStartDate(result.getDate("start_date"));
            
            cl.setEndDate(result.getDate("end_date"));
   
            //store all data into a List
            classList.add(cl);
        }
        
        result.close();
        con.close();
    
        return classList;
    }
}
