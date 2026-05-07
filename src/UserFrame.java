import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.GridLayout;

// Энэ class нь энгийн хэрэглэгчийн өөрийн дансны дэлгэцийг хариуцна.
public class UserFrame extends JFrame {
    private BankService bankService;
    private Account currentAccount;
    private LoginFrame loginFrame;
    private JTextArea infoArea;

    public UserFrame(BankService bankService, Account currentAccount, LoginFrame loginFrame) {
        this.bankService = bankService;
        this.currentAccount = currentAccount;
        this.loginFrame = loginFrame;

        setTitle("Хэрэглэгчийн хэсэг");
        setSize(760, 520);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        initializeUi();
        refreshInfo();
        setVisible(true);
    }

    private void initializeUi() {
        // UI theme: хэрэглэгчийн frame-ийг admin/login хэсэгтэй нэг дизайнтай болгоно.
        BankTheme.applyFrame(this);
        setLayout(new BorderLayout(10, 10));

        JLabel titleLabel = new JLabel("Миний банкны данс", JLabel.CENTER);
        BankTheme.styleTitle(titleLabel);
        add(titleLabel, BorderLayout.NORTH);

        infoArea = new JTextArea();
        infoArea.setEditable(false);
        infoArea.setLineWrap(true);
        infoArea.setWrapStyleWord(true);
        infoArea.setBorder(BorderFactory.createTitledBorder("Миний мэдээлэл"));
        // Дансны мэдээллийг уншихад илүү цэвэр харагдуулах text area style.
        BankTheme.styleTextArea(infoArea);
        JScrollPane infoScrollPane = new JScrollPane(infoArea);
        BankTheme.styleScrollPane(infoScrollPane);
        infoScrollPane.setBorder(BorderFactory.createEmptyBorder(0, 16, 0, 16));
        add(infoScrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new GridLayout(3, 3, 6, 6));
        BankTheme.stylePanel(buttonPanel);

        JButton viewInfoButton = new JButton("Мэдээлэл");
        JButton balanceButton = new JButton("Үлдэгдэл");
        JButton depositButton = new JButton("Орлого");
        JButton withdrawButton = new JButton("Зарлага");
        JButton transferButton = new JButton("Шилжүүлэг");
        JButton historyButton = new JButton("Түүх");
        JButton passwordButton = new JButton("Нууц үг солих");
        JButton deleteButton = new JButton("Өөрийгөө устгах");
        JButton logoutButton = new JButton("Гарах");
        // Button color hierarchy: орлого/шилжүүлэг/устгах үйлдлүүдийг ялгаж харуулна.
        BankTheme.stylePrimaryButton(depositButton);
        BankTheme.styleAccentButton(transferButton);
        BankTheme.styleDangerButton(deleteButton);
        BankTheme.styleButtons(viewInfoButton, balanceButton, withdrawButton, historyButton,
                passwordButton, logoutButton);

        viewInfoButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                refreshInfo();
            }
        });

        balanceButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                JOptionPane.showMessageDialog(UserFrame.this,
                        "Таны үлдэгдэл: " + currentAccount.getBalance() + " төгрөг");
            }
        });

        depositButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                showDepositDialog();
            }
        });

        withdrawButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                showWithdrawDialog();
            }
        });

        transferButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                showTransferDialog();
            }
        });

        historyButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                showHistoryDialog();
            }
        });

        passwordButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                showChangePasswordDialog();
            }
        });

        deleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                showDeleteAccountDialog();
            }
        });

        logoutButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                logout();
            }
        });

        buttonPanel.add(viewInfoButton);
        buttonPanel.add(balanceButton);
        buttonPanel.add(depositButton);
        buttonPanel.add(withdrawButton);
        buttonPanel.add(transferButton);
        buttonPanel.add(historyButton);
        buttonPanel.add(passwordButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(logoutButton);

        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        BankTheme.stylePanel(southPanel);
        southPanel.add(buttonPanel, BorderLayout.CENTER);
        add(southPanel, BorderLayout.SOUTH);
    }

    private void refreshInfo() {
        currentAccount = bankService.refreshAccount(currentAccount.getAccountId());

        if (currentAccount == null) {
            infoArea.setText("Таны данс системээс устсан байна.");
            return;
        }

        infoArea.setText(currentAccount.displayInfo());
    }

    private void showDepositDialog() {
        String amountText = JOptionPane.showInputDialog(this, "Орлогын дүн оруулна уу:");

        if (amountText == null) {
            return;
        }

        try {
            double amount = Double.parseDouble(amountText.trim());
            OperationResult result = bankService.depositToOwnAccount(currentAccount, amount);
            JOptionPane.showMessageDialog(this, result.getMessage());

            if (result.isSuccess()) {
                refreshInfo();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Дүн буруу байна.");
        }
    }

    private void showWithdrawDialog() {
        String amountText = JOptionPane.showInputDialog(this, "Зарлагын дүн оруулна уу:");

        if (amountText == null) {
            return;
        }

        try {
            double amount = Double.parseDouble(amountText.trim());
            OperationResult result = bankService.withdrawFromOwnAccount(currentAccount, amount);
            JOptionPane.showMessageDialog(this, result.getMessage());

            if (result.isSuccess()) {
                refreshInfo();
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Дүн буруу байна.");
        }
    }

    private void showTransferDialog() {
        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
        JTextField targetField = new JTextField();
        JTextField amountField = new JTextField();

        panel.add(new JLabel("Хүлээн авах ID"));
        panel.add(targetField);
        panel.add(new JLabel("Шилжүүлэх дүн"));
        panel.add(amountField);

        int option = JOptionPane.showConfirmDialog(this, panel, "Шилжүүлэг",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (option == JOptionPane.OK_OPTION) {
            try {
                double amount = Double.parseDouble(amountField.getText().trim());
                OperationResult result = bankService.transferFromOwnAccount(
                        currentAccount, targetField.getText().trim(), amount);
                JOptionPane.showMessageDialog(this, result.getMessage());

                if (result.isSuccess()) {
                    refreshInfo();
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Дүн буруу байна.");
            }
        }
    }

    private void showHistoryDialog() {
        JTextArea historyArea = new JTextArea(bankService.getOwnTransactionText(currentAccount), 18, 40);
        historyArea.setEditable(false);
        historyArea.setLineWrap(true);
        historyArea.setWrapStyleWord(true);
        JOptionPane.showMessageDialog(this, new JScrollPane(historyArea),
                "Миний гүйлгээний түүх", JOptionPane.INFORMATION_MESSAGE);
    }

    private void showChangePasswordDialog() {
        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
        JTextField oldPasswordField = new JTextField();
        JTextField newPasswordField = new JTextField();

        panel.add(new JLabel("Хуучин нууц үг"));
        panel.add(oldPasswordField);
        panel.add(new JLabel("Шинэ нууц үг"));
        panel.add(newPasswordField);

        int option = JOptionPane.showConfirmDialog(this, panel, "Нууц үг солих",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (option == JOptionPane.OK_OPTION) {
            OperationResult result = bankService.changePassword(
                    currentAccount, oldPasswordField.getText(), newPasswordField.getText());
            JOptionPane.showMessageDialog(this, result.getMessage());

            if (result.isSuccess()) {
                refreshInfo();
            }
        }
    }

    private void showDeleteAccountDialog() {
        String password = JOptionPane.showInputDialog(this, "Дансаа устгах бол нууц үгээ оруулна уу:");

        if (password == null) {
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Та өөрийн дансыг бүрмөсөн устгах уу?", "Баталгаажуулалт", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            OperationResult result = bankService.deleteOwnAccount(currentAccount, password);
            JOptionPane.showMessageDialog(this, result.getMessage());

            if (result.isSuccess()) {
                dispose();
                loginFrame.setVisible(true);
            }
        }
    }

    private void logout() {
        dispose();
        loginFrame.setVisible(true);
    }
}
