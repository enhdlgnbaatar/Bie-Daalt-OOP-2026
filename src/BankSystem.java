// Энэ class нь Phase 1-ийн нэршлийг хадгалах зориулалттай туслах class юм.
public class BankSystem {
    private BankService bankService;

    public BankSystem() {
        bankService = new BankService();
    }

    public BankService getBankService() {
        return bankService;
    }

    // GUI use
    public void start() {
        bankService.loadData();
        System.out.println("Энэ хувилбар нь Swing GUI ашиглана.");
    }
}
