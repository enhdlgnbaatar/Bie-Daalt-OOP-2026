// Энэ class нь харилцах дансыг илэрхийлнэ.
// final: sealed Account-аас удамшсан энэ class-аас дахин удамшуулахгүй.
// Inheritance: CurrentAccount нь Account class-ийн field, method-уудыг өвлөж авна.
public final class CurrentAccount extends Account {
    private double overdraftLimit;

    // Constructor chaining: super(...) нь эцэг Account class-ийн constructor-ийг дуудна.
    public CurrentAccount(String accountId, String name, String password, String phone, double balance) {
        super(accountId, name, password, phone, balance);
        this.overdraftLimit = 100000.0;
    }

    public double getOverdraftLimit() {
        return overdraftLimit;
    }

    @Override
    // Overriding: харилцах дансны төрлийг өөрийнхөөрөө буцаана.
    public String getAccountType() {
        return "Харилцах";
    }

    // Энэ method нь харилцах дансанд овердрафт ашиглах боломж олгоно.
    @Override
    // Overriding: CurrentAccount нь balance + overdraftLimit хүртэл зарлага гаргаж болно.
    public boolean withdraw(double amount, String description, String targetAccountId) {
        if (amount <= 0) {
            return false;
        }

        if (amount > getBalance() + overdraftLimit) {
            return false;
        }

        balance = balance - amount;
        addTransaction("ЗАРЛАГА", amount, description, targetAccountId);
        return true;
    }

    @Override
    // Overriding: эцэг class-ийн displayInfo дээр overdraft мэдээлэл нэмж байна.
    public String displayInfo() {
        return super.displayInfo()
                + "\nОвердрафт: " + overdraftLimit + " төгрөг";
    }
}
