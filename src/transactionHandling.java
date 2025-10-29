import java.sql.*;
import java.util.Scanner;

public class transactionHandling {
    // DATABASE USED IS "transaction";
    private static final String url = "jdbc:mysql://127.0.0.1:3306/transaction";
    private static final String username = "root";
    private static final String password = "Sonam@1108";

   public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }

        try {
            Connection connection = DriverManager.getConnection(url, username, password);
            String debit_query = "UPDATE accounts SET balance = balance - ? WHERE account_number = ?";
            String credit_query = "UPDATE accounts SET balance = balance + ? WHERE account_number = ?";

            PreparedStatement debitPrep = connection.prepareStatement(debit_query);
            PreparedStatement creditPrep = connection.prepareStatement(credit_query);
            Scanner sc = new Scanner(System.in);

            // taking the amount the user wants to deduct:
            System.out.println("Enter the amount you want to deduct");
            double amount = sc.nextDouble();

            // taking the account_number from which the money is to be deducted:
            System.out.println("Enter the account number you want to deduct money from: ");
            int account_number_debit = sc.nextInt();

            // taking the account_number the user wants to send his money to :
            System.out.println("Enter the account number in which you want to transfer this amount: ");
            int account_number_credit = sc.nextInt();

            debitPrep.setDouble(1, amount);        // deducting 500 rs
            debitPrep.setInt(2, account_number_debit);       // the account number we want to deduct the money from

            creditPrep.setDouble(1, amount);   // 500 rs credited
            creditPrep.setInt(2, account_number_credit);  // the account number we want to credit money in

            // we need to stop auto_commiting of the transaction:
            connection.setAutoCommit(false);


            // only implementing the updates if the amount to be debited is less than the current balance(having sufficient balance):

            if (isSufficient(connection, account_number_debit, amount)) {
                debitPrep.executeUpdate();
                creditPrep.executeUpdate();
                connection.commit();

                System.out.println("Transaction successful");
            } else {
                connection.rollback();
                System.out.println("Transaction Failed");
            }

            debitPrep.close();  // closing all the resources at the end
            creditPrep.close();
            sc.close();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // FUNCTION TO CHECK IF WE HAVE ENOUGH BALANCE OR NOT:

    static boolean isSufficient(Connection connection, int account_number, double amount) {
        try {
            String retrieveBalance = "SELECT balance FROM accounts WHERE account_number = ?";
            PreparedStatement balancePrep = connection.prepareStatement(retrieveBalance);

            balancePrep.setInt(1, account_number);

            ResultSet rs = balancePrep.executeQuery();
            if (rs.next()) {
                double current_balance = rs.getDouble("balance");
                return amount <= current_balance;        // if the amount we want to deduct is less than balance, return true
            }
            // closing the resources:
            rs.close();
            balancePrep.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }



}
