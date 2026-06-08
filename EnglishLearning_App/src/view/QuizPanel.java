package view;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import dao.WordDAO;
import dao.ProgressDAO;
import model.Word;
import helper.UIHelper;

public class QuizPanel extends JPanel {

    private List<Word> allWords;
    private List<Word> currentQuizList;
    private Word currentQuizWord;
    private String correctQuizAnswer;
    private int userId;

    private int score = 0, qNum = 1, timeLeft = 180;
    private boolean quizInProgress = false;
    private boolean isMcqMode = true;

    private Timer timer;
    private JLabel lblScore, lblTimer, lblProgress;
    private JButton btnStart, btnNext, btnReset, btnCancel;
    private JButton btnModeMcq, btnModeWrite;
    private CardLayout modeLayout;
    private JPanel modePanel;
    private JProgressBar quizProgress;
    private final ProgressDAO progressDAO = new ProgressDAO();

    private final JRadioButton[] mcqOptions  = new JRadioButton[4];
    private final JPanel[]       optWrappers = new JPanel[4];
    private final ButtonGroup mcqGroup = new ButtonGroup();
    private final String[] mcqValues = new String[4];
    private JLabel lblMcqQuestion, lblMcqResult;

    private static final Color[] OPT_COLORS = {
        new Color(239, 246, 255), new Color(240, 253, 244),
        new Color(255, 251, 235), new Color(253, 242, 248)
    };

    private JLabel lblWriteQuestion, lblWriteResult, lblWriteHint;
    private JTextField txtWriteAnswer;

    public QuizPanel() {
        this(1);
    }

    public QuizPanel(int userId) {
        this.userId = userId;
        allWords = new WordDAO().getAllWords();
        setLayout(new BorderLayout(0, 0));
        setBackground(UIHelper.BG_PAGE);
        setBorder(new EmptyBorder(30, 40, 30, 40));

        JPanel header = new JPanel(new BorderLayout(16, 0));
        header.setBackground(UIHelper.BG_PAGE);
        header.setBorder(new EmptyBorder(0, 0, 20, 0));

        JLabel lblTitle = new JLabel("Kiểm tra");
        lblTitle.setFont(new Font("Segoe UI", Font.BOLD, 26));
        lblTitle.setForeground(UIHelper.TEXT_PRIMARY);

        JPanel rightRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        rightRow.setBackground(UIHelper.BG_PAGE);

        btnModeMcq   = makeModeBtn("Trắc nghiệm");
        btnModeWrite = makeModeBtn("Viết");
        setModeActive(btnModeMcq, btnModeWrite);

        lblScore = new JLabel("Điểm: 0");
        lblScore.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblScore.setForeground(UIHelper.ACCENT_BLUE);

        lblTimer = new JLabel("03:00");
        lblTimer.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTimer.setForeground(UIHelper.ACCENT_RED);

        rightRow.add(btnModeMcq);
        rightRow.add(btnModeWrite);
        rightRow.add(Box.createHorizontalStrut(8));
        rightRow.add(lblScore);
        rightRow.add(lblTimer);

        header.add(lblTitle, BorderLayout.WEST);
        header.add(rightRow, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        JPanel card = new JPanel(new BorderLayout(0, 16)) {
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
        card.setBorder(new EmptyBorder(28, 36, 28, 36));

        JPanel topRow = new JPanel(new BorderLayout(8, 0));
        topRow.setOpaque(false);
        lblProgress = new JLabel("Câu 0 / 30");
        lblProgress.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        lblProgress.setForeground(UIHelper.TEXT_SECOND);
        quizProgress = new JProgressBar(0, 30);
        quizProgress.setValue(0);
        quizProgress.setStringPainted(false);
        quizProgress.setForeground(UIHelper.ACCENT_BLUE);
        quizProgress.setBackground(UIHelper.BORDER_COLOR);
        quizProgress.setPreferredSize(new Dimension(0, 6));
        quizProgress.setBorder(null);
        topRow.add(lblProgress, BorderLayout.WEST);
        topRow.add(quizProgress, BorderLayout.CENTER);
        card.add(topRow, BorderLayout.NORTH);

        modeLayout = new CardLayout();
        modePanel  = new JPanel(modeLayout);
        modePanel.setBackground(UIHelper.BG_CARD);
        modePanel.add(buildMcqPanel(),   "mcq");
        modePanel.add(buildWritePanel(), "write");
        modeLayout.show(modePanel, "mcq");
        card.add(modePanel, BorderLayout.CENTER);
        add(card, BorderLayout.CENTER);

        JPanel botRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 16, 0));
        botRow.setBackground(UIHelper.BG_PAGE);
        botRow.setBorder(new EmptyBorder(20, 0, 0, 0));

        btnStart  = makeBtn("Bắt đầu thi", new Color(219, 234, 254), new Color(29, 78, 216));
        btnNext   = makeBtn("Chốt đáp án", new Color(209, 250, 229), new Color(6, 95, 70));
        btnReset  = makeBtn("Làm lại",      new Color(254, 243, 199), new Color(146, 64, 14));
        btnCancel = makeBtn("Hủy bỏ",       new Color(254, 226, 226), UIHelper.ACCENT_RED);

        btnNext.setEnabled(false);
        btnReset.setEnabled(false);
        btnCancel.setEnabled(false);

        botRow.add(btnStart);
        botRow.add(btnNext);
        botRow.add(btnReset);
        botRow.add(btnCancel);
        add(botRow, BorderLayout.SOUTH);

        timer = new Timer(1000, e -> {
            timeLeft--;
            lblTimer.setText(String.format("%02d:%02d", timeLeft / 60, timeLeft % 60));
            lblTimer.setForeground(timeLeft <= 30 ? UIHelper.ACCENT_RED : UIHelper.TEXT_PRIMARY);
            if (timeLeft <= 0) finishQuiz();
        });

        btnModeMcq.addActionListener(e -> {
            if (!quizInProgress) {
                isMcqMode = true;
                modeLayout.show(modePanel, "mcq");
                setModeActive(btnModeMcq, btnModeWrite);
            }
        });
        btnModeWrite.addActionListener(e -> {
            if (!quizInProgress) {
                isMcqMode = false;
                modeLayout.show(modePanel, "write");
                setModeActive(btnModeWrite, btnModeMcq);
            }
        });
        btnStart.addActionListener(e -> startQuiz());
        btnNext.addActionListener(e -> handleNext());
        btnReset.addActionListener(e -> handleReset());
        btnCancel.addActionListener(e -> handleCancel());
    }

