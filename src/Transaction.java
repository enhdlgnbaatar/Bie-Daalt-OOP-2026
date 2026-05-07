import java.time.LocalDate;

// Энэ class нь нэг гүйлгээний мэдээллийг хадгална.
public class Transaction {
    private static int nextId = 1;

    private String transactionId;
    private String accountId;
    private String type;
    private double amount;
    private LocalDate date;
    private String description;
    private String targetAccountId;

    // Энэ constructor нь шинэ гүйлгээ үүсгэнэ.
    public Transaction(String accountId, String type, double amount, String description, String targetAccountId) {
        this.transactionId = "TXN" + nextId;
        nextId++;
        this.accountId = accountId;
        this.type = type;
        this.amount = amount;
        this.date = LocalDate.now();
        this.description = description;
        this.targetAccountId = targetAccountId;
    }

    // Энэ constructor нь файлаас уншсан гүйлгээг сэргээнэ.
    public Transaction(String transactionId, String accountId, String type, double amount, LocalDate date,
                       String description, String targetAccountId) {
        this.transactionId = transactionId;
        this.accountId = accountId;
        this.type = type;
        this.amount = amount;
        this.date = date;
        this.description = description;
        this.targetAccountId = targetAccountId;
        updateNextId(transactionId);
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getAccountId() {
        return accountId;
    }

    public String getType() {
        return type;
    }

    public double getAmount() {
        return amount;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getDescription() {
        return description;
    }

    public String getTargetAccountId() {
        return targetAccountId;
    }

    // Энэ method нь гүйлгээний мэдээллийг уншихад хялбар мөр болгон буцаана.
    public String toText() {
        String text = "Гүйлгээний ID: " + transactionId
                + "\nДансны ID: " + accountId
                + "\nТөрөл: " + type
                + "\nДүн: " + amount + " төгрөг"
                + "\nОгноо: " + date
                + "\nТайлбар: " + description;

        if (targetAccountId != null && !targetAccountId.equals("")) {
            text = text + "\nХолбогдсон данс: " + targetAccountId;
        }

        return text;
    }

    // Энэ method нь дараагийн автоматаар өсөх ID-г шинэчилнэ.
    public static void updateNextId(String transactionId) {
        if (transactionId == null) {
            return;
        }

        if (transactionId.startsWith("TXN")) {
            try {
                int number = Integer.parseInt(transactionId.substring(3));

                if (number >= nextId) {
                    nextId = number + 1;
                }
            } catch (Exception e) {
                // Энэ хэсэгт буруу ID байсан ч програм үргэлжилнэ.
            }
        }
    }
}
