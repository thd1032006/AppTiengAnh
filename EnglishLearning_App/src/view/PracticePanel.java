package view;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import dao.WordDAO;
import dao.LessonDAO;
import dao.ProgressDAO;
import model.Word;
import model.Lesson;
import helper.UIHelper;

public class PracticePanel extends JPanel {

    private List<Word> allWords;
    private List<Word> lessonWords;
    private int currentIndex = 0;
    private int userId;

    private JComboBox<Object> cbTopic;
    private JComboBox<String> cbMode;
    private CardLayout modeLayout;
    private JPanel modePanel;

    private JLabel lblWriteQuestion, lblWriteResult, lblWriteHint, lblWriteCounter;
    private JTextField txtWriteAnswer;

    private JLabel lblMcqQuestion, lblMcqResult, lblMcqCounter;
    private final JRadioButton[] mcqOptions = new JRadioButton[4];
    private final ButtonGroup mcqGroup = new ButtonGroup();
    private final String[] mcqValues = new String[4];
    private String mcqCorrectAnswer;

    private static final Color[] OPT_COLORS = {
        new Color(239, 246, 255), new Color(240, 253, 244),
        new Color(255, 251, 235), new Color(253, 242, 248)
    };

    private final ProgressDAO progressDAO = new ProgressDAO();

    public PracticePanel() {
        this(1);
    }

    public PracticePanel(int userId) {
        this.userId = userId;
        allWords = new WordDAO().getAllWords();
        setLayout(new BorderLayout(0, 0));
        setBackground(UIHelper.BG_PAGE);
        setBorder(new EmptyBorder(30, 40, 30, 40));

        JPanel header = new JPanel(new BorderLayout(16, 0));
        header.setBackground(UIHelper.BG_PAGE);
        header.setBorder(new EmptyBorder(0, 0, 24, 0));

        JLabel lblTitle = new JLabel("Luyện tập");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitle.setForeground(UIHelper.TEXT_PRIMARY);

        JPanel filterRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        filterRow.setBackground(UIHelper.BG_PAGE);

        cbTopic = new JComboBox<>();
        cbTopic.setPreferredSize(new Dimension(200, 36));
        UIHelper.styleComboBox(cbTopic);
        cbTopic.addItem("-- Chọn chủ đề --");
        for (Lesson l : new LessonDAO().getAllLessons()) cbTopic.addItem(l);

        cbMode = new JComboBox<>(new String[]{"Viết", "Trắc nghiệm"});
        cbMode.setPreferredSize(new Dimension(160, 36));
        UIHelper.styleComboBox(cbMode);

        filterRow.add(new JLabel("Chủ đề:"));
        filterRow.add(cbTopic);
        filterRow.add(Box.createHorizontalStrut(8));
        filterRow.add(new JLabel("Chế độ:"));
        filterRow.add(cbMode);

        header.add(lblTitle, BorderLayout.WEST);
        header.add(filterRow, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        modeLayout = new CardLayout();
        modePanel  = new JPanel(modeLayout);
        modePanel.setBackground(UIHelper.BG_PAGE);
        modePanel.add(buildWriteMode(),   "Viết");
        modePanel.add(buildMcqMode(),     "Trắc nghiệm");
        add(modePanel, BorderLayout.CENTER);

        cbTopic.addActionListener(e -> loadLesson());
        cbMode.addActionListener(e -> switchMode());
    }

    private JPanel buildWriteMode() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UIHelper.BG_PAGE);

