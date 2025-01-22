package Bank;

public class ClientRequest {
    private String typeOfOperation;
    private BankAccount accFrom;
    private BankAccount accTo;
    private double amount;

    ClientRequest(String typeOfOperation, BankAccount accFrom, BankAccount accTo, double amount) {
        this.typeOfOperation = typeOfOperation;
        this.accFrom = accFrom;
        this.accTo = accTo;
        this.amount = amount;
    }

    public void execute() {
        switch (typeOfOperation) {
            case "Пополнить" -> accTo.deposit(amount);
            case "Снять" -> accFrom.withdraw(amount);
            case "Перевести" -> accFrom.transfer(accTo, amount);
            default -> throw new IllegalStateException("\nНеизвестный тип операции: " + typeOfOperation);
        }
    }

    public String getTypeOfOperation() {
        return typeOfOperation;
    }

    public BankAccount getAccFrom() {
        return accFrom;
    }

    public BankAccount getAccTo() {
        return accTo;
    }

     public double getAmount() {
        return amount;
     }

     public void setTypeOfOperation(String type) {
        this.typeOfOperation = type;
     }

     public void setAccFrom(BankAccount acc) {
        this.accFrom = acc;
     }

     public void setAccTo(BankAccount acc) {
        this.accTo = acc;
     }

     public void setAmount(double amount) {
        this.amount = amount;
     }
}
