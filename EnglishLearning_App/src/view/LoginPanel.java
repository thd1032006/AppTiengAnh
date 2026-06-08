package view;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import dao.UserDAO;
import model.User;
import helper.UIHelper;

public class LoginPanel extends JFrame {

    private final JTextField txtUsername;
    private final JPasswordField txtPassword;
    private final JLabel lblError;
    private final UserDAO userDAO = new UserDAO();

    public LoginPanel() {
        setTitle("English Mastery Pro - Đăng nhập");
        setSize(460, 520);
        setResizable(false);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(UIHelper.BG_PAGE);
        setLayout(new BorderLayout());

        JPanel main = new JPanel(new GridBagLayout());
        main.setBackground(UIHelper.BG_PAGE);
        main.setBorder(new EmptyBorder(40, 50, 40, 50));

        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx = 0; gc.fill = GridBagConstraints.HORIZONTAL; gc.weightx = 1;

        gc.gridy = 0; gc.insets = new Insets(0, 0, 8, 0);
        JLabel lblTitle = new JLabel("English Mastery Pro", JLabel.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitle.setForeground(UIHelper.ACCENT_BLUE);
        main.add(lblTitle, gc);

        gc.gridy = 1; gc.insets = new Insets(0, 0, 36, 0);
        JLabel lblSub = new JLabel("Đăng nhập để tiếp tục học", JLabel.CENTER);
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        lblSub.setForeground(UIHelper.TEXT_SECOND);
        main.add(lblSub, gc);

        JPanel card = buildCard();
        card.setLayout(new GridBagLayout());
        GridBagConstraints cc = new GridBagConstraints();
        cc.gridx = 0; cc.fill = GridBagConstraints.HORIZONTAL; cc.weightx = 1;

        cc.gridy = 0; cc.insets = new Insets(0, 0, 6, 0);
        card.add(makeLabel("Tên đăng nhập"), cc);

        cc.gridy = 1; cc.insets = new Insets(0, 0, 18, 0);
        txtUsername = new JTextField();
        UIHelper.styleTextField(txtUsername);
        card.add(txtUsername, cc);

        cc.gridy = 2; cc.insets = new Insets(0, 0, 6, 0);
        card.add(makeLabel("Mật khẩu"), cc);

        cc.gridy = 3; cc.insets = new Insets(0, 0, 8, 0);
        txtPassword = new JPasswordField();
        UIHelper.styleTextField(txtPassword);
        card.add(txtPassword, cc);

        cc.gridy = 4; cc.insets = new Insets(0, 0, 24, 0);
        JLabel lblForgot = new JLabel("Quên mật khẩu?");
        lblForgot.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblForgot.setForeground(UIHelper.ACCENT_BLUE);
        lblForgot.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblForgot.setHorizontalAlignment(JLabel.RIGHT);
        card.add(lblForgot, cc);

        cc.gridy = 5; cc.insets = new Insets(0, 0, 12, 0);
        lblError = new JLabel(" ", JLabel.CENTER);
        lblError.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblError.setForeground(UIHelper.ACCENT_RED);
        card.add(lblError, cc);

        cc.gridy = 6; cc.insets = new Insets(0, 0, 16, 0);
        JButton btnLogin = makePrimaryBtn("Đăng nhập");
        card.add(btnLogin, cc);

        cc.gridy = 7; cc.insets = new Insets(0, 0, 0, 0);
        JPanel regRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        regRow.setOpaque(false);
        JLabel lblHave = new JLabel("Chưa có tài khoản?");
        lblHave.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblHave.setForeground(UIHelper.TEXT_SECOND);
        JLabel lblRegister = new JLabel("Tạo tài khoản");
        lblRegister.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblRegister.setForeground(UIHelper.ACCENT_BLUE);
        lblRegister.setCursor(new Cursor(Cursor.HAND_CURSOR));
        regRow.add(lblHave);
        regRow.add(lblRegister);
        card.add(regRow, cc);

        gc.gridy = 2; gc.insets = new Insets(0, 0, 0, 0);
        main.add(card, gc);
        add(main, BorderLayout.CENTER);

        btnLogin.addActionListener(e -> doLogin());
        txtPassword.addActionListener(e -> doLogin());
        txtUsername.addActionListener(e -> txtPassword.requestFocus());

        lblForgot.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                new ForgotPasswordPanel(LoginPanel.this).setVisible(true);
            }
        });

        lblRegister.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                new RegisterPanel(LoginPanel.this).setVisible(true);
            }
        });
    }

    private void doLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            lblError.setText("Vui lòng nhập đầy đủ thông tin!");
            return;
        }

        User user = userDAO.login(username, password);
        if (user != null) {
            dispose();
            SwingUtilities.invokeLater(() -> new MainDashboard(user).setVisible(true));
        } else {
            lblError.setText("Tên đăng nhập hoặc mật khẩu không đúng!");
            txtPassword.setText("");
            txtPassword.requestFocus();
        }
    }

    private JPanel buildCard()               { return UIHelper.buildCard(); }
    private JLabel makeLabel(String text)    { return UIHelper.makeFormLabel(text); }
    private JButton makePrimaryBtn(String t) { return UIHelper.makePrimaryBtn(t); }
}
