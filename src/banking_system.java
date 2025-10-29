

import java.net.ConnectException;
import java.sql.*;
import java.util.Scanner;

public class banking_system {
     private static final String url = "jdbc:mysql://127.0.0.1:3306/bank_system";
     private static final String username = "root";
     private static final String password = "Sonam@1108";

    public static void main(String[] args) {
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
        }
        catch (ClassNotFoundException e){
            System.out.println(e.getMessage());
        }

        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            Scanner sc = new Scanner(System.in);

            // asking the user if he is a new user or an old user :
            System.out.print("1. Sign in (New User)\t\t");
            System.out.println("2. Log In");

            int login_option = sc.nextInt();


            // if the user is new :
            if(login_option == 1){
                createUser(connection, sc);
            }
           else if (login_option == 2){
                System.out.println("Enter your user_id : ");
                int user_id = sc.nextInt();
              boolean credentials = userLogin(connection, sc, user_id);
              if(credentials){
                  System.out.print("1. Check Balance\t\t");
                  System.out.print("2. Deposit Money\t\t");
                  System.out.print("3. Withdraw Money\t\t");
                  System.out.print("4. Transfer Money\t\t");
                  System.out.print("5. Transaction History\t\t");

                  int user_operation = sc.nextInt();

                  switch (user_operation){
                      case 1 : check_balance(connection, user_id);
                      break;
                      case 2 : deposit_money(connection, sc, user_id);
                      break;
                      case 3 : withdraw(connection, sc, user_id);
                      break;
                      case 4 : money_transfer(connection, sc, user_id);
                      break;
                      case 5 : transaction_history(connection, user_id);
                      break;

                      default :
                          System.out.println("Invalid input, please try again");
                  }
              }
            }else {
               System.out.println("Please enter a valid input");
               return;
            }
            while (true) {
                // menu code

                System.out.println("1. Close the application");
                System.out.println("2. Go back to main menu");
                int choice = sc.nextInt();
                if (choice == 1) {
                    System.out.println("Thanks for using our services!");
                    break;
                }
            }


        }catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // FUNCTION TO CREATE A NEW USER:
    public static void createUser(Connection connection, Scanner sc) {
        try {
            String insertUsers = "INSERT INTO users (name, email, password) VALUES (?, ?, ?)";
            PreparedStatement insertPrep = connection.prepareStatement(insertUsers);

            while (true) {
                System.out.print("Enter your name : ");
                String name = sc.next();

                System.out.print("Enter your email : ");
                String email = sc.next();

                System.out.print("Set a new password : ");
                String password = sc.next();

                insertPrep.setString(1, name);
                insertPrep.setString(2, email);
                insertPrep.setString(3, password);
                insertPrep.executeUpdate();

                // Fetch the user_id of this newly created user
                String getUserId = "SELECT user_id FROM users WHERE email = ?";
                PreparedStatement getUserIdPrep = connection.prepareStatement(getUserId);
                getUserIdPrep.setString(1, email);
                ResultSet rs = getUserIdPrep.executeQuery();

                if (rs.next()) {
                    int newUserId = rs.getInt("user_id");

                    // Create a bank account for this new user
                    String createAccount = "INSERT INTO accounts (user_id, balance) VALUES (?, 0)";
                    PreparedStatement accPrep = connection.prepareStatement(createAccount);
                    accPrep.setInt(1, newUserId);
                    accPrep.executeUpdate();

                    System.out.println("User created successfully with user_id: " + newUserId);
                    System.out.println("Bank account created successfully for user_id: " + newUserId);
                }

                System.out.println("Do you want to add a new user? : Y/N");
                String choice = sc.next();

                if (choice.equalsIgnoreCase("N")) break;
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }



     // for login of a user :

    public static boolean userLogin(Connection connection, Scanner sc, int user_id) {
        System.out.println("Please enter your password : ");
        String password = sc.next();

        return isValid(connection, user_id, password);
    }

    // check if the login info is valid :

    public static boolean isValid(Connection connection, int user_id, String password) {
        try{
            String valid_user = "SELECT password FROM users WHERE user_id = ? ";
            PreparedStatement validPrep = connection.prepareStatement(valid_user);
            validPrep.setInt(1, user_id);
            ResultSet pass = validPrep.executeQuery();

            if(pass.next()) {
                String correct_password = pass.getString("password");   // this is the correct password from the database
                return correct_password.equals(password);
            }

        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
       return false;
    }

    // check current balance :

    public static double check_balance(Connection connection, int user_id) {
        try {
            String check_balance = "SELECT balance FROM accounts WHERE user_id = ? ";
            PreparedStatement check_prep = connection.prepareStatement(check_balance);

            check_prep.setInt(1, user_id);
            ResultSet current_balance = check_prep.executeQuery();
            if(current_balance.next()) {
                System.out.println("Your current bank balance is : " +current_balance.getInt("balance"));
                return current_balance.getInt("balance");
            }
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
        return 0;
    }

    // to deposit money in the account :

    public static void deposit_money(Connection connection, Scanner sc, int user_id){
        try{
            String deposit = "UPDATE accounts SET balance = balance + ? WHERE user_id = ?";
            PreparedStatement deposit_prep = connection.prepareStatement(deposit);

            System.out.println("Enter the amount you want to deposit : ");
            double amount = sc.nextDouble();

            deposit_prep.setDouble(1, amount);
            deposit_prep.setInt(2, user_id);

            int deposited = deposit_prep.executeUpdate();

            if(deposited > 0) {
                System.out.println("Amount : " +amount + "deposited in account of user : " +user_id);
            }else System.out.println("Amount could not be deposited");

        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }

    // to withdraw money:

    public static void withdraw(Connection connection, Scanner sc, int user_id) {
        try {
            System.out.println("Enter the amount that you want to withdraw from your account : ");
            double amount = sc.nextDouble();

            String withdraw_money = "UPDATE accounts SET balance = balance - ? WHERE user_id = ?";

            if(isSufficient(connection, sc, amount, user_id)){
                PreparedStatement withdraw_prep = connection.prepareStatement(withdraw_money);
                withdraw_prep.setDouble(1, amount);
                withdraw_prep.setInt(2, user_id);
                withdraw_prep.executeUpdate();
            }
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }

    // does the user have sufficient money in the account :

    public static boolean isSufficient(Connection connection, Scanner sc, double amount, int user_id){
            double curr_balance = check_balance(connection, user_id);   // using the check_balance function to get the value of current balance
            return curr_balance >= amount;
    }

    // to transfer money:

    public static void money_transfer(Connection connection, Scanner sc, int debit_user_id){
        try {

            System.out.println("Enter the amount you want to transfer : ");
            double amount = sc.nextDouble();

            System.out.println("Enter the user_id you want to send money to : ");
            int credit_user_id = sc.nextInt();

            connection.setAutoCommit(false);
            String debit_query = "UPDATE accounts SET balance = balance - ? WHERE user_id = ? ";
            String credit_query = "UPDATE accounts SET balance = balance + ? WHERE user_id = ? ";

            String debit_account = "SELECT account_number FROM accounts WHERE user_id = ?";
            String credit_account = "SELECT account_number FROM accounts WHERE user_id = ?";
            String update_transaction = "INSERT INTO transactions(debit_account, credit_account, amount, status) VALUES(?, ? ,?, ?) ";

            PreparedStatement debit_prep = connection.prepareStatement(debit_query);
            PreparedStatement credit_prep = connection.prepareStatement(credit_query);




            if(isSufficient(connection, sc, amount, debit_user_id)){
                debit_prep.setDouble(1, amount);
                debit_prep.setInt(2, debit_user_id);
                credit_prep.setDouble(1, amount);
                credit_prep.setInt(2, credit_user_id);

                debit_prep.executeUpdate();
                credit_prep.executeUpdate();
                System.out.println("Money transferred successfully");

                // update of transaction table :
                PreparedStatement debited_account = connection.prepareStatement(debit_account);
                PreparedStatement credited_account = connection.prepareStatement(credit_account);
                debited_account.setInt(1, debit_user_id);
                credited_account.setInt(1, credit_user_id);

                ResultSet debit_set = debited_account.executeQuery();
                ResultSet credit_set = credited_account.executeQuery();

                if(debit_set.next() && credit_set.next()){
                    int debit_account_number = debit_set.getInt("account_number");
                    int credit_account_number = credit_set.getInt("account_number");
                    // now queries to update the details in the transaction table :

                    PreparedStatement insert_transaction_prep = connection.prepareStatement(update_transaction);
                    insert_transaction_prep.setInt(1, debit_account_number);
                    insert_transaction_prep.setInt(2, credit_account_number);
                    insert_transaction_prep.setDouble(3, amount);
                    insert_transaction_prep.setString(4, "successful");
                    insert_transaction_prep.executeUpdate();
                }

                connection.commit();
            }
            else {
                System.out.println("Money transfer failed");
                connection.rollback();
            }

        } catch (SQLException e){
            System.out.println(e.getMessage());
        }

    }

    // transaction history :

    public static void transaction_history(Connection connection, int user_id){
        try{
            String show_history = "SELECT * FROM transactions WHERE user_id = ?";

            PreparedStatement history_prep = connection.prepareStatement(show_history);
            history_prep.setInt(1, user_id);
            ResultSet history = history_prep.executeQuery();
            while(history.next()){
                System.out.println("transaction_id: " +history.getInt("transaction_id"));
                System.out.println("debit_account : " +history.getInt("debit_account"));
                System.out.println("credit_account : " +history.getInt("credit_account"));
                System.out.println("amount : " +history.getDouble("amount"));
                System.out.println("status : " +history.getString("status"));
                System.out.println("created_at : " +history.getTimestamp("created_at"));
            }
        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }




}
