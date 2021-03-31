package nl.stokpop.money;

public class Amount {
    private String amount;

    public Amount(String amount) {
        this.amount = amount;
    }

    public String getAmount() {
        return amount;
    }

    @Override
    public String toString() {
        return amount;
    }
}
