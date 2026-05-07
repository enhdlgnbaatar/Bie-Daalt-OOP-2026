import java.util.ArrayList;

// Энэ class нь банкны бүх дансыг удирдана.
public class Bank {
    private String bankName;
    // Aggregation: Bank нь олон Account object-ийг удирдана, гэхдээ Account тусдаа object хэвээр.
    private ArrayList<Account> accounts;

    public Bank(String bankName) {
        this.bankName = bankName;
        this.accounts = new ArrayList<Account>();
    }

    public String getBankName() {
        return bankName;
    }

    public ArrayList<Account> getAccounts() {
        return accounts;
    }

    public void setAccounts(ArrayList<Account> accounts) {
        this.accounts = accounts;
    }

    // Энэ method нь шинэ данс нэмнэ.
    public boolean addAccount(Account account) {
        if (findAccountById(account.getAccountId()) != null) {
            return false;
        }

        accounts.add(account);
        return true;
    }

    // Энэ method нь ID-аар данс хайна.
    public Account findAccountById(String accountId) {
        for (int i = 0; i < accounts.size(); i++) {
            Account account = accounts.get(i);

            if (account.getAccountId().equalsIgnoreCase(accountId)) {
                return account;
            }
        }

        return null;
    }

    // Энэ method нь нэр эсвэл ID-аар олон данс хайна.
    public ArrayList<Account> searchAccounts(String keyword) {
        ArrayList<Account> results = new ArrayList<Account>();
        String lowerKeyword = keyword.toLowerCase();

        for (int i = 0; i < accounts.size(); i++) {
            Account account = accounts.get(i);

            if (account.getAccountId().toLowerCase().contains(lowerKeyword)
                    || account.getName().toLowerCase().contains(lowerKeyword)) {
                results.add(account);
            }
        }

        return results;
    }

    // Энэ method нь дансыг устгана.
    public boolean removeAccount(String accountId) {
        Account account = findAccountById(accountId);

        if (account == null) {
            return false;
        }

        accounts.remove(account);
        return true;
    }

    // Энэ method нь бүх гүйлгээг нэг жагсаалтад нэгтгэнэ.
    public ArrayList<Transaction> getAllTransactions() {
        ArrayList<Transaction> transactions = new ArrayList<Transaction>();

        for (int i = 0; i < accounts.size(); i++) {
            Account account = accounts.get(i);
            ArrayList<Transaction> accountTransactions = account.getTransactions();

            for (int j = 0; j < accountTransactions.size(); j++) {
                transactions.add(accountTransactions.get(j));
            }
        }

        return transactions;
    }

    // Энэ method нь дансуудыг нэрээр эрэмбэлнэ.
    public void sortByName() {
        for (int i = 0; i < accounts.size() - 1; i++) {
            for (int j = 0; j < accounts.size() - 1 - i; j++) {
                String firstName = accounts.get(j).getName().toLowerCase();
                String secondName = accounts.get(j + 1).getName().toLowerCase();

                if (firstName.compareTo(secondName) > 0) {
                    Account temp = accounts.get(j);
                    accounts.set(j, accounts.get(j + 1));
                    accounts.set(j + 1, temp);
                }
            }
        }
    }

    // Энэ method нь дансуудыг үлдэгдлээр эрэмбэлнэ.
    public void sortByBalance() {
        for (int i = 0; i < accounts.size() - 1; i++) {
            for (int j = 0; j < accounts.size() - 1 - i; j++) {
                if (accounts.get(j).getBalance() > accounts.get(j + 1).getBalance()) {
                    Account temp = accounts.get(j);
                    accounts.set(j, accounts.get(j + 1));
                    accounts.set(j + 1, temp);
                }
            }
        }
    }

    // Энэ method нь дансуудыг ID-аар эрэмбэлнэ.
    public void sortById() {
        for (int i = 0; i < accounts.size() - 1; i++) {
            for (int j = 0; j < accounts.size() - 1 - i; j++) {
                String firstId = accounts.get(j).getAccountId().toLowerCase();
                String secondId = accounts.get(j + 1).getAccountId().toLowerCase();

                if (firstId.compareTo(secondId) > 0) {
                    Account temp = accounts.get(j);
                    accounts.set(j, accounts.get(j + 1));
                    accounts.set(j + 1, temp);
                }
            }
        }
    }

    // Энэ method нь нийт үлдэгдлийг тооцно.
    public double getTotalBalance() {
        double total = 0;

        for (int i = 0; i < accounts.size(); i++) {
            total = total + accounts.get(i).getBalance();
        }

        return total;
    }

    // Энэ method нь тухайн төрлийн дансны тоог тоолно.
    public int countAccountsByType(String accountType) {
        int count = 0;

        for (int i = 0; i < accounts.size(); i++) {
            if (accounts.get(i).getAccountType().equals(accountType)) {
                count++;
            }
        }

        return count;
    }
}
