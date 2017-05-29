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
import java.io.PrintWriter;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import javax.annotation.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.servlet.http.Part;

@Named(value = "assignment")
@SessionScoped
@ManagedBean
public class Assignment implements Serializable {
    private DBConnect dbConnect = new DBConnect();
    private Class cl;
    private Student student;
    private Integer id;
    private Integer assignmentSubmitId;
    private String name;
    private String description;
    private Date dueDate;
    private Date submitDate = new Date();
    private Integer grade;
    private String filePath;
    private String fileContent;
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

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getAssignmentSubmitId() {
        return assignmentSubmitId;
    }

    public void setAssignmentSubmitId(Integer assignmentSubmitId) {
        this.assignmentSubmitId = assignmentSubmitId;
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

    public Date getSubmitDate() {
        return submitDate;
    }

    public void setSubmitDate(Date submitDate) {
        this.submitDate = submitDate;
    }

    public Integer getGrade() {
        return grade;
    }

    public void setGrade(Integer grade) {
        this.grade = grade;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileContent() {
        return fileContent;
    }

    public void setFileContent(String fileContent) {
        this.fileContent = fileContent;
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
                        "select assignment.id as assgn_id, assignment.name as assgn_name, assignment.description as assgn_description, " +
                        "assignment.due_date, class.name as class_name from " +
                        "student join class_schedule on student.id = class_schedule.student_id " +
                        "join class on class.id = class_schedule.class_id join assignment on class.id = assignment.class_id " +
                        "left join assignment_submit on assignment.id = assignment_submit.assignment_id " +
                        "where student.login = ? and assignment_submit.id is null");
        
        //get customer data from database
        preparedStatement.setString(1, Util.getStudentLogin());
        
        ResultSet result = preparedStatement.executeQuery();

        List<Assignment> assignmentList = new ArrayList<>();

        while (result.next()) {
            Assignment assignment = new Assignment();
            
            assignment.setId(result.getInt("assgn_id"));
            
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
    
    public List<Assignment> getSubmittedAssignmentList() throws SQLException {

        Connection con = dbConnect.getConnection();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }

        PreparedStatement preparedStatement
                = con.prepareStatement("select assignment.id as assignment_id, assignment_submit.id as assignment_submit_id, assignment_submit.student_id, assignment_submit.submit_date,"
                        + " assignment.due_date, assignment_submit.file_path from assignment_submit join assignment on"
                        + " assignment_submit.assignment_id = assignment.id where (graded = 0) and assignment.teacher_id = (select id from teacher where login = ?);");
        
        preparedStatement.setString(1, Util.getTeacherLogin());
        
        System.out.println("Teacher Login: " + Util.getTeacherLogin());
        
        ResultSet result = preparedStatement.executeQuery();

        List<Assignment> submittedAssignmentList = new ArrayList<>();

        while (result.next()) {
            Assignment assignment = new Assignment();
            
            assignment.setId(result.getInt("assignment_id"));
            
            assignment.setAssignmentSubmitId(result.getInt("assignment_submit_id"));
            
            assignment.student = new Student();
            
            assignment.student.setId(result.getInt("student_id"));
            
            assignment.setSubmitDate(result.getDate("submit_date"));
            
            assignment.setDueDate(result.getDate("due_date"));
            
            assignment.setFilePath(result.getString("file_path"));
   
            //store all data into a List
            submittedAssignmentList.add(assignment);
        }
        
        result.close();
        con.close();
        
        return submittedAssignmentList;
    }
    
    public List<Assignment> getGradedAssignmentList() throws SQLException {

        Connection con = dbConnect.getConnection();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }

        PreparedStatement preparedStatement
                = con.prepareStatement("select assignment.id as assignment_id, assignment_submit.id as assignment_submit_id, assignment_submit.student_id, assignment_submit.submit_date,"
                        + " assignment.due_date, assignment_submit.grade from assignment_submit join assignment on"
                        + " assignment_submit.assignment_id = assignment.id where (graded = 1) and assignment.teacher_id = (select id from teacher where login = ?);");
        
        preparedStatement.setString(1, Util.getTeacherLogin());
        
        ResultSet result = preparedStatement.executeQuery();

        List<Assignment> submittedAssignmentList = new ArrayList<>();

        while (result.next()) {
            Assignment assignment = new Assignment();
            
            assignment.setId(result.getInt("assignment_id"));
            
            assignment.setAssignmentSubmitId(result.getInt("assignment_submit_id"));
            
            assignment.student = new Student();
            
            assignment.student.setId(result.getInt("student_id"));
            
            assignment.setSubmitDate(result.getDate("submit_date"));
            
            assignment.setDueDate(result.getDate("due_date"));
            
            assignment.setGrade(result.getInt("grade"));
   
            //store all data into a List
            submittedAssignmentList.add(assignment);
        }
        
        result.close();
        con.close();
        
        return submittedAssignmentList;
    }

    public void save(String fileNameAttributes) {
        try {
            fileContent = new Scanner(file.getInputStream())
                .useDelimiter("\\A").next();
            
            System.out.println(fileContent);
            
            String relativePath = "student" + File.separator + "uploads" + File.separator;
            
            ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext(); 

            String webContentRoot = ec.getRealPath("/");
            
            System.out.println("File Name: " + webContentRoot + relativePath + fileNameAttributes + ".txt");
            
            File assignmentFile = new File( webContentRoot + relativePath + fileNameAttributes + ".txt" );
            
            // if file already exists will do nothing
            if (!assignmentFile.createNewFile()) {
                throw new IOException("File Not Created");
            }
                
            PrintWriter out = new PrintWriter( webContentRoot + relativePath + fileNameAttributes + ".txt" );
            out.println( fileContent );
            
            out.close();
            
        } catch (IOException e) {
          // Error handling
            e.printStackTrace();
        }
    }
    
    // This function will consume the id of an assignment that is submitted
    // and appropriate update the database with it.
    public void submit(Integer id) throws SQLException {
        String fileNameAttributes = id + "_" + Util.getStudentLogin();
        
        save(fileNameAttributes);
        
        // add to assignment_submit table
        Connection con = dbConnect.getConnection();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }

        PreparedStatement preparedStatement
                = con.prepareStatement(
                        "insert into assignment_submit(assignment_id, student_id, file_path, submit_date, graded) " +
                        "values(?, ?, ?, ?, 0)");
        
        preparedStatement.setInt(1, id);
        preparedStatement.setInt(2, (new Student()).getStudentId(Util.getStudentLogin()));
        preparedStatement.setString(3, "../student/uploads/" + fileNameAttributes + ".txt");
        preparedStatement.setDate(4, new java.sql.Date(this.submitDate.getTime()));
        
        preparedStatement.executeUpdate();
    }
    
