/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author shubham.kahal
 */
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.annotation.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.inject.Named;
import javax.servlet.http.Part;

@Named(value = "assignment")
@SessionScoped
@ManagedBean
public class Assignment implements Serializable {
    private DBConnect dbConnect = new DBConnect();
    private Class cl;
    private Student student;
    private String name;
    private String description;
    private Date dueDate;
    private Part file; // +getter+setter

    public DBConnect getDbConnect() {
        return dbConnect;
    }

    public void setDbConnect(DBConnect dbConnect) {
        this.dbConnect = dbConnect;
    }

    public Class getCl() {
        return cl;
    }

    public void setCl(Class cl) {
        this.cl = cl;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
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

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    public Part getFile() {
        return file;
    }

    public void setFile(Part file) {
        this.file = file;
    }
    
    public List<Assignment> getAssignmentList() throws SQLException {

        Connection con = dbConnect.getConnection();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }

        PreparedStatement preparedStatement
                = con.prepareStatement(
                        "select assignment.name as assgn_name, assignment.description as assgn_description, due_date, class.name as class_name from " +
                        "student join attend on student.id = attend.student_id " +
                        "join class on class.id = attend.class_id join assignment on class.id = assignment.class_id " +
                        "where student.login = ?");
        
        //get customer data from database
        preparedStatement.setString(1, Util.getStudentLogin());
        
        ResultSet result = preparedStatement.executeQuery();

        List<Assignment> assignmentList = new ArrayList<>();

        while (result.next()) {
            Assignment assignment = new Assignment();
            
            assignment.setName(result.getString("assgn_name"));
            
            assignment.cl = new Class();
            
            assignment.cl.setName(result.getString("class_name")); 
            
            assignment.setDescription(result.getString("assgn_description"));
            
            assignment.setDueDate(result.getDate("due_date"));
   
            //store all data into a List
            assignmentList.add(assignment);
        }
        
        result.close();
        con.close();
        
        return assignmentList;
    }

    public void save(String fileNameAttributes) {
        try (InputStream input = file.getInputStream()) {
            Files.copy(input, new File("uploads", file.getSubmittedFileName() + "_" + fileNameAttributes).toPath());
        }
        catch (IOException e) {
            // Show faces message?
        }
    }
    
    // This function will consume the id of an assignment that is submitted
    // and appropriate update the database with it.
    public void submit(Integer id) {
        save(id + "_" + Util.getStudentLogin());
    }
    
    public void createAssignment() throws SQLException {
        Connection con = dbConnect.getConnection();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }

        PreparedStatement preparedStatement
                = con.prepareStatement(
                        "insert into assignment(name, description, due_date, class_id) " +
                        "values(?, ?, ?, (select id from class where name = ?))");
        
        //get customer data from database
        preparedStatement.setString(1, this.name);
        preparedStatement.setString(2, this.description);
        preparedStatement.setDate(3, new java.sql.Date(this.dueDate.getTime()));
        preparedStatement.setString(4, this.cl.getName());
        
        preparedStatement.executeUpdate();
    }
}
