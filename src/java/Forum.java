/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author shubham.kahal
 */
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.inject.Named;

@Named(value = "forum")
@SessionScoped
@ManagedBean
public class Forum {
    private DBConnect dbConnect = new DBConnect();
    private String comment;

    public DBConnect getDbConnect() {
        return dbConnect;
    }

    public void setDbConnect(DBConnect dbConnect) {
        this.dbConnect = dbConnect;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
    
    public String createTeacherForumHTML() throws SQLException {
        
        Connection con = dbConnect.getConnection();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }

        PreparedStatement preparedStatement
                = con.prepareStatement("select class.name as class_name, class.description as class_description, " +
                                        "class.day_schedule as day_schedule, class.start_time as start_time, " +
                                        "class.end_time as end_time, teacher.first_name as teacher_first_name, " +
                                        "teacher.last_name as teacher_last_name from class_schedule join class " +
                                        "on class_schedule.class_id = class.id join teacher on class_schedule.teacher_id = " +
                                        "teacher.id where class_schedule.student_id = (select id from student where login = ?);");
        
        preparedStatement.setString(1, Util.getStudentLogin());
        
        String tempForumHTML = 
        "<div class=\"panel-group\" id=\"accordion\">\n" +
          "<div class=\"panel panel-primary\">\n" +
          "<div class=\"panel-heading\">\n" +
            "<h4 class=\"panel-title\">\n" +
                "<a data-toggle=\"collapse\" data-parent=\"#accordion\" href=\"#collapse1\">Collapsible Group 1</a>\n" +
            "</h4>\n" +
           "</div>\n" +
           "<div id=\"collapse1\" class=\"panel-collapse collapse in\">\n" +
           "<div class=\"panel-body\">Lorem ipsum dolor sit amet, consectetur adipisicing elit,\n" +
            "sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam,\n" +
            "quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat.</div>\n" +
          "</div>\n" +
        "</div>";
    
        tempForumHTML += "</div>";
        
        return tempForumHTML;
    }
    
    public String createStudentForumHTML() throws SQLException {
        String tempForumHTML = "";
        Connection con = dbConnect.getConnection();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }

        PreparedStatement preparedStatement
                = con.prepareStatement("select class.name as class_name, class.description as class_description, " +
                                        "class.day_schedule as day_schedule, class.start_time as start_time, " +
                                        "class.end_time as end_time, teacher.first_name as teacher_first_name, " +
                                        "teacher.last_name as teacher_last_name from class_schedule join class " +
                                        "on class_schedule.class_id = class.id join teacher on class_schedule.teacher_id = " +
                                        "teacher.id where class_schedule.student_id = (select id from student where login = ?);");
        
        preparedStatement.setString(1, Util.getStudentLogin());
        
        List<Schedule> studentSchedule = (new Schedule()).getStudentSchedule();
        
        for (Schedule sched : studentSchedule) {
            tempForumHTML += 
              "<div class=\"panel-group\" id=\"accordion\">\n" +
              "<div class=\"panel panel-primary\">\n" +
              "<div class=\"panel-heading\">\n" +
                "<h4 class=\"panel-title\">\n" +
                    "<a data-toggle=\"collapse\" data-parent=\"#accordion\" href=\"#collapse1\">" + sched.getCl().getName() + "</a>\n" +
                "</h4>\n" +
                "</div>\n" +
                "<div id=\"collapse1\" class=\"panel-collapse collapse in\">\n" +
                "<div class=\"panel panel-default\">\n" +
                "<div class=\"panel-heading\">\n" +
                    "<i class=\"fa fa-clock-o fa-fw\"></i> Forum\n" +
                "</div>\n" +
                "<!-- /.panel-heading -->\n" +
                "<div class=\"panel-body\">\n" +
                "<ul class=\"timeline\" id=\"" + sched.getCl().getName() + "_forum\"" + ">" +
                    // loop and generate comments for each li
                    "<li>\n" +
                        "<div class=\"timeline-badge\"><i class=\"fa fa-check\"></i>\n" +
                        "</div>\n" +
                        "<div class=\"timeline-panel\">\n" +
                            "<div class=\"timeline-heading\">\n" +
                                "<h4 class=\"timeline-title\">Lorem ipsum dolor</h4>\n" +
                                    "<p><small class=\"text-muted\"><i class=\"fa fa-clock-o\"></i> 11 hours ago via Twitter</small>\n" +
                                    "</p>\n" +
                            "</div>\n" +
                                    "<div class=\"timeline-body\">\n" +
                                        "<p>Lorem ipsum dolor sit amet, consectetur adipisicing elit. Libero laboriosam dolor perspiciatis omnis exercitationem. Beatae, officia pariatur? Est cum veniam excepturi. Maiores praesentium, porro voluptas suscipit facere rem dicta, debitis.</p>\n" +
                                    "</div>\n" +
                        "</div>\n" +
                    "</li></ul>" +
                    "<p:commandLink value=\"Get Distance\"  oncomplete=\"PF('selectedRowValuesDlg').show()\"  process=\"@this\">        \n" +
"                  <f:attribute name=\"clinic\" value=\"#{clinic}\"/>\n" +
"            </p:commandLink>" +
                    "</div></div></div></div></div>";
            
        }
        
        return tempForumHTML;
    }
    
    public void add() throws SQLException {
        Connection con = dbConnect.getConnection();

        if (con == null) {
            throw new SQLException("Can't get database connection");
        }

        PreparedStatement preparedStatement
                = con.prepareStatement("INSERT INTO class_schedule(class_id, student_id, teacher_id) VALUES(2, 11, 1);");
        
        preparedStatement.executeUpdate();
    }
}
