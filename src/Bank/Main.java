package Bank;

import javax.xml.crypto.Data;
import java.sql.*;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        BankSimulator simulator = new BankSimulator();
        simulator.addATM(new ATM());
        simulator.addATM(new ATM());

        boolean fl = true;
        Scanner sc = new Scanner(System.in);

        while(fl) {
            simulator.simulate();

            System.out.println("""
                    Добро пожаловать в Банк34.
                    Выберите цифру с нужным действием:
                    1. Зарегистрироваться
                    2. Войти
                    3. Выход
                    """);

            int choice = sc.nextInt();
            switch (choice) {
                case 1 -> {
                    System.out.print("\nИмя (Например, Иван): ");
                    String name = sc.next();
                    System.out.print("\nФамилия (Например, Иванов): ");
                    String lastName = sc.next();
                    System.out.print("\nОтчество (Например, Иванович): ");
                    String middleName = sc.next();
                    System.out.print("\nДата рождения (Например, 01.01.2000): ");
                    String birthday = sc.next();
                    System.out.print("\nЛогин: (Например, ivan34rus): ");
                    String login = sc.next();
                    System.out.print("\nПароль: (Например, ivan123): ");
                    String password = sc.next();

                    try (Connection conn = Database.getConnection()) {
                        String sql = "INSERT INTO users (name, last_name, middle_name, birthday, login, password) VALUES (?, ?, ?, ?, ?, ?)";
                        PreparedStatement preparedStatement = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
                        preparedStatement.setString(1, name);
                        preparedStatement.setString(2, lastName);
                        preparedStatement.setString(3, middleName);
                        preparedStatement.setDate(4, Date.valueOf(birthday.replace('.', '-')));
                        preparedStatement.setString(5, login);
                        preparedStatement.setString(6, password);
                        preparedStatement.executeUpdate();

                        ResultSet rs = preparedStatement.getGeneratedKeys();
                        if (rs.next()) {
                            int userId = rs.getInt(1);
                            // создание аккаунта
                            BankAccount account = new BankAccount(userId, 0);
                            String accSql = "INSERT INTO accounts (user_id, balance) VALUES (?, ?)";
                            PreparedStatement accStatement = conn.prepareStatement(accSql);
                            accStatement.setInt(1, userId);
                            accStatement.setDouble(2, 0);
                            accStatement.executeUpdate();
                        }
                        System.out.println("Регистрация прошла успешно.");
                    } catch (SQLException e) {
                        System.out.println("Ошибка регистрации: " + e.getMessage());
                    }
                }

                case 2 -> {
                    System.out.print("Введите логин: ");
                    String login = sc.next();
                    System.out.print("Введите пароль: ");
                    String password = sc.next();

                    try (Connection conn = Database.getConnection()) {
                        String sql = "SELECT * FROM users WHERE login = ? AND password = ?";
                        PreparedStatement preparedStatement = conn.prepareStatement(sql);
                        preparedStatement.setString(1, login);
                        preparedStatement.setString(2, password);
                        ResultSet rs = preparedStatement.executeQuery();

                        if (rs.next()) {
                            System.out.println("Вход выполнен успешно.");
                            userMenu(sc, rs.getInt("id"));
                        }

                        else {
                            System.out.println("Введены неверные данные.");
                        }
                    } catch (SQLException e) {
                        System.out.println("Ошибка входа: " + e.getMessage());
                    }
                }

                case 3 -> {
                    System.out.println("До свидания и до скорой встречи!");
                    fl = false;
                }
            }
        }
    }

    private static void userMenu(Scanner sc, int userId) {
        boolean loggedIn = true;
        while (loggedIn) {
            System.out.println("""
            1. Перевод
            2. Пополнить
            3. Снять
            4. Выход
            Выберите действие:""");

            int choice = sc.nextInt();
            switch (choice) {
                case 1 -> transferMoney(sc, userId);
                case 2 -> depositMoney(sc, userId);
                case 3 -> withdrawMoney(sc, userId);
                case 4 -> loggedIn = false;
            }
        }
    }
}
