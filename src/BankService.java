import java.io.File;
import java.util.ArrayList;

// Энэ class нь системийн бүх үндсэн логикийг нэг дор удирдана.
public class BankService {
    private static final String ADMIN_USERNAME = "admin17";
    private static final String ADMIN_PASSWORD = "OOP1";

    private Bank bank;
    private Validator validator;
    private FileManager fileManager;
    private boolean createdFilesOnStart;

    public BankService() {
        String basePath = System.getProperty("user.dir");
        String accountsPath = basePath + File.separator + "accounts.csv";
        String transactionsPath = basePath + File.separator + "transactions.csv";

        bank = new Bank("Оюутны банкны систем");
        validator = new Validator();
        fileManager = new FileManager(accountsPath, transactionsPath);
        createdFilesOnStart = fileManager.ensureFilesExist();
    }

    public Bank getBank() {
        return bank;
    }

    // Энэ method нь програм эхлэхэд бүх мэдээллийг уншина.
    public OperationResult loadData() {
        ArrayList<Account> loadedAccounts = fileManager.loadAccounts();

        if (loadedAccounts == null) {
            return new OperationResult(false, "accounts.csv файлаас мэдээлэл уншихад алдаа гарлаа.");
        }

        bank.setAccounts(loadedAccounts);

        boolean transactionLoaded = fileManager.loadTransactions(bank.getAccounts());

        if (!transactionLoaded) {
            return new OperationResult(false, "transactions.csv файлаас мэдээлэл уншихад алдаа гарлаа.");
        }

        if (createdFilesOnStart && bank.getAccounts().size() == 0) {
            createSampleData();
            saveData();
            return new OperationResult(true, "Жишээ өгөгдөл автоматаар үүслээ.");
        }

        return new OperationResult(true, "Файлын мэдээлэл амжилттай ачааллаа.");
    }

    // Энэ method нь бүх мэдээллийг файлд хадгална.
    public OperationResult saveData() {
        boolean result = fileManager.saveAll(bank);

        if (result) {
            return new OperationResult(true, "Мэдээлэл CSV файлд хадгалагдлаа.");
        }

        return new OperationResult(false, "CSV файлд хадгалах үед алдаа гарлаа.");
    }

    // Энэ method нь админ нэвтрэлтийг шалгана.
    public OperationResult adminLogin(String username, String password) {
        if (ADMIN_USERNAME.equals(username) && ADMIN_PASSWORD.equals(password)) {
            return new OperationResult(true, "Админ амжилттай нэвтэрлээ.");
        }

        return new OperationResult(false, "Админы нэр эсвэл нууц үг буруу байна.");
    }

    // Энэ method нь хэрэглэгчийн нэвтрэлтийг шалгана.
    public OperationResult userLogin(String accountId, String password) {
        if (!validator.isValidAccountId(accountId) || !validator.isValidPassword(password)) {
            return new OperationResult(false, "Дансны ID эсвэл нууц үг хоосон байна.");
        }

        Account account = bank.findAccountById(accountId.trim());

        if (account == null) {
            return new OperationResult(false, "Ийм данс олдсонгүй.");
        }

        if (!account.checkPassword(password)) {
            return new OperationResult(false, "Нууц үг буруу байна.");
        }

        return new OperationResult(true, "Хэрэглэгч амжилттай нэвтэрлээ.", account);
    }

