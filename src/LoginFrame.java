import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.GridLayout;

// Энэ class нь админ болон хэрэглэгчийн нэвтрэлтийг хариуцна.
public class LoginFrame extends JFrame {
    private BankService bankService;
    private JComboBox<String> roleBox;
    private JLabel idLabel;
    private JTextField idField;
    private JPasswordField passwordField;

    public LoginFrame() {
        this(new BankService());
    }

    public LoginFrame(BankService bankService) {
        this.bankService = bankService;

        setTitle("Банкны систем - Нэвтрэх");
        setSize(420, 280);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        initializeUi();

        OperationResult loadResult = this.bankService.loadData();

        if (!loadResult.isSuccess()) {
            JOptionPane.showMessageDialog(this, loadResult.getMessage(), "Алдаа", JOptionPane.ERROR_MESSAGE);
        }

        setVisible(true);
    }

    private void initializeUi() {
        // UI theme: login frame-ийн өнгө, font, background-ийг нэг загварт оруулна.
        BankTheme.applyFrame(this);
        setLayout(new BorderLayout(10, 10));

        JLabel titleLabel = new JLabel("Банкны системд нэвтрэх", JLabel.CENTER);
        BankTheme.styleTitle(titleLabel);
        add(titleLabel, BorderLayout.NORTH);

        JPanel centerPanel = new JPanel(new GridLayout(0, 2, 8, 8));
        centerPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        // Login form-ийг default Swing саарал биш, цэвэр card-like surface болгоно.
        BankTheme.styleSurface(centerPanel);

        roleBox = new JComboBox<String>(new String[]{"Админ", "Хэрэглэгч"});
        idLabel = new JLabel("Нэвтрэх нэр");
        idField = new JTextField();
        passwordField = new JPasswordField();
        JLabel roleLabel = new JLabel("Нэвтрэх төрөл");
        JLabel passwordLabel = new JLabel("Нууц үг");

        BankTheme.styleComboBox(roleBox);
        BankTheme.styleTextField(idField);
        BankTheme.styleTextField(passwordField);
        BankTheme.styleLabels(roleLabel, idLabel, passwordLabel);

        roleBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                updateLabels();
            }
        });

        centerPanel.add(roleLabel);
        centerPanel.add(roleBox);
        centerPanel.add(idLabel);
        centerPanel.add(idField);
        centerPanel.add(passwordLabel);
        centerPanel.add(passwordField);

        add(centerPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();
        BankTheme.stylePanel(bottomPanel);
        JButton loginButton = new JButton("Нэвтрэх");
        JButton exitButton = new JButton("Гарах");
        // Гол үйлдэл тул primary өнгө хэрэглэнэ.
        BankTheme.stylePrimaryButton(loginButton);
        BankTheme.styleButton(exitButton);

        loginButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                handleLogin();
            }
        });

        exitButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                dispose();
            }
        });

        bottomPanel.add(loginButton);
        bottomPanel.add(exitButton);
        add(bottomPanel, BorderLayout.SOUTH);

        updateLabels();
    }

    private void updateLabels() {
        String selectedRole = String.valueOf(roleBox.getSelectedItem());

        if ("Админ".equals(selectedRole)) {
            idLabel.setText("Хэрэглэгчийн нэр");    
        } else {
            idLabel.setText("Дансны ID");
        }
    }

    private void handleLogin() {
        String role = String.valueOf(roleBox.getSelectedItem());
        String id = idField.getText().trim();
        String password = new String(passwordField.getPassword());

        if ("Админ".equals(role)) {
            OperationResult result = bankService.adminLogin(id, password);

            if (result.isSuccess()) {
                new AdminFrame(bankService, this);
                setVisible(false);
            } else {
                JOptionPane.showMessageDialog(this, result.getMessage(), "Алдаа", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            OperationResult result = bankService.userLogin(id, password);

            if (result.isSuccess()) {
                new UserFrame(bankService, result.getAccount(), this);
                setVisible(false);
            } else {
                JOptionPane.showMessageDialog(this, result.getMessage(), "Алдаа", JOptionPane.ERROR_MESSAGE);
            }
        }

        passwordField.setText("");
    }
}