    public String createAssignment() throws SQLException {
        Connection con = dbConnect.getConnection();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }

        PreparedStatement preparedStatement
                = con.prepareStatement(
                        "insert into assignment(name, description, due_date, class_id, teacher_id) " +
                        "values(?, ?, ?, (select id from class where name = ?), (select id from teacher where login = ?))");
        
        //get customer data from database
        preparedStatement.setString(1, this.name);
        preparedStatement.setString(2, this.description);
        preparedStatement.setDate(3, new java.sql.Date(this.dueDate.getTime()));
        preparedStatement.setString(4, this.cl.getName());
        preparedStatement.setString(5, Util.getTeacherLogin());
        
        preparedStatement.executeUpdate();
        
        setCl(null);
        setName(null);
        setDescription(null);
        setDueDate(null);
        
        return "index";
    }
    
    public String goGradeAssignment(Assignment assignment) {
        this.id = assignment.id;
        this.assignmentSubmitId = assignment.assignmentSubmitId;
        this.name = assignment.name;
        this.description = assignment.description;
        this.dueDate = assignment.dueDate;
        this.submitDate = assignment.submitDate;
        this.filePath =  assignment.filePath;
        
        return "gradeIndividualAssignment";
    }
    
    public String viewGradedAssignment(Assignment assignment) {
        this.id = assignment.id;
        this.assignmentSubmitId = assignment.assignmentSubmitId;
        this.name = assignment.name;
        this.description = assignment.description;
        this.dueDate = assignment.dueDate;
        this.submitDate = assignment.submitDate;
        this.filePath =  assignment.filePath; 
        this.grade = assignment.grade;
        
        return "viewGradedAssignment";
    }
    
    public String gradeAssignment(Integer submitId) throws SQLException {
        Connection con = dbConnect.getConnection();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }

        PreparedStatement preparedStatement
                = con.prepareStatement("update assignment_submit set graded = 1, grade = ? where id = ?");
        
        //get customer data from database
        preparedStatement.setInt(1, this.grade);
        preparedStatement.setInt(2, submitId);
        
        preparedStatement.executeUpdate();
        
        return "gradeAssignments";
    }
    
    public List<Assignment> getGradedAssignmentListByStudent() throws SQLException {

        Connection con = dbConnect.getConnection();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }

        PreparedStatement preparedStatement
                = con.prepareStatement("select assignment.name as assignment_name, class.name as class_name, assignment.description as assignment_description, assignment_submit.submit_date,"
                        + " assignment.due_date, assignment_submit.id as assignment_submit_id, assignment_submit.file_path, assignment_submit.grade from assignment_submit join assignment on"
                        + " assignment_submit.assignment_id = assignment.id join class on assignment.class_id = class.id where graded = 1 and assignment_submit.student_id ="
                        + " (select id from student where login = ?);");
        
        preparedStatement.setString(1, Util.getStudentLogin());
        
        ResultSet result = preparedStatement.executeQuery();

        List<Assignment> gradedAssignmentList = new ArrayList<>();

        while (result.next()) {
            Assignment assignment = new Assignment();
            
            assignment.setName(result.getString("assignment_name")); 
            
            Class cl = new Class();
            
            cl.setName(result.getString("class_name"));
            
            assignment.setCl(cl);
            
            assignment.setDescription(result.getString("assignment_description"));
            
            assignment.setAssignmentSubmitId(result.getInt("assignment_submit_id"));
            
            assignment.setSubmitDate(result.getDate("submit_date"));
            
            assignment.setDueDate(result.getDate("due_date"));
            
            assignment.setFilePath(result.getString("file_path"));
            
            assignment.setGrade(result.getInt("grade"));
   
            //store all data into a List
            gradedAssignmentList.add(assignment);
        }
        
        result.close();
        con.close();
        
        return gradedAssignmentList;
    }
    
    public Integer getAssignmentCount() throws SQLException {
        Connection con = dbConnect.getConnection();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }

        PreparedStatement preparedStatement
                = con.prepareStatement(
                        "select count(*) as count from " +
                        "student join class_schedule on student.id = class_schedule.student_id " +
                        "join class on class.id = class_schedule.class_id join assignment on class.id = assignment.class_id " +
                        "left join assignment_submit on assignment.id = assignment_submit.assignment_id " +
                        "where student.login = ? and assignment_submit.id is null");
        
        //get customer data from database
        preparedStatement.setString(1, Util.getStudentLogin());
        
        ResultSet result = preparedStatement.executeQuery();
        
        result.next();

        return result.getInt("count");
    }
    
    public void onload() {
        this.cl = new Class();
    }
    
    public String getLetterGrade(Double percentage) {
        if (percentage >= 93) {
            return "A";
        }
        else if (percentage >= 90) {
            return "A-";
        }
        else if (percentage >= 83) {
            return "B";
        }
        else if (percentage >= 80) {
            return "B-";
        }
        else if (percentage >= 73) {
            return "C";
        }
        else if (percentage >= 70) {
            return "C-";
        }
        else if (percentage >= 63) {
            return "D";
        }
        else if (percentage >= 60) {
            return "D-";
        }
        else {
            return "F";
        }
    }
    
    public String getIndividualClassStudentReportCard(String className) throws SQLException {
        String individualClassReportCardHTML = "<table class='table table-inverse table-striped'>"
                + "<tr>"
                + "<th>Class Name</th>"
                + "<th>Assignment</th>"
                + "<th>Submit Date</th>"
                + "<th>Due Date</th>"
                + "<th>Grade</th>"
                + "<th>Letter Grade</th>"
                + "</tr>";
        
        Connection con = dbConnect.getConnection();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }

        PreparedStatement preparedStatement
                = con.prepareStatement("select assignment.name as assignment_name, class.name as class_name, assignment_submit.submit_date,"
                        + " assignment.due_date, assignment_submit.grade from assignment_submit join assignment on"
                        + " assignment_submit.assignment_id = assignment.id join class on assignment.class_id = class.id where graded = 1 and assignment_submit.student_id ="
                        + " (select id from student where login = ?) and class.id = (select id from class where name = ?);");
        
        preparedStatement.setString(1, Util.getStudentLogin());
        preparedStatement.setString(2, className);
        
        ResultSet result = preparedStatement.executeQuery();
        
        while (result.next()) {
            individualClassReportCardHTML += "<tr><td>" + result.getString("class_name") + "</td><td>" + result.getString("assignment_name")
                    + "</td><td>" + result.getString("submit_date") + "</td><td>" + result.getString("due_date") + "</td>"
                    + "<td>" + result.getDouble("grade") + "</td><td>" + getLetterGrade(result.getDouble("grade")) + "</td></tr>";
        }
        
        individualClassReportCardHTML += "</table>";
        
        return individualClassReportCardHTML;
    }
    
    public String getIndividualReportCardHTML() throws SQLException {
        Connection con = dbConnect.getConnection();
        String html = "";

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }
        
        PreparedStatement preparedStatement
                = con.prepareStatement("select distinct class.name as class_name from class_schedule join class "
                                        + "on class_schedule.class_id = class.id where "
                                        + "class_schedule.student_id = (select id from student where login = ?);");
        
        preparedStatement.setString(1, Util.getStudentLogin());
        
        ResultSet result = preparedStatement.executeQuery();
        
        while (result.next()) {
            html += "<div id=\"" + result.getString("class_name") + "\" class=\"row\" style=\"display:none\">\n" +
                    "<div class=\"col-lg-12\">\n" +
                    "<div class=\"panel panel-default\">\n" +
                        "<div class=\"panel-heading\">\n" +
"                            <strong>" + result.getString("class_name") + "</strong>\n" +
"                        </div>\n" +
"                        <!-- /.panel-heading -->\n" +
"                        <div class=\"panel-body\">\n" +
                            getIndividualClassStudentReportCard(result.getString("class_name")) +
"                        </div>\n" +
"                        <!-- /.panel-body -->\n" +
"                    </div>\n" +
"                    <!-- /.panel -->\n" +
"                </div>\n" +
"                <!-- /.col-lg-12 -->\n" +
"            </div>";
        }
        
        return html;
    }
    
    public String getStudentReportCardHTML() throws SQLException {
        String reportCardHTML = "<table class='table table-inverse table-striped'>"
                + "<tr>"
                + "<th>Class</th>"
                + "<th>Percentage (%)</th>"
                + "<th>Letter Grade</th>"
                + "</tr>";
        
        Connection con = dbConnect.getConnection();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }

        PreparedStatement preparedStatement
                = con.prepareStatement("select class.name as class_name, avg(assignment_submit.grade) avg_grade"
                        + " from assignment_submit join assignment on assignment_submit.assignment_id = assignment.id"
                        + " join class on assignment.class_id = class.id where graded = 1 and assignment_submit.student_id ="
                        + " (select id from student where login = ?) group by class.name;");
        
        preparedStatement.setString(1, Util.getStudentLogin());
        
        ResultSet result = preparedStatement.executeQuery();
        
        while (result.next()) {
            reportCardHTML += "<tr><td>" + result.getString("class_name") + "</td><td>" + result.getDouble("avg_grade") + "</td><td>" + getLetterGrade(result.getDouble("avg_grade")) + "</td></tr>";
        }
        
        reportCardHTML += "</table>";
        
        return reportCardHTML;
    }
}
