
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import javax.annotation.ManagedBean;
import javax.faces.application.FacesMessage;
import javax.faces.bean.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.inject.Named;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author shubham.kahal
 */
@Named(value = "reservationvalidation")
@RequestScoped
@ManagedBean
public class ReservationValidation {
    private DBConnect dbConnect = new DBConnect();
    private UIInput viewUI;
    private UIInput typeUI;
    private UIInput startDateUI;
    
    public UIInput getViewUI() {
        return viewUI;
    }

    public void setViewUI(UIInput viewUI) {
        this.viewUI = viewUI;
    }

    public UIInput getTypeUI() {
        return typeUI;
    }

    public void setTypeUI(UIInput typeUI) {
        this.typeUI = typeUI;
    }

    public UIInput getStartDateUI() {
        return startDateUI;
    }

    public void setStartDateUI(UIInput startDateUI) {
        this.startDateUI = startDateUI;
    }
    
    
    public void validateReservation(FacesContext context, UIComponent component, Object value)
          throws ValidatorException, SQLException {

      String submittedView = (String) viewUI.getLocalValue();
      String submittedType = (String) typeUI.getLocalValue();
      Date submittedStartDate = (Date) startDateUI.getLocalValue(); 
      Date submittedEndDate = (Date) value;
      Connection con = dbConnect.getConnection();
      int count;
      
      if (submittedStartDate.after(submittedEndDate)) {            
          FacesMessage errorMessage = new FacesMessage("The start date cannot be greater than the end date.");
          throw new ValidatorException(errorMessage);
      }

      if (con == null) {
          throw new SQLException("Can't get database connection");
      }

      con.setAutoCommit(false);
      
      PreparedStatement preparedStatement = con.prepareStatement("select count(*) count from room left join "
                + "reservation on room.room_number = reservation.room_number where reservation.room_number is null and view = ? and type = ?");
        preparedStatement.setString(1, submittedView);
        preparedStatement.setString(2, submittedType);
        
        ResultSet result = preparedStatement.executeQuery();

        result.next();
        
        count = result.getInt("count");
        
        // no free room available, so we need to do a deeper check with the dates
        if (count == 0) {
            preparedStatement = con.prepareStatement("select count(*) count from room join "
              + "reservation on room.room_number = reservation.room_number where view = ? and type = ? "
              + "and ((? >= start_date and ? <= end_date) or (? >= start_date and ? <= end_date))");
      
            preparedStatement.setString(1, submittedView);
            preparedStatement.setString(2, submittedType);
            preparedStatement.setDate(3, new java.sql.Date(submittedStartDate.getTime()));
            preparedStatement.setDate(4, new java.sql.Date(submittedStartDate.getTime()));
            preparedStatement.setDate(5, new java.sql.Date(submittedEndDate.getTime()));
            preparedStatement.setDate(6, new java.sql.Date(submittedEndDate.getTime()));

            result = preparedStatement.executeQuery();

            result.next();

            count = result.getInt("count");

            result.close();
            con.close();

            if (count > 0) {
                FacesMessage errorMessage = new FacesMessage("This reservation is not available, please pick another one.");
                throw new ValidatorException(errorMessage);
            }
        }
  }
}
