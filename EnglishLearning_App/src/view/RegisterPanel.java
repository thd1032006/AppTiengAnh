package view;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import dao.UserDAO;
import helper.UIHelper;

public class RegisterPanel extends JDialog {

    private final JTextField txtUsername, txtEmail, txtPhone;
    private final JPasswordField txtPassword, txtConfirm;
    private final JLabel lblError;
    private final UserDAO userDAO = new UserDAO();

    public RegisterPanel(JFrame parent) {
        super(parent, "Tạo tài khoản", true);
        setSize(480, 620);
        setResizable(false);
        setLocationRelativeTo(parent);
        getContentPane().setBackground(UIHelper.BG_PAGE);
        setLayout(new BorderLayout());

        JPanel main = new JPanel(new GridBagLayout());
        main.setBackground(UIHelper.BG_PAGE);
        main.setBorder(new EmptyBorder(30, 44, 30, 44));

        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx = 0; gc.fill = GridBagConstraints.HORIZONTAL; gc.weightx = 1;

        // Tiêu đề
        gc.gridy = 0; gc.insets = new Insets(0, 0, 6, 0);
        JLabel lblTitle = new JLabel("Tạo tài khoản mới", JLabel.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(UIHelper.ACCENT_BLUE);
        main.add(lblTitle, gc);

        gc.gridy = 1; gc.insets = new Insets(0, 0, 28, 0);
        JLabel lblSub = new JLabel("Điền đầy đủ thông tin bên dưới", JLabel.CENTER);
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSub.setForeground(UIHelper.TEXT_SECOND);
        main.add(lblSub, gc);

        // Card form
        JPanel card = buildCard();
        card.setLayout(new GridBagLayout());
        GridBagConstraints cc = new GridBagConstraints();
        cc.gridx = 0; cc.fill = GridBagConstraints.HORIZONTAL; cc.weightx = 1;

        // Username
        cc.gridy = 0; cc.insets = new Insets(0, 0, 5, 0);
        card.add(makeLabel("Tên đăng nhập *"), cc);
        cc.gridy = 1; cc.insets = new Insets(0, 0, 14, 0);
        txtUsername = new JTextField();
        UIHelper.styleTextField(txtUsername);
        card.add(txtUsername, cc);

        // Email
        cc.gridy = 2; cc.insets = new Insets(0, 0, 5, 0);
        card.add(makeLabel("Email"), cc);
        cc.gridy = 3; cc.insets = new Insets(0, 0, 14, 0);
        txtEmail = new JTextField();
        UIHelper.styleTextField(txtEmail);
        card.add(txtEmail, cc);

        // Số điện thoại
        cc.gridy = 4; cc.insets = new Insets(0, 0, 5, 0);
        card.add(makeLabel("Số điện thoại"), cc);
        cc.gridy = 5; cc.insets = new Insets(0, 0, 14, 0);
        txtPhone = new JTextField();
        UIHelper.styleTextField(txtPhone);
        card.add(txtPhone, cc);

        // Mật khẩu
        cc.gridy = 6; cc.insets = new Insets(0, 0, 5, 0);
        card.add(makeLabel("Mật khẩu *"), cc);
        cc.gridy = 7; cc.insets = new Insets(0, 0, 14, 0);
        txtPassword = new JPasswordField();
        UIHelper.styleTextField(txtPassword);
        card.add(txtPassword, cc);

        // Nhập lại mật khẩu
        cc.gridy = 8; cc.insets = new Insets(0, 0, 5, 0);
        card.add(makeLabel("Nhập lại mật khẩu *"), cc);
        cc.gridy = 9; cc.insets = new Insets(0, 0, 18, 0);
        txtConfirm = new JPasswordField();
        UIHelper.styleTextField(txtConfirm);
        card.add(txtConfirm, cc);

        // Lỗi
        cc.gridy = 10; cc.insets = new Insets(0, 0, 12, 0);
        lblError = new JLabel(" ", JLabel.CENTER);
        lblError.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblError.setForeground(UIHelper.ACCENT_RED);
        card.add(lblError, cc);

        // Nút đăng ký
        cc.gridy = 11; cc.insets = new Insets(0, 0, 12, 0);
        JButton btnRegister = makePrimaryBtn("Tạo tài khoản");
        card.add(btnRegister, cc);

        // Quay lại đăng nhập
        cc.gridy = 12; cc.insets = new Insets(0, 0, 0, 0);
        JButton btnBack = makeSecondaryBtn("Quay lại đăng nhập");
        card.add(btnBack, cc);

        gc.gridy = 2; gc.insets = new Insets(0, 0, 0, 0);
        main.add(card, gc);
        add(main, BorderLayout.CENTER);

        btnRegister.addActionListener(e -> doRegister());
        btnBack.addActionListener(e -> dispose());
    }

    private void doRegister() {
        String username = txtUsername.getText().trim();
        String email    = txtEmail.getText().trim();
        String phone    = txtPhone.getText().trim();
        String password = new String(txtPassword.getPassword());
        String confirm  = new String(txtConfirm.getPassword());

        // Validate bắt buộc
        if (username.isEmpty()) {
            showError("Vui lòng nhập tên đăng nhập!");
            txtUsername.requestFocus();
            return;
        }
        if (username.length() < 4) {
            showError("Tên đăng nhập phải có ít nhất 4 ký tự!");
            txtUsername.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            showError("Vui lòng nhập mật khẩu!");
            txtPassword.requestFocus();
            return;
        }
        if (password.length() < 6) {
            showError("Mật khẩu phải có ít nhất 6 ký tự!");
            txtPassword.requestFocus();
            return;
        }
        if (!password.equals(confirm)) {
            showError("Mật khẩu nhập lại không khớp!");
            txtConfirm.setText("");
            txtConfirm.requestFocus();
            return;
        }
        // Validate email format nếu có nhập
        if (!email.isEmpty() && !email.matches("^[\\w._%+\\-]+@[\\w.\\-]+\\.[a-zA-Z]{2,}$")) {
            showError("Email không đúng định dạng!");
            txtEmail.requestFocus();
            return;
        }
        // Validate phone format nếu có nhập
        if (!phone.isEmpty() && !phone.matches("^[0-9]{9,11}$")) {
            showError("Số điện thoại không hợp lệ (9-11 chữ số)!");
            txtPhone.requestFocus();
            return;
        }

        // Gọi DAO
        String error = userDAO.register(username, password,
                email.isEmpty() ? null : email,
                phone.isEmpty() ? null : phone);

        if (error == null) {
            JOptionPane.showMessageDialog(this,
                "Tạo tài khoản thành công!\nBạn có thể đăng nhập ngay bây giờ.",
                "Thành công", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            showError(error);
        }
    }

    private void showError(String msg) {
        lblError.setText(msg);
    }

    // ── HELPERS ──────────────────────────────────────────────────────────────
    private JPanel buildCard()               { return UIHelper.buildCard(); }
    private JLabel makeLabel(String text)    { return UIHelper.makeFormLabel(text); }
    private JButton makePrimaryBtn(String t) { return UIHelper.makePrimaryBtn(t); }
    private JButton makeSecondaryBtn(String t){ return UIHelper.makeSecondaryBtn(t); }
}