    // Энэ method нь шинэ данс үүсгэнэ.
    public OperationResult createAccount(String type, String accountId, String name,
                                         String password, String phone, double balance) {
        if (!validator.isValidAccountId(accountId)) {
            return new OperationResult(false, "Дансны ID хоосон байна.");
        }

        if (!validator.isValidText(name)) {
            return new OperationResult(false, "Нэр хоосон байна.");
        }

        if (!validator.isValidPassword(password)) {
            return new OperationResult(false, "Нууц үг хоосон байна.");
        }

        if (!validator.isValidPhone(phone)) {
            return new OperationResult(false, "Утасны дугаар хоосон байна.");
        }

        if (!validator.isValidStartBalance(balance)) {
            return new OperationResult(false, "Эхний үлдэгдэл буруу байна.");
        }

        if (bank.findAccountById(accountId.trim()) != null) {
            return new OperationResult(false, "Ийм ID-тай данс бүртгэлтэй байна.");
        }

        Account account;

        if ("Хадгаламж".equals(type)) {
            account = new SavingsAccount(accountId.trim(), name.trim(), password, phone.trim(), 0.0);
        } else {
            account = new CurrentAccount(accountId.trim(), name.trim(), password, phone.trim(), 0.0);
        }

        bank.addAccount(account);

        if (balance > 0) {
            account.deposit(balance, "Эхний үлдэгдэл", "");
        }

        OperationResult saveResult = saveData();

        if (!saveResult.isSuccess()) {
            return new OperationResult(false, "Данс үүссэн боловч файлд хадгалах үед алдаа гарлаа.");
        }

        return new OperationResult(true, "Шинэ данс амжилттай нэмэгдлээ.", account);
    }

    // Энэ method нь админ данс устгана.
    public OperationResult deleteAccount(String accountId) {
        if (!validator.isValidAccountId(accountId)) {
            return new OperationResult(false, "Устгах дансны ID хоосон байна.");
        }

        boolean removed = bank.removeAccount(accountId.trim());

        if (!removed) {
            return new OperationResult(false, "Устгах данс олдсонгүй.");
        }

        OperationResult saveResult = saveData();

        if (!saveResult.isSuccess()) {
            return new OperationResult(false, "Данс устсан боловч файлд хадгалах үед алдаа гарлаа.");
        }

        return new OperationResult(true, "Данс амжилттай устгагдлаа.");
    }

    // Энэ method нь хэрэглэгч өөрийн дансыг устгана.
    public OperationResult deleteOwnAccount(Account account, String password) {
        if (account == null) {
            return new OperationResult(false, "Нэвтэрсэн хэрэглэгч олдсонгүй.");
        }

        if (!account.checkPassword(password)) {
            return new OperationResult(false, "Нууц үг баталгаажсангүй.");
        }

        return deleteAccount(account.getAccountId());
    }

    // Энэ method нь өөрийн нууц үгийг солино.
    public OperationResult changePassword(Account account, String oldPassword, String newPassword) {
        if (account == null) {
            return new OperationResult(false, "Нэвтэрсэн хэрэглэгч олдсонгүй.");
        }

        if (!account.checkPassword(oldPassword)) {
            return new OperationResult(false, "Хуучин нууц үг буруу байна.");
        }

        if (!validator.isValidPassword(newPassword)) {
            return new OperationResult(false, "Шинэ нууц үг хоосон байна.");
        }

        account.setPassword(newPassword);
        OperationResult saveResult = saveData();

        if (!saveResult.isSuccess()) {
            return new OperationResult(false, "Нууц үг солигдсон боловч файлд хадгалах үед алдаа гарлаа.");
        }

        return new OperationResult(true, "Нууц үг амжилттай солигдлоо.", account);
    }

    // Энэ method нь нэг дансны мэдээллийг буцаана.
    public Account refreshAccount(String accountId) {
        return bank.findAccountById(accountId);
    }

    // Энэ method нь админ эсвэл хэрэглэгчид дансны мэдээлэл өгнө.
    public OperationResult getAccountInfo(String accountId) {
        Account account = bank.findAccountById(accountId);

        if (account == null) {
            return new OperationResult(false, "Данс олдсонгүй.");
        }

        return new OperationResult(true, account.displayInfo(), account);
    }

    // Энэ method нь нэр эсвэл ID-аар хайлт хийнэ.
    public ArrayList<Account> searchAccounts(String keyword) {
        if (!validator.isValidText(keyword)) {
            return new ArrayList<Account>();
        }

        return bank.searchAccounts(keyword.trim());
    }

