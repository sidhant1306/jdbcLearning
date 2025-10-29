import java.sql.*;

public class Main {
    // DATABASE USED IS "school";
    private static final String url = "jdbc:mysql://127.0.0.1:3306/school";
    private static final String username = "root";
    private static final String password = "Sonam@1108";
     static void main(String[] args) {
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
        }
        catch (ClassNotFoundException e){
            System.out.println(e.getMessage());
        }

        try{


            Connection connection = DriverManager.getConnection(url, username, password);
            Statement statement = connection.createStatement();


            // everytime you run this code, we are inserting the data again and again so there will be too many rahul and mahak everytime, like they will keep adding on
            // so it's just to use when testing the code,truncate the table, delete the content:
//            String truncate = "TRUNCATE TABLE employee;";
//            statement.executeUpdate(truncate);


            // ********************************* CREATING A TABLE SONAM ********************************************


            // let's create a table and insert some data into it:
            // we may run code multiple times, so drop the sonam table everytime when we create it :
            // we need to wrap these statements in try and catch :

            String dropSonam = "DROP TABLE IF EXISTS SONAM ;";
            statement.executeUpdate(dropSonam); // we do not need to print that it is dropped because we are just doing it because we may run the program multiple times
            System.out.println("Table sonam dropped successfully");


            // creating the table now:
            String createSonam = "CREATE TABLE IF NOT EXISTS SONAM" +
                    "(id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "name VARCHAR(250) NOT NULL, " +
                    "age INT NOT NULL, " +
                    "marks DOUBLE NOT NULL);";

            statement.executeUpdate(createSonam);
            System.out.println("Table created : SONAM");

            //************************ INSERT into sonam: ***********************************

            String insertSonam = "INSERT INTO SONAM(name, age, marks) VALUES" +
                    "('Sonam', 19, 92), " +
                    "('Sidhant', 20, 80), " +
                    "('Mahak', 24, 50), " +
                    "('Arpit', 21, 82), " +
                    "('Bhatia', 22, 65);" ;

            int insertedSonam = statement.executeUpdate(insertSonam);
            if(insertedSonam > 0) System.out.println("Data inserted in Sonam successfully") ;
            else System.out.println("Data could not be inserted into Sonam");
            // ***************************** PRINT SONAM *********************************
            // when we want to print some data in tabular format:
            String query = "SELECT * FROM SONAM;";  // the query we want to execute.SONAM is the table name.
            ResultSet resultSet = statement.executeQuery(query);    // resultset is an interface which holds the output, by using executeQuery command, we are executing the query which is to print the table, but there is nothing to hold the output table, so we use resultSet.
            while(resultSet.next()){        //.next means there is something present after the current position.
                // now we need to access the columns, but these are present inside the sql queries.
                // so we create their local instances.
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                int age = resultSet.getInt("age");
                double marks = resultSet.getDouble("marks");

                System.out.println("ID : " +id );   // it's in a while loop, so we are printing the data for each student.
                System.out.println("name : " +name);
                System.out.println("age : " +age);
                System.out.println("marks : " +marks);
            }
//
//
//            // now what if we want to update some data: (using a different table than SONAM).
//            // now we already created a table with name employees in the database, you can view it by "describe employees":
            // Let's just drop the already created employee table and create a new one everytime:

            // ******************************* DROPPING EMPLOYEE TABLE ******************************************
            String dropEmployees = "DROP TABLE IF EXISTS employees;";
            statement.executeUpdate(dropEmployees);
            System.out.println("employees table deleted successfully");

            // ******************************* CREATING EMPLOYEE TABLE ******************************************

            String createEmployees = "CREATE TABLE IF NOT EXISTS employees" +
                    "(emp_id INT AUTO_INCREMENT PRIMARY KEY ," +
                    "emp_name VARCHAR(250) NOT NULL, " +
                    "emp_age INT NOT NULL, " +
                    "emp_salary DOUBLE NOT NULL);";

            statement.executeUpdate(createEmployees);        //CREATE TABLE does not return the number of affected rows.
            System.out.println("Table created : employees"); //So your if(employeesCreated > 0) check will never be true, which is why it prints "Table employees could not be created" even when it works.


            // now we want to insert some data in the table: so we have to format the string to write multiple rows of commands:
            //****************************** INSERTING INTO EMPLOYEE ***********************************
            String query2 = "INSERT INTO employees(emp_name, emp_age, emp_salary) VALUES" +"('Rahul', 22, 1038.77)," + "('Mahak', 24, 1899.8);";
            // now in the table we have id, name, age and salary, but as id is auto_incrementing so we are not inserting the values of id
            // %s is used to get string value, %o is used to get integer value, %f is used to get float values.

            int rowsAffected = statement.executeUpdate(query2);     // now here we use executeUpdate instead of executeQuery because we are updating the table
            // we are storing it in an int because the as an output we get the number of rows which are affected not a whole table
            // ***** IMPORTANT: rows affected means that the input was executed, like when we run an sql command on the workbench and if it is executed correctly,
            // the command line returns a line for how many rows are affected, so if we get some number in the int rowsAffected,
            // it means some rows were affected by our input
            // we do not need while loop for this because we are not getting a table
            // so as some rows were affected, the rows will only be affected when our input is executed

            if(rowsAffected > 0) {      // some rows were affected when our input executed
                System.out.println("Data inserted Successfully");
            } else System.out.println("Data not inserted");

            // now view the table in which you inserted data just like you did for the previous table before.
            // ***************************** PRINTING EMPLOYEE ***********************************
            String viewQuery = "SELECT * FROM employees;";

            ResultSet result = statement.executeQuery(viewQuery);
            System.out.println();
            System.out.println("***** printing the employee table *****");
            while(result.next()){
                int emp_id = result.getInt("emp_id");
                String emp_name = result.getString("emp_name");
                int emp_age = result.getInt("emp_age");
                double emp_salary = result.getDouble("emp_salary");
                System.out.println(" ** Next employee : ");
                System.out.println("employee id : " + emp_id);
                System.out.println("employee name : " + emp_name);
                System.out.println("employee age : " + emp_age);
                System.out.println("employee salary : " + emp_salary);

            }

            // NOW WHAT IF WE WANT TO UPDATE(CHANGE) THE VALUE OF SALARY OF AN EMPLOYEE :
            //*********************** UPDATE SALARY OF EMPLOYEE ***********************************
            String updateQuery1 = String.format("UPDATE employees SET emp_salary = %f WHERE emp_name = '%s'", 878.9, "Mahak"); // now in this we can also use format, %s for string, %d for integer
            int dataChanged = statement.executeUpdate(updateQuery1);

            if(dataChanged > 0) System.out.println("Salary changed successfully");
            // ******************************* PRINTING THE UPDATED SALARY *********************************
            String mahak_salary = "SELECT emp_salary FROM employees WHERE emp_name = 'Mahak';";
            ResultSet rs = statement.executeQuery(mahak_salary);
            if(rs.next()){
                float salary = rs.getFloat("emp_salary");
                System.out.println("mahak current salary : " + salary);
            }

            System.out.println("***** Deleting operation ******");

            // what if now we want to delete an entry:
            // ***************************** DELETING AN ENTRY **************************************
            // at first, let's just add some temporary data to the SONAM table:
            //************************** ADDING A TEMPORARY ENTRY IN SONAM ********************************
            String addSonam = "INSERT INTO SONAM(name, age, marks) VALUES" + "('Kunal', 17, 80.5);";

            int addedSonam = statement.executeUpdate(addSonam);

            if(addedSonam > 0) System.out.println("data inserted successfully in Sonam table");
            else System.out.println("data could not be inserted in the sonam table");

            String deleteQuery = "DELETE FROM SONAM WHERE name = 'Kunal';";    // deleting the entry(query);

            int dataDeleted = statement.executeUpdate(deleteQuery);

            if(dataDeleted > 0){
                System.out.println("Data deleted successfully");
            }else System.out.println("Data not deleted");

        }catch (SQLException e){
            System.out.println(e.getMessage());
        }


    }
}

