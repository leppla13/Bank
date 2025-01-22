package Bank;

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
                }

                case 2 -> {

                }

                case 3 -> {
                    System.out.println("До свидания и до скорой встречи!");
                    fl = false;
                }
            }
        }
    }
}
