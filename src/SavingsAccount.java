// Энэ class нь хадгаламжийн дансыг илэрхийлнэ.
// final: sealed Account-аас удамшсан энэ class-аас дахин удамшуулахгүй.
// Inheritance: SavingsAccount нь Account class-ийн field, method-уудыг өвлөж авна.
public final class SavingsAccount extends Account {
    private double interestRate;

    // Constructor chaining: super(...) нь эцэг Account class-ийн constructor-ийг дуудна.
    public SavingsAccount(String accountId, String name, String password, String phone, double balance) {
        super(accountId, name, password, phone, balance);
        this.interestRate = 2.5;
    }

    public double getInterestRate() {
        return interestRate;
    }

    @Override
    // Overriding: хадгаламжийн дансны төрлийг өөрийнхөөрөө буцаана.
    public String getAccountType() {
        return "Хадгаламж";
    }

    @Override
    // Overriding: эцэг class-ийн displayInfo дээр хүүгийн мэдээлэл нэмж байна.
    public String displayInfo() {
        return super.displayInfo()
                + "\nХүү: " + interestRate + "%";  
    }
}
