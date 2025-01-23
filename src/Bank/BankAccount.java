package Bank;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BankAccount {
    private static int accNumber = 0;
    private static volatile double balance;
    private static final Lock locker = new ReentrantLock(); // потокобезопасность

    BankAccount(int accNumber, double balance) {
        BankAccount.accNumber = accNumber;
        BankAccount.balance = balance;
    }

    synchronized static public void depositMoney(Scanner sc, double amount) { // пополнение счета
        locker.lock();
        try {
            if (amount <= 0) throw new IllegalArgumentException("Сумма не может быть отрицательной или равной нулю.");
            balance += amount;
            System.out.printf("\nСчёт %d пополнен на %.2f рублей." +
                    "\nТекущий баланс: %.2f рублей\n", accNumber, amount, balance);
        } finally {
            locker.unlock();
        }
    }

    synchronized static public void withdrawMoney(Scanner sc, double amount) { // вывод средств со счета
        locker.lock();

        try {
            if (amount <= 0) throw new IllegalArgumentException("\nСумма не может быть отрицательной или равной нулю.\n");
            if (amount > balance) throw new IllegalArgumentException("\nНа счете недостаточно средств для снятия.\n");
            balance -= amount;
            System.out.printf("\nСо счёта %d было снято %.2f рублей." +
                    "\nТекущий баланс: %.2f рублей\n", accNumber, amount, balance);
        } finally {
            locker.unlock();
        }
    }

//    synchronized public void transfer(BankAccount target, double amount) { // перевод средств
//        // необходимо синхронизировать оба класса с клиентами
//        this.locker.lock();
//        target.locker.lock();
//
//        try {
//            if (amount <= 0) throw new IllegalArgumentException("\nСумма не может быть отрицательной или равной нулю.\n");
//            if (amount > balance) throw new IllegalArgumentException("\nНа счете недостаточно средств для снятия.\n");
//            this.withdraw(amount);
//            target.deposit(amount);
//            System.out.printf("\nПереведено %.2f рублей со счёта %d на счёт %d.\n", amount, this.accNumber, target.accNumber);
//        } finally {
//            locker.unlock();
//        }
//    }

    static void transferMoney(Scanner sc, int userId) {
        System.out.print("Введите ID получателя: ");
        int receiverId = sc.nextInt();
        System.out.print("Сумма: ");
        double amount = sc.nextDouble();

        CompletableFuture.runAsync(() -> {
            try (Connection conn = Database.getConnection()) {
                BankAccount from = loadAccount(userId);
                BankAccount to = loadAccount(receiverId);
                ClientRequest request = new ClientRequest("Перевести", from, to, amount);
                new BankSimulator().processClientRequestAsync(request).join();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    private static BankAccount loadAccount(int userId) throws IOException {
        try (Connection conn = Database.getConnection()) {
            PreparedStatement preparedStatement = conn.prepareStatement("SELECT * FROM accounts WHERE user_id = ?");
            preparedStatement.setInt(1, userId);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                return new BankAccount(rs.getInt("id"), rs.getDouble("balance"));
            }
            throw new SQLException("Аккаунт не найден.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return BankAccount.loadAccount(0);
    }

    synchronized public double checkBalance() {
        locker.lock();

        try {
            return balance;
        } finally {
            locker.unlock();
        }
    }
}