    // Энэ method нь хайлтын үр дүнг текст болгон буцаана.
    public String buildSearchResultText(String keyword) {
        ArrayList<Account> results = searchAccounts(keyword);

        if (results.size() == 0) {
            return "Хайлтад тохирох данс олдсонгүй.";
        }

        StringBuilder builder = new StringBuilder();
        builder.append("Хайлтын үр дүн: ").append(results.size()).append(" данс");

        for (int i = 0; i < results.size(); i++) {
            builder.append("\n------------------------------\n");
            builder.append(results.get(i).displayInfo());
        }

        return builder.toString();
    }

    // Энэ method нь өөрийн дансанд орлого хийнэ.
    public OperationResult depositToOwnAccount(Account account, double amount) {
        if (account == null) {
            return new OperationResult(false, "Данс олдсонгүй.");
        }

        if (!validator.isValidAmount(amount)) {
            return new OperationResult(false, "Орлогын дүн буруу байна.");
        }

        boolean result = account.deposit(amount, "Дансандаа мөнгө хийлээ", "");

        if (!result) {
            return new OperationResult(false, "Орлого хийх боломжгүй байна.");
        }

        OperationResult saveResult = saveData();

        if (!saveResult.isSuccess()) {
            return new OperationResult(false, "Орлого хийгдсэн боловч файлд хадгалахад алдаа гарлаа.");
        }

        return new OperationResult(true, "Орлого амжилттай хийгдлээ.", account);
    }

    // Энэ method нь өөрийн данснаас зарлага хийнэ.
    public OperationResult withdrawFromOwnAccount(Account account, double amount) {
        if (account == null) {
            return new OperationResult(false, "Данс олдсонгүй.");
        }

        if (!validator.isValidAmount(amount)) {
            return new OperationResult(false, "Зарлагын дүн буруу байна.");
        }

        boolean result = account.withdraw(amount, "Данснаасаа мөнгө авлаа", "");

        if (!result) {
            return new OperationResult(false, "Зарлага хийх боломжгүй байна.");
        }

        OperationResult saveResult = saveData();

        if (!saveResult.isSuccess()) {
            return new OperationResult(false, "Зарлага хийгдсэн боловч файлд хадгалахад алдаа гарлаа.");
        }

        return new OperationResult(true, "Зарлага амжилттай хийгдлээ.", account);
    }

    // Энэ method нь өөрийн данснаас өөр данс руу шилжүүлэг хийнэ.
    public OperationResult transferFromOwnAccount(Account fromAccount, String targetAccountId, double amount) {
        if (fromAccount == null) {
            return new OperationResult(false, "Нэвтэрсэн данс олдсонгүй.");
        }

        if (!validator.isValidAccountId(targetAccountId)) {
            return new OperationResult(false, "Хүлээн авах дансны ID хоосон байна.");
        }

        if (!validator.isValidAmount(amount)) {
            return new OperationResult(false, "Шилжүүлэх дүн буруу байна.");
        }

        Account targetAccount = bank.findAccountById(targetAccountId.trim());

        if (targetAccount == null) {
            return new OperationResult(false, "Хүлээн авах данс олдсонгүй.");
        }

        // Association: шилжүүлгийн үед хоёр Account object хоорондоо харилцаж байна.
        if (fromAccount.getAccountId().equalsIgnoreCase(targetAccount.getAccountId())) {
            return new OperationResult(false, "Өөрийн данс руу шилжүүлэг хийж болохгүй.");
        }

        boolean withdrawResult = fromAccount.withdraw(amount, "Шилжүүлэг хийлээ", targetAccount.getAccountId());

        if (!withdrawResult) {
            return new OperationResult(false, "Шилжүүлэг хийх үлдэгдэл хүрэлцэхгүй байна.");
        }

        targetAccount.deposit(amount, "Шилжүүлэг хүлээн авлаа", fromAccount.getAccountId());

        Transaction senderTransaction = fromAccount.getTransactions().get(fromAccount.getTransactions().size() - 1);
        Transaction receiverTransaction = targetAccount.getTransactions().get(targetAccount.getTransactions().size() - 1);

        if (senderTransaction != null && receiverTransaction != null) {
            // Энэ хэсэг хоосон биш байх нь гүйлгээ нэмэгдсэнийг ойлгомжтой болгоно.
        }

        OperationResult saveResult = saveData();

        if (!saveResult.isSuccess()) {
            return new OperationResult(false, "Шилжүүлэг хийгдсэн боловч файлд хадгалахад алдаа гарлаа.");
        }

        return new OperationResult(true, "Шилжүүлэг амжилттай хийгдлээ.");
    }

