import java.util.ArrayList;

// Энэ class нь бүх төрлийн дансны эцэг class юм.
// abstract: шууд Account object үүсгэхгүй, зөвхөн хүүхэд class-аар ашиглана.
// sealed: зөвхөн permits хэсэгт бичсэн SavingsAccount, CurrentAccount хоёр л удамшина.
// implements: AccountOperations interface-ийн method-уудыг хэрэгжүүлнэ.
public abstract sealed class Account implements AccountOperations permits SavingsAccount, CurrentAccount {
    private String accountId;
    private String name;
    private String password;
    private String phone;
    protected double balance;
    // Composition: transaction-ууд тухайн account object-д харьяалагдана.
    private ArrayList<Transaction> transactions;

    // Энэ constructor нь дансны үндсэн мэдээллийг онооно.
    // Constructor: object үүсэх үед this ашиглан field-үүдэд утга онооно.
    public Account(String accountId, String name, String password, String phone, double balance) {
        this.accountId = accountId;
        this.name = name;
        this.password = password;
        this.phone = phone;
        this.balance = balance;
        this.transactions = new ArrayList<Transaction>();
    }

    public String getAccountId() {
        return accountId;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public String getPhone() {
        return phone;
    }

    public double getBalance() {
        return balance;
    }

    public ArrayList<Transaction> getTransactions() {
        return transactions;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    // Энэ method нь хэрэглэгчийн нууц үгийг шалгана.
    public boolean checkPassword(String password) {
        if (password == null) {
            return false;
        }

        return this.password.equals(password);
    }

    // Энэ method нь дансны төрлийг буцаана.
    // Abstract method: хүүхэд class бүр өөрийн дансны төрлийг заавал тодорхойлно.
    public abstract String getAccountType();

    // Compile-time polymorphism буюу overloading: ижил нэртэй method өөр parameter-тэй.
    public boolean deposit(double amount) {
        return deposit(amount, "Deposit", "");
    }

    public boolean deposit(double amount, String description) {
        return deposit(amount, description, "");
    }

    // Энэ method нь дансанд орлого хийнэ.
    // Гол deposit method: overloaded method-ууд эцэст нь энэ method-ийг дуудна.
    public boolean deposit(double amount, String description, String targetAccountId) {
        if (amount <= 0) {
            return false;
        }

        balance = balance + amount;
        addTransaction("ОРЛОГО", amount, description, targetAccountId);
        return true;
    }

    // Compile-time polymorphism буюу overloading: withdraw method-ийн хялбар хувилбарууд.
    public boolean withdraw(double amount) {
        return withdraw(amount, "Withdraw", "");
    }

    public boolean withdraw(double amount, String description) {
        return withdraw(amount, description, "");
    }

    // Энэ method нь данснаас зарлага хийнэ.
    // Энгийн дансны зарлага хийх үндсэн logic.
    // CurrentAccount энэ method-ийг override хийж overdraft боломж нэмдэг.
    public boolean withdraw(double amount, String description, String targetAccountId) {
        if (amount <= 0) {
            return false;
        }

        if (amount > balance) {
            return false;
        }

        balance = balance - amount;
        addTransaction("ЗАРЛАГА", amount, description, targetAccountId);
        return true;
    }

    // Энэ method нь шинэ гүйлгээ үүсгэж жагсаалтад нэмнэ.
    public void addTransaction(String type, double amount, String description, String targetAccountId) {
        Transaction transaction = new Transaction(accountId, type, amount, description, targetAccountId);
        transactions.add(transaction);
    }

    // Энэ method нь файлаас уншсан гүйлгээг шууд нэмнэ.
    public void addLoadedTransaction(Transaction transaction) {
        transactions.add(transaction);
    }

    // Энэ method нь polymorphism харуулах дэлгэрэнгүй мэдээлэл бэлтгэнэ.
    // Runtime polymorphism: Account төрлөөр дуудагдсан ч subclass-ийн override method ажиллана.
    public String displayInfo() {
        // getAccountType() нь бодит object-ийн төрлөөс хамаарч Savings/Current утга буцаана.
        return "Дансны ID: " + accountId
                + "\nНэр: " + name
                + "\nУтас: " + phone
                + "\nТөрөл: " + getAccountType()
                + "\nҮлдэгдэл: " + balance + " төгрөг"
                + "\nГүйлгээний тоо: " + transactions.size();
    }
}
