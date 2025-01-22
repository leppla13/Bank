package Bank;

import java.util.ArrayList;
import java.util.concurrent.*;

public class BankSimulator {
    private final ExecutorService executorService = Executors.newFixedThreadPool(2); // всего 2 банкомата
    private final ArrayList<ATM> atms = new ArrayList<>();

    public void addATM(ATM atm) {
        atms.add(atm);
    }

    public CompletableFuture<Void> processClientRequestAsync(ClientRequest request) {
        ATM atm = atms.get(request.hashCode() % atms.size()); // выбор банкомата на основе хеш-кода
        return CompletableFuture.runAsync(() -> atm.processRequest(request), executorService);
    }

    public void simulate() {
        System.out.println("=====Начата симуляция=====\n");

        BankAccount acc1 = new BankAccount(1, 1000);
        BankAccount acc2 = new BankAccount(2, 2000);

        ClientRequest request1 = new ClientRequest("Пополнить", null, acc1, 300);
        ClientRequest request2 = new ClientRequest("Снять", acc1, null, 200);
        ClientRequest request3 = new ClientRequest("Перевести", acc1, acc2, 100);

        try {
            CompletableFuture.runAsync(() -> processClientRequestAsync(request1).join(), executorService).get();
            CompletableFuture.runAsync(() -> processClientRequestAsync(request2).join(), executorService).get();
            CompletableFuture.runAsync(() -> processClientRequestAsync(request3).join(), executorService).get();
        } catch (InterruptedException | ExecutionException e) {
            System.out.println("\nОшибка при выполнении запросов: " + e.getMessage());
            Thread.currentThread().interrupt();
        } finally {
            System.out.println("=====Симуляция завершена=====");
            executorService.shutdown();
        }
    }
}
