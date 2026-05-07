import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.table.JTableHeader;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Insets;

// UI helper class: frame-үүд inheritance ашиглахгүйгээр энэ class-ийн static method-уудыг ашиглана.
public final class BankTheme {
    // Аппын үндсэн өнгөнүүдийг нэг газраас удирдаж байна.
    public static final Color BACKGROUND = new Color(245, 248, 250);
    public static final Color SURFACE = Color.WHITE;
    public static final Color PRIMARY = new Color(0, 121, 107);
    public static final Color PRIMARY_DARK = new Color(0, 77, 64);
    public static final Color ACCENT = new Color(37, 99, 235);
    public static final Color DANGER = new Color(211, 47, 47);
    public static final Color TEXT = new Color(30, 41, 59);
    public static final Color MUTED = new Color(100, 116, 139);
    public static final Color BORDER = new Color(218, 226, 235);

    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 22);
    private static final Font BODY_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font BUTTON_FONT = new Font("Segoe UI", Font.BOLD, 13);

    // Utility class тул object үүсгэх шаардлагагүй.
    private BankTheme() {
    }

    // Swing-ийн default component-үүдэд font/background тохируулна.
    public static void install() {
        UIManager.put("OptionPane.background", BACKGROUND);
        UIManager.put("Panel.background", BACKGROUND);
        UIManager.put("Label.font", BODY_FONT);
        UIManager.put("Button.font", BUTTON_FONT);
        UIManager.put("TextField.font", BODY_FONT);
        UIManager.put("PasswordField.font", BODY_FONT);
        UIManager.put("ComboBox.font", BODY_FONT);
        UIManager.put("TextArea.font", BODY_FONT);
        UIManager.put("Table.font", BODY_FONT);
    }

    // JFrame бүрт theme суулгаж, үндсэн background тохируулна.
    public static void applyFrame(JFrame frame) {
        install();
        frame.getContentPane().setBackground(BACKGROUND);
    }

    // Frame-ийн дээд гарчгийг банкны dashboard маягтай харагдуулна.
    public static void styleTitle(JLabel label) {
        label.setFont(TITLE_FONT);
        label.setForeground(PRIMARY_DARK);
        label.setBorder(BorderFactory.createEmptyBorder(18, 16, 12, 16));
    }

    // Энгийн panel background.
    public static void stylePanel(JPanel panel) {
        panel.setBackground(BACKGROUND);
    }

    // Form хэсгүүдэд цагаан гадаргуу, padding, border өгнө.
    public static void styleSurface(JPanel panel) {
        panel.setBackground(SURFACE);
        panel.setBorder(paddedBorder());
    }

    // Бүх энгийн button-ийн суурь style.
    public static void styleButton(JButton button) {
        button.setFont(BUTTON_FONT);
        button.setForeground(TEXT);
        button.setBackground(SURFACE);
        button.setFocusPainted(false);
        button.setBorder(compoundBorder());
    }

    // Гол үйлдэл хийх button: нэвтрэх, нэмэх, орлого гэх мэт.
    public static void stylePrimaryButton(JButton button) {
        styleButton(button);
        button.setForeground(Color.WHITE);
        button.setBackground(PRIMARY);
    }

    // Туслах чухал үйлдэл хийх button: хайх, шилжүүлэх гэх мэт.
    public static void styleAccentButton(JButton button) {
        styleButton(button);
        button.setForeground(Color.WHITE);
        button.setBackground(ACCENT);
    }

    // Эрсдэлтэй үйлдэл хийх button: устгах гэх мэт.
    public static void styleDangerButton(JButton button) {
        styleButton(button);
        button.setForeground(Color.WHITE);
        button.setBackground(DANGER);
    }

    // Олон button-д нэг дор энгийн style өгөх overloaded биш, varargs helper method.
    public static void styleButtons(JButton... buttons) {
        for (int i = 0; i < buttons.length; i++) {
            styleButton(buttons[i]);
        }
    }

    // TextField/JPasswordField зэрэг input-уудын харагдах байдлыг нэг мөр болгоно.
    public static void styleTextField(JTextField field) {
        field.setFont(BODY_FONT);
        field.setForeground(TEXT);
        field.setBackground(SURFACE);
        field.setBorder(compoundBorder());
    }

    // ComboBox-ийн font, өнгө, border-ийг theme-тэй тааруулна.
    public static void styleComboBox(JComboBox<?> comboBox) {
        comboBox.setFont(BODY_FONT);
        comboBox.setForeground(TEXT);
        comboBox.setBackground(SURFACE);
        comboBox.setBorder(BorderFactory.createLineBorder(BORDER));
    }

    // TextArea-г уншихад цэвэр, зайтай харагдуулна.
    public static void styleTextArea(JTextArea area) {
        area.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        area.setForeground(TEXT);
        area.setBackground(SURFACE);
        area.setCaretColor(PRIMARY);
        area.setMargin(new Insets(12, 12, 12, 12));
    }

    // Admin table-ийн мөр, сонголт, header-ийн өнгө.
    public static void styleTable(JTable table) {
        table.setFont(BODY_FONT);
        table.setForeground(TEXT);
        table.setBackground(SURFACE);
        table.setGridColor(BORDER);
        table.setRowHeight(30);
        table.setSelectionBackground(new Color(204, 251, 241));
        table.setSelectionForeground(PRIMARY_DARK);

        JTableHeader header = table.getTableHeader();
        header.setFont(BUTTON_FONT);
        header.setForeground(Color.WHITE);
        header.setBackground(PRIMARY_DARK);
    }

    // ScrollPane border/background-ийг нэг style-д оруулна.
    public static void styleScrollPane(JScrollPane scrollPane) {
        scrollPane.setBorder(BorderFactory.createLineBorder(BORDER));
        scrollPane.getViewport().setBackground(SURFACE);
    }

    // Label-үүдийг muted өнгөтэй болгож form илүү цэвэр харагдуулна.
    public static void styleLabels(Component... components) {
        for (int i = 0; i < components.length; i++) {
            components[i].setFont(BODY_FONT);
            components[i].setForeground(MUTED);
        }
    }

    // Panel-ийн гадна border ба дотор padding.
    private static Border paddedBorder() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(14, 14, 14, 14)
        );
    }

    // Button/input-ийн border ба дотор padding.
    private static Border compoundBorder() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        );
    }
}
