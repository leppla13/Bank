package Bank;

import java.util.concurrent.Semaphore;

public class ATM {
    Semaphore semaphore = new Semaphore(1); // только 1 человек может работать с банкоматом

    public void processRequest(ClientRequest request) {
        try {
            semaphore.acquire(); // блок банкомата, пока он используется одним человеком
            System.out.printf("Обрабатывается запрос \"%s\".", request.getTypeOfOperation());
            request.execute();
            System.out.println("Запрос завершён.\n");
        } catch (InterruptedException e) {
            System.out.printf("\nПоток %s был прерван.", Thread.currentThread().getName());
            Thread.currentThread().interrupt();
        } finally {
            semaphore.release(); // освобождение банкомата
        }
    }
}
