// Interface буюу implements ойлголт: дансны бүх төрлүүд заавал хэрэгжүүлэх method-ууд.
public interface AccountOperations {
    // Орлого хийх үйлдлийн гэрээ.
    boolean deposit(double amount, String description, String targetAccountId);

    // Зарлага хийх үйлдлийн гэрээ.
    boolean withdraw(double amount, String description, String targetAccountId);

    // Дансны төрлийг subclass бүр өөрөөр буцаана.
    String getAccountType();

    // Дансны дэлгэрэнгүй мэдээллийг subclass бүр override хийж өргөтгөж болно.
    String displayInfo();
}