        JPanel card = createCard();
        card.setLayout(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx = 0;

        gc.gridy = 0; gc.insets = new Insets(0, 0, 12, 0);
        lblWriteCounter = new JLabel("0 / 0");
        lblWriteCounter.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblWriteCounter.setForeground(UIHelper.TEXT_SECOND);
        card.add(lblWriteCounter, gc);

        gc.gridy = 1; gc.insets = new Insets(0, 0, 20, 0);
        lblWriteQuestion = new JLabel("Chọn chủ đề để bắt đầu", JLabel.CENTER);
        lblWriteQuestion.setFont(new Font("Segoe UI", Font.BOLD, 28));
        lblWriteQuestion.setForeground(UIHelper.TEXT_PRIMARY);
        card.add(lblWriteQuestion, gc);

        gc.gridy = 2; gc.insets = new Insets(0, 0, 28, 0);
        lblWriteHint = new JLabel(" ", JLabel.CENTER);
        lblWriteHint.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        lblWriteHint.setForeground(UIHelper.TEXT_SECOND);
        card.add(lblWriteHint, gc);

        gc.gridy = 3; gc.insets = new Insets(0, 0, 16, 0); gc.fill = GridBagConstraints.HORIZONTAL;
        txtWriteAnswer = new JTextField(22);
        UIHelper.styleTextField(txtWriteAnswer);
        txtWriteAnswer.setHorizontalAlignment(JTextField.CENTER);
        card.add(txtWriteAnswer, gc);

        gc.gridy = 4; gc.insets = new Insets(0, 0, 28, 0); gc.fill = GridBagConstraints.NONE;
        lblWriteResult = new JLabel(" ", JLabel.CENTER);
        lblWriteResult.setFont(new Font("Segoe UI", Font.BOLD, 20));
        card.add(lblWriteResult, gc);

        gc.gridy = 5; gc.insets = new Insets(0, 0, 0, 0);
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 0));
        btnRow.setOpaque(false);
        JButton btnPrev  = makeBtn("← Câu trước", new Color(226, 232, 240), UIHelper.TEXT_PRIMARY);
        JButton btnCheck = makeBtn("Kiểm tra",     new Color(219, 234, 254), new Color(29, 78, 216));
        JButton btnNext  = makeBtn("Câu tiếp →",   new Color(209, 250, 229), new Color(6, 95, 70));
        btnRow.add(btnPrev);
        btnRow.add(btnCheck);
        btnRow.add(btnNext);
        card.add(btnRow, gc);

        panel.add(card, BorderLayout.CENTER);

        btnCheck.addActionListener(e -> checkWriteAnswer());
        btnNext.addActionListener(e -> nextWriteQuestion());
        btnPrev.addActionListener(e -> prevWriteQuestion());
        txtWriteAnswer.addActionListener(e -> checkWriteAnswer());
        return panel;
    }

    private void showWriteQuestion() {
        Word w = lessonWords.get(currentIndex);
        lblWriteQuestion.setText(w.getWord());
        lblWriteHint.setText("Nhập nghĩa tiếng Việt");
        lblWriteCounter.setText((currentIndex + 1) + " / " + lessonWords.size());
        txtWriteAnswer.setText("");
        lblWriteResult.setText(" ");
        txtWriteAnswer.requestFocus();
    }

    private void nextWriteQuestion() {
        if (lessonWords == null || lessonWords.isEmpty()) return;
        currentIndex = (currentIndex + 1) % lessonWords.size();
        showWriteQuestion();
    }

    private void prevWriteQuestion() {
        if (lessonWords == null || lessonWords.isEmpty()) return;
        currentIndex = (currentIndex - 1 + lessonWords.size()) % lessonWords.size();
        showWriteQuestion();
    }

    private void checkWriteAnswer() {
        if (lessonWords == null || lessonWords.isEmpty() || txtWriteAnswer.getText().trim().isEmpty()) return;
        Word w = lessonWords.get(currentIndex);
        if (txtWriteAnswer.getText().trim().equalsIgnoreCase(w.getMeaning())) {
            lblWriteResult.setText("Chính xác!");
            lblWriteResult.setForeground(UIHelper.ACCENT_GREEN);
            progressDAO.markAsMastered(userId, w.getId());
        } else {
            lblWriteResult.setText("Sai!  Đáp án: " + w.getMeaning());
            lblWriteResult.setForeground(UIHelper.ACCENT_RED);
        }
    }

    private JPanel buildMcqMode() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UIHelper.BG_PAGE);

        JPanel card = createCard();
        card.setLayout(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx = 0;

        gc.gridy = 0; gc.insets = new Insets(0, 0, 12, 0);
        lblMcqCounter = new JLabel("0 / 0");
        lblMcqCounter.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblMcqCounter.setForeground(UIHelper.TEXT_SECOND);
        card.add(lblMcqCounter, gc);

        gc.gridy = 1; gc.insets = new Insets(0, 0, 24, 0);
        lblMcqQuestion = new JLabel("Chọn chủ đề để bắt đầu", JLabel.CENTER);
        lblMcqQuestion.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblMcqQuestion.setForeground(UIHelper.TEXT_PRIMARY);
        card.add(lblMcqQuestion, gc);

        gc.gridy = 2; gc.insets = new Insets(0, 0, 16, 0); gc.fill = GridBagConstraints.HORIZONTAL;
        JPanel optPanel = new JPanel(new GridLayout(4, 1, 0, 10));
        optPanel.setOpaque(false);
        for (int i = 0; i < 4; i++) {
            mcqOptions[i] = createRoundedRadio(OPT_COLORS[i]);
            mcqGroup.add(mcqOptions[i]);
            optPanel.add(mcqOptions[i]);
        }
        card.add(optPanel, gc);

        gc.gridy = 3; gc.insets = new Insets(0, 0, 24, 0); gc.fill = GridBagConstraints.NONE;
        lblMcqResult = new JLabel(" ", JLabel.CENTER);
        lblMcqResult.setFont(new Font("Segoe UI", Font.BOLD, 20));
        card.add(lblMcqResult, gc);

        gc.gridy = 4; gc.insets = new Insets(0, 0, 0, 0);
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 0));
        btnRow.setOpaque(false);
        JButton btnPrev  = makeBtn("← Câu trước", new Color(226, 232, 240), UIHelper.TEXT_PRIMARY);
        JButton btnCheck = makeBtn("Kiểm tra",     new Color(219, 234, 254), new Color(29, 78, 216));
        JButton btnNext  = makeBtn("Câu tiếp →",   new Color(209, 250, 229), new Color(6, 95, 70));
        btnRow.add(btnPrev);
        btnRow.add(btnCheck);
        btnRow.add(btnNext);
        card.add(btnRow, gc);

        panel.add(card, BorderLayout.CENTER);

        btnCheck.addActionListener(e -> checkMcqAnswer());
        btnNext.addActionListener(e -> nextMcqQuestion());
        btnPrev.addActionListener(e -> prevMcqQuestion());
        return panel;
    }

    private void showMcqQuestion() {
        Word w = lessonWords.get(currentIndex);
        lblMcqQuestion.setText(w.getWord());
        lblMcqCounter.setText((currentIndex + 1) + " / " + lessonWords.size());

        mcqCorrectAnswer = w.getMeaning();
        List<String> opts = new ArrayList<>();
        opts.add(mcqCorrectAnswer);
        List<Word> tmp = new ArrayList<>(allWords);
        tmp.remove(w);
        Collections.shuffle(tmp);
        for (int i = 0; i < 3 && i < tmp.size(); i++) opts.add(tmp.get(i).getMeaning());
        Collections.shuffle(opts);

        for (int i = 0; i < 4; i++) {
            mcqValues[i] = opts.get(i);
            mcqOptions[i].setText("  " + (char)('A' + i) + ".  " + opts.get(i));
            mcqOptions[i].setEnabled(true);
            mcqOptions[i].setBackground(OPT_COLORS[i]);
            mcqOptions[i].setForeground(UIHelper.TEXT_PRIMARY);
        }
        mcqGroup.clearSelection();
        lblMcqResult.setText(" ");
    }

    private void nextMcqQuestion() {
        if (lessonWords == null || lessonWords.isEmpty()) return;
        currentIndex = (currentIndex + 1) % lessonWords.size();
        showMcqQuestion();
    }

    private void prevMcqQuestion() {
        if (lessonWords == null || lessonWords.isEmpty()) return;
        currentIndex = (currentIndex - 1 + lessonWords.size()) % lessonWords.size();
        showMcqQuestion();
    }

    private void checkMcqAnswer() {
        if (lessonWords == null || lessonWords.isEmpty()) return;
        int sel = -1;
        for (int i = 0; i < mcqOptions.length; i++) if (mcqOptions[i].isSelected()) { sel = i; break; }
        if (sel == -1) return;

        for (JRadioButton rb : mcqOptions) rb.setEnabled(false);

        if (mcqValues[sel].equals(mcqCorrectAnswer)) {
            lblMcqResult.setText("Chính xác!");
            lblMcqResult.setForeground(UIHelper.ACCENT_GREEN);
            mcqOptions[sel].setBackground(new Color(209, 250, 229));
            mcqOptions[sel].setForeground(new Color(6, 95, 70));
            progressDAO.markAsMastered(userId, lessonWords.get(currentIndex).getId());
        } else {
            lblMcqResult.setText("Sai!  Đáp án: " + mcqCorrectAnswer);
            lblMcqResult.setForeground(UIHelper.ACCENT_RED);
            mcqOptions[sel].setBackground(new Color(254, 226, 226));
            mcqOptions[sel].setForeground(UIHelper.ACCENT_RED);
            for (int i = 0; i < 4; i++) {
                if (mcqValues[i].equals(mcqCorrectAnswer)) {
                    mcqOptions[i].setBackground(new Color(209, 250, 229));
                    mcqOptions[i].setForeground(new Color(6, 95, 70));
                }
            }
        }
    }

    private void loadLesson() {
        Object sel = cbTopic.getSelectedItem();
        if (sel instanceof Lesson) {
            lessonWords = new WordDAO().getWordsByLesson(((Lesson) sel).getId());
            if (!lessonWords.isEmpty()) {
                currentIndex = 0;
                if ("Viết".equals(cbMode.getSelectedItem())) showWriteQuestion();
                else showMcqQuestion();
            } else {
                lessonWords = new ArrayList<>();
                lblWriteQuestion.setText("Chủ đề này chưa có từ vựng");
                lblMcqQuestion.setText("Chủ đề này chưa có từ vựng");
            }
        } else {
            lessonWords = new ArrayList<>();
            lblWriteQuestion.setText("Chọn chủ đề để bắt đầu");
            lblMcqQuestion.setText("Chọn chủ đề để bắt đầu");
        }
    }

    private void switchMode() {
        String mode = (String) cbMode.getSelectedItem();
        currentIndex = 0;
        modeLayout.show(modePanel, mode);
        if (lessonWords != null && !lessonWords.isEmpty()) {
            if ("Viết".equals(mode)) showWriteQuestion();
            else showMcqQuestion();
        }
    }

    private JPanel createCard() {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 0, 0, 10));
                g2.fillRoundRect(4, 6, getWidth() - 8, getHeight() - 8, 20, 20);
                g2.setColor(UIHelper.BG_CARD);
                g2.fillRoundRect(0, 0, getWidth() - 4, getHeight() - 4, 20, 20);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(32, 40, 32, 40));
        return card;
    }

    private JRadioButton createRoundedRadio(Color bg) {
        JRadioButton rb = new JRadioButton() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        rb.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        rb.setForeground(UIHelper.TEXT_PRIMARY);
        rb.setBackground(bg);
        rb.setOpaque(false);
        rb.setBorder(new EmptyBorder(10, 16, 10, 16));
        rb.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return rb;
    }

    private JButton makeBtn(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(10, 24, 10, 24));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    public void reloadLessons() {
        Object selected = cbTopic.getSelectedItem();
        cbTopic.removeAllItems();
        cbTopic.addItem("-- Chọn chủ đề --");
        for (Lesson l : new LessonDAO().getAllLessons()) cbTopic.addItem(l);
        if (selected instanceof Lesson selLesson) {
            for (int i = 0; i < cbTopic.getItemCount(); i++) {
                Object item = cbTopic.getItemAt(i);
                if (item instanceof Lesson l && l.getId() == selLesson.getId()) {
                    cbTopic.setSelectedIndex(i);
                    break;
                }
            }
        }
    }
}
