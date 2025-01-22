package Bank;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BankAccount {
    private final int accNumber;
    private volatile double balance;
    private final Lock locker = new ReentrantLock(); // потокобезопасность

    BankAccount(int accNumber, double balance) {
        this.accNumber = accNumber;
        this.balance = balance;
    }

    synchronized public void deposit(double amount) { // пополнение счета
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

    synchronized public void withdraw(double amount) { // вывод средств со счета
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

    synchronized public void transfer(BankAccount target, double amount) { // перевод средств
        // необходимо синхронизировать оба класса с клиентами
        this.locker.lock();
        target.locker.lock();

        try {
            if (amount <= 0) throw new IllegalArgumentException("\nСумма не может быть отрицательной или равной нулю.\n");
            if (amount > balance) throw new IllegalArgumentException("\nНа счете недостаточно средств для снятия.\n");
            this.withdraw(amount);
            target.deposit(amount);
            System.out.printf("\nПереведено %.2f рублей со счёта %d на счёт %d.\n", amount, this.accNumber, target.accNumber);
        } finally {
            locker.unlock();
        }
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
