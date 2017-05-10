/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author shubham.kahal
 */
import java.util.List;
import javax.annotation.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.inject.Named;

@Named(value = "schedule")
@SessionScoped
@ManagedBean
public class Schedule {
    Class cl = new Class();
    Student student = new Student();
    Teacher teacher = new Teacher();

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

    public Teacher getTeacher() {
        return teacher;
    }

    public void setTeacher(Teacher teacher) {
        this.teacher = teacher;
    }
    
    public void addClass() {
        
    }
    
    public List<Schedule> getSchedule() {
        
        return null;
    }
}
