import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.ArrayList;

// Энэ class нь админы удирдлагын дэлгэцийг хариуцна.
public class AdminFrame extends JFrame {
    private BankService bankService;
    private LoginFrame loginFrame;
    private DefaultTableModel tableModel;
    private JTable accountTable;
    private JTextArea infoArea;

    public AdminFrame(BankService bankService, LoginFrame loginFrame) {
        this.bankService = bankService;
        this.loginFrame = loginFrame;

        setTitle("Админы хэсэг");
        setSize(980, 620);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        initializeUi();
        refreshTable();
        setVisible(true);
    }

    private void initializeUi() {
        // UI theme: admin dashboard-ийн бүх component нэг өнгө, font-той болно.
        BankTheme.applyFrame(this);
        setLayout(new BorderLayout(10, 10));

        JLabel titleLabel = new JLabel("Админы системийн удирдлага", JLabel.CENTER);
        BankTheme.styleTitle(titleLabel);
        add(titleLabel, BorderLayout.NORTH);

        tableModel = new DefaultTableModel(new Object[]{"ID", "Нэр", "Төрөл", "Үлдэгдэл", "Утас"}, 0) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        accountTable = new JTable(tableModel);
        // Table style: банкны дансны жагсаалтыг илүү уншигдахуйц болгоно.
        BankTheme.styleTable(accountTable);
        accountTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        accountTable.getSelectionModel().addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    showSelectedAccountInfo();
                }
            }
        });

        infoArea = new JTextArea();
        infoArea.setEditable(false);
        infoArea.setLineWrap(true);
        infoArea.setWrapStyleWord(true);
        infoArea.setBorder(BorderFactory.createTitledBorder("Дэлгэрэнгүй мэдээлэл"));
        BankTheme.styleTextArea(infoArea);

        JScrollPane tableScrollPane = new JScrollPane(accountTable);
        JScrollPane infoScrollPane = new JScrollPane(infoArea);
        BankTheme.styleScrollPane(tableScrollPane);
        BankTheme.styleScrollPane(infoScrollPane);
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                tableScrollPane, infoScrollPane);
        splitPane.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 12));
        splitPane.setDividerLocation(600);
        add(splitPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new GridLayout(2, 5, 6, 6));
        BankTheme.stylePanel(buttonPanel);

        JButton addButton = new JButton("Данс нэмэх");
        JButton deleteButton = new JButton("Данс устгах");
        JButton searchButton = new JButton("Хайх");
        JButton sortButton = new JButton("Эрэмбэлэх");
        JButton transactionsButton = new JButton("Бүх гүйлгээ");
        JButton saveButton = new JButton("Хадгалах");
        JButton loadButton = new JButton("Унших");
        JButton summaryButton = new JButton("Товч тайлан");
        JButton refreshButton = new JButton("Сэргээх");
        JButton logoutButton = new JButton("Гарах");
        // Button color hierarchy: гол, туслах, эрсдэлтэй үйлдлүүдийг өнгөөр ялгана.
        BankTheme.stylePrimaryButton(addButton);
        BankTheme.styleDangerButton(deleteButton);
        BankTheme.styleAccentButton(searchButton);
        BankTheme.styleButtons(sortButton, transactionsButton, saveButton, loadButton,
                summaryButton, refreshButton, logoutButton);

        addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                showAddDialog();
            }
        });

        deleteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                deleteSelectedAccount();
            }
        });

        searchButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                showSearchDialog();
            }
        });

        sortButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                showSortDialog();
            }
        });

        transactionsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                showAllTransactions();
            }
        });

        saveButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                JOptionPane.showMessageDialog(AdminFrame.this, bankService.saveData().getMessage());
            }
        });

        loadButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                JOptionPane.showMessageDialog(AdminFrame.this, bankService.loadData().getMessage());
                refreshTable();
            }
        });

        summaryButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                JOptionPane.showMessageDialog(AdminFrame.this, bankService.getSystemSummary());
            }
        });

        refreshButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                refreshTable();
            }
        });

        logoutButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                logout();
            }
        });

        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(searchButton);
        buttonPanel.add(sortButton);
        buttonPanel.add(transactionsButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(loadButton);
        buttonPanel.add(summaryButton);
        buttonPanel.add(refreshButton);
        buttonPanel.add(logoutButton);

        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
        BankTheme.stylePanel(southPanel);
        southPanel.add(buttonPanel, BorderLayout.CENTER);
        add(southPanel, BorderLayout.SOUTH);
    }

    private void refreshTable() {
        tableModel.setRowCount(0);
        ArrayList<Account> accounts = bankService.getBank().getAccounts();

        for (int i = 0; i < accounts.size(); i++) {
            Account account = accounts.get(i);
            tableModel.addRow(new Object[]{
                    account.getAccountId(),
                    account.getName(),
                    account.getAccountType(),
                    account.getBalance(),
                    account.getPhone()
            });
        }

        if (accounts.size() == 0) {
            infoArea.setText("Дансны мэдээлэл алга.");
        } else {
            showSelectedAccountInfo();
        }
    }

    private void showSelectedAccountInfo() {
        int selectedRow = accountTable.getSelectedRow();

        if (selectedRow < 0) {
            infoArea.setText("Дэлгэрэнгүйг харах дансаа сонгоно уу.");
            return;
        }

        String accountId = String.valueOf(tableModel.getValueAt(selectedRow, 0));
        OperationResult result = bankService.getAccountInfo(accountId);
        infoArea.setText(result.getMessage());
    }

    private void showAddDialog() {
        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));

        JTextField idField = new JTextField();
        JTextField nameField = new JTextField();
        JTextField passwordField = new JTextField();
        JTextField phoneField = new JTextField();
        JTextField balanceField = new JTextField("0");
        JComboBox<String> typeBox = new JComboBox<String>(new String[]{"Хадгаламж", "Харилцах"});

        panel.add(new JLabel("Дансны ID"));
        panel.add(idField);
        panel.add(new JLabel("Нэр"));
        panel.add(nameField);
        panel.add(new JLabel("Нууц үг"));
        panel.add(passwordField);
        panel.add(new JLabel("Утас"));
        panel.add(phoneField);
        panel.add(new JLabel("Төрөл"));
        panel.add(typeBox);
        panel.add(new JLabel("Эхний үлдэгдэл"));
        panel.add(balanceField);

        int option = JOptionPane.showConfirmDialog(this, panel, "Шинэ данс нэмэх",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (option == JOptionPane.OK_OPTION) {
            double balance;

            try {
                balance = Double.parseDouble(balanceField.getText().trim());
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Эхний үлдэгдэл буруу байна.");
                return;
            }

            OperationResult result = bankService.createAccount(
                    String.valueOf(typeBox.getSelectedItem()),
                    idField.getText().trim(),
                    nameField.getText().trim(),
                    passwordField.getText(),
                    phoneField.getText().trim(),
                    balance
            );

            JOptionPane.showMessageDialog(this, result.getMessage());

            if (result.isSuccess()) {
                refreshTable();
            }
        }
    }

    private void deleteSelectedAccount() {
        String accountId = getSelectedAccountId();

        if (accountId.equals("")) {
            accountId = JOptionPane.showInputDialog(this, "Устгах дансны ID оруулна уу:");

            if (accountId == null) {
                return;
            }
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                accountId + " дансыг устгах уу?", "Баталгаажуулалт", JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            OperationResult result = bankService.deleteAccount(accountId.trim());
            JOptionPane.showMessageDialog(this, result.getMessage());

            if (result.isSuccess()) {
                refreshTable();
            }
        }
    }

    private void showSearchDialog() {
        String keyword = JOptionPane.showInputDialog(this, "ID эсвэл нэрээр хайна уу:");

        if (keyword == null) {
            return;
        }

        JOptionPane.showMessageDialog(this, bankService.buildSearchResultText(keyword.trim()));
    }

    private void showSortDialog() {
        String sortType = (String) JOptionPane.showInputDialog(this,
                "Эрэмбэлэх төрлийг сонгоно уу:", "Эрэмбэлэх",
                JOptionPane.PLAIN_MESSAGE, null,
                new String[]{"ID-аар", "Нэрээр", "Үлдэгдлээр"}, "ID-аар");

        if (sortType == null) {
            return;
        }

        bankService.sortAccounts(sortType);
        refreshTable();
    }

    private void showAllTransactions() {
        JTextArea transactionArea = new JTextArea(bankService.getAllTransactionsText(), 20, 45);
        transactionArea.setEditable(false);
        transactionArea.setLineWrap(true);
        transactionArea.setWrapStyleWord(true);
        JOptionPane.showMessageDialog(this, new JScrollPane(transactionArea),
                "Бүх гүйлгээ", JOptionPane.INFORMATION_MESSAGE);
    }

    private String getSelectedAccountId() {
        int selectedRow = accountTable.getSelectedRow();

        if (selectedRow < 0) {
            return "";
        }

        return String.valueOf(tableModel.getValueAt(selectedRow, 0));
    }

    private void logout() {
        dispose();
        loginFrame.setVisible(true);
    }
}
