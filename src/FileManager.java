import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;

// Энэ class нь CSV файл унших, бичих ажлыг хариуцна.
public class FileManager {
    private File accountsFile;
    private File transactionsFile;

    public FileManager(String accountsPath, String transactionsPath) {
        this.accountsFile = new File(accountsPath);
        this.transactionsFile = new File(transactionsPath);
    }

    // Энэ method нь шаардлагатай CSV файлуудыг үүсгэнэ.
    public boolean ensureFilesExist() {
        boolean createdAnyFile = false;

        try {
            if (!accountsFile.exists()) {
                writeAccountsHeader();
                createdAnyFile = true;
            }

            if (!transactionsFile.exists()) {
                writeTransactionsHeader();
                createdAnyFile = true;
            }
        } catch (Exception e) {
            return false;
        }

        return createdAnyFile;
    }

    // Энэ method нь бүх өгөгдлийг файлд хадгална.
    public boolean saveAll(Bank bank) {
        return saveAccounts(bank.getAccounts()) && saveTransactions(bank.getAllTransactions());
    }

    // Энэ method нь accounts.csv файлд дансны жагсаалтыг хадгална.
    public boolean saveAccounts(ArrayList<Account> accounts) {
        try {
            BufferedWriter writer = createWriter(accountsFile);
            writer.write("accountId,name,password,type,balance,phone");
            writer.newLine();

            for (int i = 0; i < accounts.size(); i++) {
                Account account = accounts.get(i);
                writer.write(escape(account.getAccountId()) + ","
                        + escape(account.getName()) + ","
                        + escape(account.getPassword()) + ","
                        + escape(account.getAccountType()) + ","
                        + account.getBalance() + ","
                        + escape(account.getPhone()));
                writer.newLine();
            }

            writer.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // Энэ method нь transactions.csv файлд гүйлгээг хадгална.
    public boolean saveTransactions(ArrayList<Transaction> transactions) {
        try {
            BufferedWriter writer = createWriter(transactionsFile);
            writer.write("transactionId,accountId,type,amount,date,description,targetAccountId");
            writer.newLine();

            for (int i = 0; i < transactions.size(); i++) {
                Transaction transaction = transactions.get(i);
                writer.write(escape(transaction.getTransactionId()) + ","
                        + escape(transaction.getAccountId()) + ","
                        + escape(transaction.getType()) + ","
                        + transaction.getAmount() + ","
                        + transaction.getDate() + ","
                        + escape(transaction.getDescription()) + ","
                        + escape(transaction.getTargetAccountId()));
                writer.newLine();
            }

            writer.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // Энэ method нь accounts.csv файлаас дансуудыг уншина.
    public ArrayList<Account> loadAccounts() {
        ArrayList<Account> accounts = new ArrayList<Account>();

        try {
            ensureFilesExist();
            BufferedReader reader = createReader(accountsFile);
            String header = reader.readLine();
            String line;

            while ((line = reader.readLine()) != null) {
                if (line.trim().equals("")) {
                    continue;
                }

                String[] parts = parseCsvLine(line);

                if (header != null && header.contains("password")) {
                    Account account = createNewFormatAccount(parts);

                    if (account != null) {
                        accounts.add(account);
                    }
                } else {
                    Account account = createOldFormatAccount(parts);

                    if (account != null) {
                        accounts.add(account);
                    }
                }
            }

            reader.close();
            return accounts;
        } catch (Exception e) {
            return null;
        }
    }

    // Энэ method нь transactions.csv файлаас гүйлгээг уншина.
    public boolean loadTransactions(ArrayList<Account> accounts) {
        try {
            ensureFilesExist();
            BufferedReader reader = createReader(transactionsFile);
            String header = reader.readLine();
            String line;

            while ((line = reader.readLine()) != null) {
                if (line.trim().equals("")) {
                    continue;
                }

                String[] parts = parseCsvLine(line);
                Transaction transaction;

                if (header != null && header.contains("targetAccountId")) {
                    transaction = createNewFormatTransaction(parts);
                } else {
                    transaction = createOldFormatTransaction(parts);
                }

                if (transaction != null) {
                    Account account = findAccount(accounts, transaction.getAccountId());

                    if (account != null) {
                        account.addLoadedTransaction(transaction);
                    }
                }
            }

            reader.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private Account createNewFormatAccount(String[] parts) {
        if (parts.length < 6) {
            return null;
        }

        String accountId = parts[0];
        String name = parts[1];
        String password = parts[2];
        String type = parts[3];
        double balance = Double.parseDouble(parts[4]);
        String phone = parts[5];

        return createAccountByType(accountId, name, password, phone, balance, type);
    }

    // Энэ method нь хуучин CSV бүтцээс шинэ Account object үүсгэнэ.
    private Account createOldFormatAccount(String[] parts) {
        if (parts.length < 5) {
            return null;
        }

        String accountId = parts[0];
        String name = parts[1];
        double balance = Double.parseDouble(parts[2]);
        String type = parts[4];
        String password = "1234";
        String phone = "99000000";

        return createAccountByType(accountId, name, password, phone, balance, type);
    }

    private Account createAccountByType(String accountId, String name, String password, String phone,
                                        double balance, String type) {
        if ("Хадгаламж".equals(type)) {
            return new SavingsAccount(accountId, name, password, phone, balance);
        }

        return new CurrentAccount(accountId, name, password, phone, balance);
    }

    private Transaction createNewFormatTransaction(String[] parts) {
        if (parts.length < 7) {
            return null;
        }

        return new Transaction(
                parts[0],
                parts[1],
                parts[2],
                Double.parseDouble(parts[3]),
                LocalDate.parse(parts[4]),
                parts[5],
                parts[6]
        );
    }

    private Transaction createOldFormatTransaction(String[] parts) {
        if (parts.length < 6) {
            return null;
        }

        return new Transaction(
                parts[0],
                parts[1],
                parts[2],
                Double.parseDouble(parts[3]),
                LocalDate.parse(parts[4]),
                parts[5],
                ""
        );
    }

    private Account findAccount(ArrayList<Account> accounts, String accountId) {
        for (int i = 0; i < accounts.size(); i++) {
            Account account = accounts.get(i);

            if (account.getAccountId().equalsIgnoreCase(accountId)) {
                return account;
            }
        }

        return null;
    }

    private BufferedReader createReader(File file) throws Exception {
        return new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8)
        );
    }

    private BufferedWriter createWriter(File file) throws Exception {
        return new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8)
        );
    }

    private void writeAccountsHeader() throws Exception {
        BufferedWriter writer = createWriter(accountsFile);
        writer.write("accountId,name,password,type,balance,phone");
        writer.newLine();
        writer.close();
    }

    private void writeTransactionsHeader() throws Exception {
        BufferedWriter writer = createWriter(transactionsFile);
        writer.write("transactionId,accountId,type,amount,date,description,targetAccountId");
        writer.newLine();
        writer.close();
    }

    // Энэ method нь CSV мөр доторх тусгай тэмдгийг хамгаална.
    private String escape(String value) {
        if (value == null) {
            return "";
        }

        boolean needsQuotes = value.contains(",") || value.contains("\"") || value.contains("\n");
        String result = value.replace("\"", "\"\"");

        if (needsQuotes) {
            return "\"" + result + "\"";
        }

        return result;
    }

    // Энэ method нь CSV мөрийг талбарууд болгон салгана.
    private String[] parseCsvLine(String line) {
        ArrayList<String> values = new ArrayList<String>();
        StringBuilder current = new StringBuilder();
        boolean insideQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char ch = line.charAt(i);

            if (ch == '"') {
                if (insideQuotes && i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    current.append('"');
                    i++;
                } else {
                    insideQuotes = !insideQuotes;
                }
            } else if (ch == ',' && !insideQuotes) {
                values.add(current.toString());
                current.setLength(0);
            } else {
                current.append(ch);
            }
        }

        values.add(current.toString());

        String[] result = new String[values.size()];

        for (int i = 0; i < values.size(); i++) {
            result[i] = values.get(i);
        }

        return result;
    }
}
