// Энэ class нь хэрэглэгчийн оруулсан утгыг энгийн түвшинд шалгана.
public class Validator {

    public boolean isValidText(String text) {
        if (text == null) {
            return false;
        }

        return !text.trim().equals("");
    }

    public boolean isValidAccountId(String accountId) {
        return isValidText(accountId);
    }

    public boolean isValidPassword(String password) {
        return isValidText(password);
    }

    public boolean isValidPhone(String phone) {
        return isValidText(phone);
    }

    public boolean isValidAmount(double amount) {
        return amount > 0;
    }

    public boolean isValidStartBalance(double amount) {
        return amount >= 0;
    }
}
