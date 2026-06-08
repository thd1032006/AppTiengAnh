package view;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import dao.UserDAO;
import model.User;
import helper.UIHelper;

public class ForgotPasswordPanel extends JDialog {

    private final UserDAO userDAO = new UserDAO();

    // Bước 1: Xác minh email/phone
    private JPanel stepVerify;
    private JTextField txtContact;
    private JLabel lblVerifyError;

    // Bước 2: Đặt mật khẩu mới
    private JPanel stepReset;
    private JPasswordField txtNewPass, txtConfirmPass;
    private JLabel lblResetError;

    private User foundUser; // user tìm được sau bước 1

    public ForgotPasswordPanel(JFrame parent) {
        super(parent, "Quên mật khẩu", true);
        setSize(440, 420);
        setResizable(false);
        setLocationRelativeTo(parent);
        getContentPane().setBackground(UIHelper.BG_PAGE);
        setLayout(new BorderLayout());

        // Dùng CardLayout để chuyển giữa 2 bước
        CardLayout cardLayout = new CardLayout();
        JPanel container = new JPanel(cardLayout);
        container.setBackground(UIHelper.BG_PAGE);

        stepVerify = buildStepVerify(cardLayout, container);
        stepReset  = buildStepReset();

        container.add(stepVerify, "verify");
        container.add(stepReset,  "reset");
        cardLayout.show(container, "verify");

        add(container, BorderLayout.CENTER);
    }