    // Энэ method нь бүх гүйлгээг текст хэлбэрээр буцаана.
    public String getAllTransactionsText() {
        ArrayList<Transaction> transactions = bank.getAllTransactions();

        if (transactions.size() == 0) {
            return "Одоогоор гүйлгээ байхгүй байна.";
        }

        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < transactions.size(); i++) {
            builder.append(transactions.get(i).toText());

            if (i < transactions.size() - 1) {
                builder.append("\n------------------------------\n");
            }
        }

        return builder.toString();
    }

    // Энэ method нь нэг хэрэглэгчийн гүйлгээний түүхийг буцаана.
    public String getOwnTransactionText(Account account) {
        if (account == null) {
            return "Данс олдсонгүй.";
        }

        ArrayList<Transaction> transactions = account.getTransactions();

        if (transactions.size() == 0) {
            return "Таны гүйлгээний түүх хоосон байна.";
        }

        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < transactions.size(); i++) {
            builder.append(transactions.get(i).toText());

            if (i < transactions.size() - 1) {
                builder.append("\n------------------------------\n");
            }
        }

        return builder.toString();
    }

    // Энэ method нь системийн хураангуй мэдээллийг буцаана.
    public String getSystemSummary() {
        return "Банкны нэр: " + bank.getBankName()
                + "\nНийт данс: " + bank.getAccounts().size()
                + "\nХадгаламжийн данс: " + bank.countAccountsByType("Хадгаламж")
                + "\nХарилцах данс: " + bank.countAccountsByType("Харилцах")
                + "\nНийт үлдэгдэл: " + bank.getTotalBalance() + " төгрөг"
                + "\nНийт гүйлгээ: " + bank.getAllTransactions().size();
    }

    // Энэ method нь дансуудыг сонгосон төрлөөр эрэмбэлнэ.
    public OperationResult sortAccounts(String sortType) {
        if ("Нэрээр".equals(sortType)) {
            bank.sortByName();
        } else if ("Үлдэгдлээр".equals(sortType)) {
            bank.sortByBalance();
        } else {
            bank.sortById();
        }

        return new OperationResult(true, "Дансны жагсаалт эрэмбэлэгдлээ.");
    }

    // Энэ method нь анхны жишээ өгөгдлийг үүсгэнэ.
    private void createSampleData() {
        Account first = new SavingsAccount("ACC1001", "Бат-Эрдэнэ", "bat123", "99112233", 0.0);
        Account second = new CurrentAccount("ACC1002", "Номин", "nomin123", "99113344", 0.0);
        Account third = new SavingsAccount("ACC1003", "Саруул", "saruul123", "99114455", 0.0);

        bank.addAccount(first);
        bank.addAccount(second);
        bank.addAccount(third);

        first.deposit(1500000.0, "Эхний хадгаламж", "");
        second.deposit(350000.0, "Цалингийн орлого", "");
        second.withdraw(50000.0, "ПОС төлбөр", "");
        third.deposit(820000.0, "Эхний орлого", "");
    }
}