    private JPanel buildMcqPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 16));
        panel.setBackground(UIHelper.BG_CARD);
        panel.setBorder(new EmptyBorder(16, 0, 0, 0));

        lblMcqQuestion = new JLabel("Chọn chế độ và bấm Bắt đầu thi!", JLabel.CENTER);
        lblMcqQuestion.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblMcqQuestion.setForeground(UIHelper.TEXT_PRIMARY);
        lblMcqQuestion.setBorder(new EmptyBorder(8, 0, 8, 0));
        panel.add(lblMcqQuestion, BorderLayout.NORTH);

        JPanel answerBlock = new JPanel(new GridLayout(5, 1, 0, 10));
        answerBlock.setOpaque(false);
        for (int i = 0; i < 4; i++) {
            mcqOptions[i] = createOptionBtn(OPT_COLORS[i]);
            mcqGroup.add(mcqOptions[i]);

            final int idx = i;
            JPanel optWrapper = new JPanel(new BorderLayout()) {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(mcqOptions[idx].getBackground());
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
                    g2.setColor(new Color(203, 213, 225));
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 12, 12);
                    g2.dispose();
                }
            };
            optWrapper.setOpaque(false);
            optWrapper.setBorder(new EmptyBorder(6, 12, 6, 12));
            optWrapper.add(mcqOptions[i], BorderLayout.CENTER);
            optWrappers[i] = optWrapper;
            answerBlock.add(optWrapper);
        }
        lblMcqResult = new JLabel(" ", JLabel.CENTER);
        lblMcqResult.setFont(new Font("Segoe UI", Font.BOLD, 18));
        answerBlock.add(lblMcqResult);
        panel.add(answerBlock, BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildWritePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(UIHelper.BG_CARD);
        panel.setBorder(new EmptyBorder(16, 0, 0, 0));

        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx = 0;

        gc.gridy = 0; gc.insets = new Insets(0, 0, 20, 0);
        lblWriteQuestion = new JLabel("Chọn chế độ và bấm Bắt đầu thi!", JLabel.CENTER);
        lblWriteQuestion.setFont(new Font("Segoe UI", Font.BOLD, 24));
        lblWriteQuestion.setForeground(UIHelper.TEXT_PRIMARY);
        panel.add(lblWriteQuestion, gc);

        gc.gridy = 1; gc.insets = new Insets(0, 0, 20, 0);
        lblWriteHint = new JLabel("Nhập nghĩa tiếng Việt", JLabel.CENTER);
        lblWriteHint.setFont(new Font("Segoe UI", Font.ITALIC, 14));
        lblWriteHint.setForeground(UIHelper.TEXT_SECOND);
        panel.add(lblWriteHint, gc);

        gc.gridy = 2; gc.insets = new Insets(0, 0, 20, 0); gc.fill = GridBagConstraints.HORIZONTAL;
        txtWriteAnswer = new JTextField(28);
        UIHelper.styleTextField(txtWriteAnswer);
        txtWriteAnswer.setHorizontalAlignment(JTextField.CENTER);
        txtWriteAnswer.setEnabled(false);
        txtWriteAnswer.addActionListener(e -> handleNext());
        panel.add(txtWriteAnswer, gc);

        gc.gridy = 3; gc.insets = new Insets(0, 0, 0, 0); gc.fill = GridBagConstraints.NONE;
        lblWriteResult = new JLabel(" ", JLabel.CENTER);
        lblWriteResult.setFont(new Font("Segoe UI", Font.BOLD, 18));
        panel.add(lblWriteResult, gc);
        return panel;
    }

    private void startQuiz() {
        if (allWords.size() < 30) {
            JOptionPane.showMessageDialog(this,
                "Cần ít nhất 30 từ trong cơ sở dữ liệu để thi!", "Thiếu dữ liệu", JOptionPane.WARNING_MESSAGE);
            return;
        }
        currentQuizList = new ArrayList<>(allWords);
        Collections.shuffle(currentQuizList);
        currentQuizList = currentQuizList.subList(0, 30);
        score = 0; qNum = 1; timeLeft = 180;
        quizInProgress = true;

        lblScore.setText("Điểm: 0");
        lblTimer.setText("03:00");
        btnStart.setEnabled(false);
        btnNext.setEnabled(true);
        btnReset.setEnabled(true);
        btnCancel.setEnabled(true);
        btnModeMcq.setEnabled(false);
        btnModeWrite.setEnabled(false);

        loadQuestion();
        timer.start();
    }

    private void handleNext() {
        if (btnNext.getText().equals("Chốt đáp án")) {
            boolean correct = false;
            if (isMcqMode) {
                int sel = -1;
                for (int i = 0; i < mcqOptions.length; i++) if (mcqOptions[i].isSelected()) { sel = i; break; }
                if (sel == -1) return;
                for (JRadioButton rb : mcqOptions) rb.setEnabled(false);
                if (mcqValues[sel].equals(correctQuizAnswer)) {
                    correct = true;
                    lblMcqResult.setText("Chính xác!");
                    lblMcqResult.setForeground(UIHelper.ACCENT_GREEN);
                } else {
                    lblMcqResult.setText("Sai!  Đáp án: " + correctQuizAnswer);
                    lblMcqResult.setForeground(UIHelper.ACCENT_RED);
                }
                highlightMcqOptions(sel);
            } else {
                String ans = txtWriteAnswer.getText().trim();
                if (ans.isEmpty()) return;
                txtWriteAnswer.setEnabled(false);
                if (ans.equalsIgnoreCase(correctQuizAnswer)) {
                    correct = true;
                    lblWriteResult.setText("Chính xác!");
                    lblWriteResult.setForeground(UIHelper.ACCENT_GREEN);
                } else {
                    lblWriteResult.setText("Sai!  Đáp án: " + correctQuizAnswer);
                    lblWriteResult.setForeground(UIHelper.ACCENT_RED);
                }
            }
            if (correct) { score += 10; lblScore.setText("Điểm: " + score); }
            btnNext.setText("Câu tiếp theo");
        } else {
            qNum++;
            loadQuestion();
        }
    }

    private void loadQuestion() {
        if (qNum > 30) { finishQuiz(); return; }
        currentQuizWord = currentQuizList.get(qNum - 1);
        lblProgress.setText("Câu " + qNum + " / 30");
        quizProgress.setValue(qNum);
        if (isMcqMode) loadMcqQuestion();
        else loadWriteQuestion();
        btnNext.setText("Chốt đáp án");
    }

    private void loadMcqQuestion() {
        correctQuizAnswer = currentQuizWord.getMeaning();
        List<String> opts = new ArrayList<>();
        opts.add(correctQuizAnswer);
        List<Word> tmp = new ArrayList<>(allWords);
        tmp.remove(currentQuizWord);
        Collections.shuffle(tmp);
        for (int i = 0; i < 3 && i < tmp.size(); i++) opts.add(tmp.get(i).getMeaning());
        Collections.shuffle(opts);

        lblMcqQuestion.setText("Câu " + qNum + ":  " + currentQuizWord.getWord());
        for (int i = 0; i < 4; i++) {
            mcqValues[i] = opts.get(i);
            mcqOptions[i].setText("  " + (char)('A' + i) + ".  " + opts.get(i));
            mcqOptions[i].setEnabled(true);
            mcqOptions[i].setBackground(OPT_COLORS[i]);
            mcqOptions[i].setForeground(UIHelper.TEXT_PRIMARY);
            if (optWrappers[i] != null) optWrappers[i].repaint();
        }
        mcqGroup.clearSelection();
        lblMcqResult.setText(" ");
    }

    private void loadWriteQuestion() {
        correctQuizAnswer = currentQuizWord.getMeaning();
        lblWriteQuestion.setText("Câu " + qNum + ":  " + currentQuizWord.getWord());
        lblWriteHint.setText("Nhập nghĩa tiếng Việt");
        txtWriteAnswer.setText("");
        txtWriteAnswer.setEnabled(true);
        txtWriteAnswer.requestFocus();
        lblWriteResult.setText(" ");
    }

    private void highlightMcqOptions(int selectedIdx) {
        for (int i = 0; i < 4; i++) {
            if (mcqValues[i].equals(correctQuizAnswer)) {
                mcqOptions[i].setBackground(new Color(209, 250, 229));
                mcqOptions[i].setForeground(new Color(6, 95, 70));
            } else if (i == selectedIdx) {
                mcqOptions[i].setBackground(new Color(254, 226, 226));
                mcqOptions[i].setForeground(UIHelper.ACCENT_RED);
            }
            if (optWrappers[i] != null) optWrappers[i].repaint();
        }
    }

    private void resetState() {
        timer.stop();
        quizInProgress = false;
        score = 0; qNum = 1; timeLeft = 180;
        lblScore.setText("Điểm: 0");
        lblTimer.setText("03:00");
        lblProgress.setText("Câu 0 / 30");
        quizProgress.setValue(0);
        lblMcqQuestion.setText("Chọn chế độ và bấm Bắt đầu thi!");
        lblMcqResult.setText(" ");
        lblWriteQuestion.setText("Chọn chế độ và bấm Bắt đầu thi!");
        lblWriteResult.setText(" ");
        txtWriteAnswer.setText("");
        txtWriteAnswer.setEnabled(false);
        for (JRadioButton rb : mcqOptions) {
            rb.setEnabled(false);
            rb.setBackground(OPT_COLORS[0]);
            rb.setForeground(UIHelper.TEXT_PRIMARY);
        }
        mcqGroup.clearSelection();
        btnStart.setEnabled(true);
        btnNext.setEnabled(false);
        btnReset.setEnabled(false);
        btnCancel.setEnabled(false);
        btnModeMcq.setEnabled(true);
        btnModeWrite.setEnabled(true);
    }

    private void handleReset() {
        if (!quizInProgress) return;
        int choice = JOptionPane.showOptionDialog(this,
            "Bạn vẫn đang làm bài kiểm tra.\nBạn có chắc chắn muốn làm lại?",
            "Xác nhận làm lại",
            JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE, null,
            new String[]{"Không", "Có"}, "Không");
        if (choice == 1) resetState();
    }

    private void handleCancel() {
        if (!quizInProgress) return;
        int choice = JOptionPane.showConfirmDialog(this,
            "Bạn có chắc chắn muốn hủy bỏ bài kiểm tra?\nKết quả sẽ không được lưu.",
            "Xác nhận hủy bỏ", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (choice == JOptionPane.YES_OPTION) {
            resetState();
            JOptionPane.showMessageDialog(this, "Đã hủy bỏ bài kiểm tra.", "Hủy bỏ", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void finishQuiz() {
        timer.stop();
        quizInProgress = false;
        btnNext.setEnabled(false);
        btnStart.setEnabled(true);
        btnReset.setEnabled(false);
        btnCancel.setEnabled(false);
        btnModeMcq.setEnabled(true);
        btnModeWrite.setEnabled(true);
        for (JRadioButton rb : mcqOptions) rb.setEnabled(false);
        txtWriteAnswer.setEnabled(false);
        lblMcqQuestion.setText("Đã nộp bài!");
        lblWriteQuestion.setText("Đã nộp bài!");
        lblProgress.setText("Hoàn thành");
        quizProgress.setValue(30);
        progressDAO.saveQuizScore(userId, score);
        JOptionPane.showMessageDialog(this,
            "Hoàn thành!\nBạn đạt " + score + " / 300 điểm.",
            "Kết quả", JOptionPane.INFORMATION_MESSAGE);
    }

    private void setModeActive(JButton active, JButton inactive) {
        active.setBackground(UIHelper.ACCENT_BLUE);
        active.setForeground(Color.WHITE);
        inactive.setBackground(new Color(226, 232, 240));
        inactive.setForeground(UIHelper.TEXT_PRIMARY);
    }

    private JButton makeModeBtn(String text) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setBorder(new EmptyBorder(7, 18, 7, 18));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    private JRadioButton createOptionBtn(Color bg) {
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
        rb.setEnabled(false);
        rb.setBorder(new EmptyBorder(10, 16, 10, 16));
        rb.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return rb;
    }

    private JButton makeBtn(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 15));
        btn.setFocusPainted(false);
        btn.setBorder(new EmptyBorder(12, 28, 12, 28));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }

    public void reloadWords() {
        if (!quizInProgress) {
            allWords = new WordDAO().getAllWords();
        }
    }
}