    // ── BƯỚC 1: Xác minh email hoặc số điện thoại ────────────────────────────
    private JPanel buildStepVerify(CardLayout cardLayout, JPanel container) {
        JPanel main = new JPanel(new GridBagLayout());
        main.setBackground(UIHelper.BG_PAGE);
        main.setBorder(new EmptyBorder(36, 44, 36, 44));

        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx = 0; gc.fill = GridBagConstraints.HORIZONTAL; gc.weightx = 1;

        gc.gridy = 0; gc.insets = new Insets(0, 0, 8, 0);
        JLabel lblTitle = new JLabel("Quên mật khẩu", JLabel.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(UIHelper.ACCENT_BLUE);
        main.add(lblTitle, gc);

        gc.gridy = 1; gc.insets = new Insets(0, 0, 32, 0);
        JLabel lblSub = new JLabel("<html><div style='text-align:center'>Nhập email hoặc số điện thoại<br>đã đăng ký để xác minh danh tính</div></html>", JLabel.CENTER);
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSub.setForeground(UIHelper.TEXT_SECOND);
        main.add(lblSub, gc);

        JPanel card = buildCard();
        card.setLayout(new GridBagLayout());
        GridBagConstraints cc = new GridBagConstraints();
        cc.gridx = 0; cc.fill = GridBagConstraints.HORIZONTAL; cc.weightx = 1;

        cc.gridy = 0; cc.insets = new Insets(0, 0, 6, 0);
        JLabel lbl = new JLabel("Email hoặc số điện thoại");
        lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl.setForeground(UIHelper.TEXT_PRIMARY);
        card.add(lbl, cc);

        cc.gridy = 1; cc.insets = new Insets(0, 0, 18, 0);
        txtContact = new JTextField();
        UIHelper.styleTextField(txtContact);
        card.add(txtContact, cc);

        cc.gridy = 2; cc.insets = new Insets(0, 0, 12, 0);
        lblVerifyError = new JLabel(" ", JLabel.CENTER);
        lblVerifyError.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblVerifyError.setForeground(UIHelper.ACCENT_RED);
        card.add(lblVerifyError, cc);

        cc.gridy = 3; cc.insets = new Insets(0, 0, 12, 0);
        JButton btnVerify = makePrimaryBtn("Xác minh");
        card.add(btnVerify, cc);

        cc.gridy = 4; cc.insets = new Insets(0, 0, 0, 0);
        JButton btnBack = makeSecondaryBtn("Quay lại đăng nhập");
        card.add(btnBack, cc);

        gc.gridy = 2; gc.insets = new Insets(0, 0, 0, 0);
        main.add(card, gc);

        btnVerify.addActionListener(e -> {
            String contact = txtContact.getText().trim();
            if (contact.isEmpty()) {
                lblVerifyError.setText("Vui lòng nhập email hoặc số điện thoại!");
                return;
            }
            foundUser = userDAO.findByEmailOrPhone(contact);
            if (foundUser == null) {
                lblVerifyError.setText("Không tìm thấy tài khoản với thông tin này!");
            } else {
                lblVerifyError.setText(" ");
                cardLayout.show(container, "reset");
            }
        });

        btnBack.addActionListener(e -> dispose());
        txtContact.addActionListener(e -> btnVerify.doClick());

        return main;
    }

    // ── BƯỚC 2: Đặt mật khẩu mới ─────────────────────────────────────────────
    private JPanel buildStepReset() {
        JPanel main = new JPanel(new GridBagLayout());
        main.setBackground(UIHelper.BG_PAGE);
        main.setBorder(new EmptyBorder(36, 44, 36, 44));

        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx = 0; gc.fill = GridBagConstraints.HORIZONTAL; gc.weightx = 1;

        gc.gridy = 0; gc.insets = new Insets(0, 0, 8, 0);
        JLabel lblTitle = new JLabel("Đặt mật khẩu mới", JLabel.CENTER);
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblTitle.setForeground(UIHelper.ACCENT_BLUE);
        main.add(lblTitle, gc);

        gc.gridy = 1; gc.insets = new Insets(0, 0, 32, 0);
        JLabel lblSub = new JLabel("Xác minh thành công! Nhập mật khẩu mới", JLabel.CENTER);
        lblSub.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblSub.setForeground(UIHelper.ACCENT_GREEN);
        main.add(lblSub, gc);

        JPanel card = buildCard();
        card.setLayout(new GridBagLayout());
        GridBagConstraints cc = new GridBagConstraints();
        cc.gridx = 0; cc.fill = GridBagConstraints.HORIZONTAL; cc.weightx = 1;

        cc.gridy = 0; cc.insets = new Insets(0, 0, 6, 0);
        JLabel lbl1 = new JLabel("Mật khẩu mới");
        lbl1.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl1.setForeground(UIHelper.TEXT_PRIMARY);
        card.add(lbl1, cc);

        cc.gridy = 1; cc.insets = new Insets(0, 0, 14, 0);
        txtNewPass = new JPasswordField();
        UIHelper.styleTextField(txtNewPass);
        card.add(txtNewPass, cc);

        cc.gridy = 2; cc.insets = new Insets(0, 0, 6, 0);
        JLabel lbl2 = new JLabel("Nhập lại mật khẩu mới");
        lbl2.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lbl2.setForeground(UIHelper.TEXT_PRIMARY);
        card.add(lbl2, cc);

        cc.gridy = 3; cc.insets = new Insets(0, 0, 18, 0);
        txtConfirmPass = new JPasswordField();
        UIHelper.styleTextField(txtConfirmPass);
        card.add(txtConfirmPass, cc);

        cc.gridy = 4; cc.insets = new Insets(0, 0, 12, 0);
        lblResetError = new JLabel(" ", JLabel.CENTER);
        lblResetError.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblResetError.setForeground(UIHelper.ACCENT_RED);
        card.add(lblResetError, cc);

        cc.gridy = 5; cc.insets = new Insets(0, 0, 0, 0);
        JButton btnSave = makePrimaryBtn("Lưu mật khẩu mới");
        card.add(btnSave, cc);

        gc.gridy = 2; gc.insets = new Insets(0, 0, 0, 0);
        main.add(card, gc);

        btnSave.addActionListener(e -> doResetPassword());
        txtConfirmPass.addActionListener(e -> doResetPassword());

        return main;
    }

    private void doResetPassword() {
        String newPass  = new String(txtNewPass.getPassword());
        String confirm  = new String(txtConfirmPass.getPassword());

        if (newPass.isEmpty()) {
            lblResetError.setText("Vui lòng nhập mật khẩu mới!");
            txtNewPass.requestFocus();
            return;
        }
        if (newPass.length() < 6) {
            lblResetError.setText("Mật khẩu phải có ít nhất 6 ký tự!");
            txtNewPass.requestFocus();
            return;
        }
        if (!newPass.equals(confirm)) {
            lblResetError.setText("Mật khẩu nhập lại không khớp!");
            txtConfirmPass.setText("");
            txtConfirmPass.requestFocus();
            return;
        }

        boolean ok = userDAO.resetPassword(foundUser.getId(), newPass);
        if (ok) {
            JOptionPane.showMessageDialog(this,
                "Đặt lại mật khẩu thành công!\nVui lòng đăng nhập lại.",
                "Thành công", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            lblResetError.setText("Có lỗi xảy ra, vui lòng thử lại!");
        }
    }

    // ── HELPERS ──────────────────────────────────────────────────────────────
    private JPanel buildCard()               { return UIHelper.buildCard(); }
    private JButton makePrimaryBtn(String t) { return UIHelper.makePrimaryBtn(t); }
    private JButton makeSecondaryBtn(String t){ return UIHelper.makeSecondaryBtn(t); }
}
