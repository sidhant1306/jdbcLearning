import com.mysql.cj.x.protobuf.MysqlxCrud;

import java.sql.*;

public class prepared {
    // DATABASE USED IS "house";
    private static final String url = "jdbc:mysql://127.0.0.1:3306/house";
    private static final String username = "root";
    private static final String password = "Sonam@1108";

     static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
        try {

            Connection connection = DriverManager.getConnection(url, username, password);
            // prepared statement:
            //in prepared statement the query we want to execute is compiled only once and we just change the data in it each time,
            // to get different output

            // we need the general query for the prepared Statement with not hard coded values:
            // using a new table "family" with currently no values in it
            String query1 = "INSERT INTO family (iq, strength) VALUES(?, ?)";
            PreparedStatement preparedStatement = connection.prepareStatement(query1);   // here the query is compiled

            // now we need to execute it:

            preparedStatement.setInt(1, 100);       // 1 means at the first position i.e. iq in this case
            preparedStatement.setInt(2, 100);       // 2 means at the second position i.e. strength in this case

            int familyInserted = preparedStatement.executeUpdate();
            if(familyInserted > 0) {
                System.out.println("data inserted successfully");
            }else System.out.println("Data not inserted");

            // now we will perform all CRUD operations on table using prepared statements on table "members".


            // ************************ RETRIEVE OPERATION *************************************

            String retrieveData = "SELECT * FROM members WHERE name = ?;";

            PreparedStatement preparedRetrieve = connection.prepareStatement(retrieveData);
            preparedRetrieve.setString(1, "mahak"); // we want to retrieve data where name is mahak

            ResultSet retrieveResult = preparedRetrieve.executeQuery();
            if(retrieveResult.next()){
                System.out.println("name : " +retrieveResult.getString("name"));
                System.out.println("age: " +retrieveResult.getInt("age"));
                System.out.println("Salary: " +retrieveResult.getInt("income"));
            }

            // ************************* INSERT DATA INTO members(adding a new member) ***************************

            String insertMember = "INSERT INTO members (name, age, income) VALUES (?, ?, ?)";

            PreparedStatement preparedInsert = connection.prepareStatement(insertMember);

            preparedInsert.setString(1, "Sonam");
            preparedInsert.setInt(2, 19);
            preparedInsert.setInt(3, 2000);

            int dataInserted = preparedInsert.executeUpdate();
            if(dataInserted > 0) {
                System.out.println("New member added");
            } else System.out.println("New member could not be added");


            // now using prepared statement we can directly retrieve data of this new member:

            preparedRetrieve.setString(1, "Sonam");
            retrieveResult = preparedRetrieve.executeQuery();       // we update the values of retrieveResult here
            if(retrieveResult.next()){
                System.out.println("name : " +retrieveResult.getString("name"));
                System.out.println("age: " +retrieveResult.getInt("age"));
                System.out.println("Salary: " +retrieveResult.getInt("income"));
            }

            // ********************* DELETING THE DATA EVERYTIME TO AVOID ADDING THE NEW MEMBER WITH EACH RUN ******************

            String deleteMember = "DELETE FROM members WHERE name = ?";

            PreparedStatement preparedDelete = connection.prepareStatement(deleteMember);
            preparedDelete.setString(1, "Sonam");

            int memberDelete = preparedDelete.executeUpdate();

            if(memberDelete > 0) {
                System.out.println("Member deleted successfully");
            }else System.out.println("Member could not be deleted, No record found");

        // ********************** UPDATING SOME VAUES ***********************************

            String UpdateMember = "UPDATE members SET income = ? WHERE name = ?";
            PreparedStatement preparedUpdate = connection.prepareStatement(UpdateMember);

            preparedUpdate.setInt(1, 50);   // setting the new income
            preparedUpdate.setString(2, "mahak");   // name of the member whose income we want to update

            int updatedMember = preparedUpdate.executeUpdate();
            if(updatedMember > 0) {
                System.out.println("Data updated successfully");
            }else System.out.println("Data could not be updated");

            // check if the income was updated:
            preparedRetrieve.setString(1, "mahak");
            retrieveResult = preparedRetrieve.executeQuery();
            if(retrieveResult.next()) {
                System.out.println("name : " +retrieveResult.getString("name"));
                System.out.println("age: " +retrieveResult.getInt("age"));
                System.out.println("Salary: " +retrieveResult.getInt("income"));
            }


            // ************************************* TAKING INPUTS FROM THE USER ************************************


        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }
}
