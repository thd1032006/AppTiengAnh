package view;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.List;
import dao.WordDAO;
import dao.LessonDAO;
import model.Word;
import model.Lesson;
import helper.UIHelper;

public class VocabularyManagerPanel extends JPanel {

    private final WordDAO   wordDAO   = new WordDAO();
    private final LessonDAO lessonDAO = new LessonDAO();
    private Runnable onDataChanged;
    private JTabbedPane tabs;

    public VocabularyManagerPanel() {
        setLayout(new BorderLayout(0, 0));
        setBackground(UIHelper.BG_PAGE);
        setBorder(new EmptyBorder(30, 40, 30, 40));

        add(buildHeader(), BorderLayout.NORTH);

        tabs = new JTabbedPane();
        tabs.setFont(new Font("Segoe UI", Font.BOLD, 14));
        tabs.setBackground(UIHelper.BG_PAGE);
        tabs.addTab(" Từ vựng", buildWordTab());
        tabs.addTab(" Lesson",  buildLessonTab());
        add(tabs, BorderLayout.CENTER);
    }

    public void setOnDataChanged(Runnable callback) { this.onDataChanged = callback; }
    private void fireDataChanged() { if (onDataChanged != null) onDataChanged.run(); }

    private JPanel buildHeader() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(UIHelper.BG_PAGE);
        p.setBorder(new EmptyBorder(0, 0, 20, 0));

        JLabel title = new JLabel(" Quản lý từ vựng & Lesson");
        title.setFont(new Font("Segoe UI", Font.BOLD, 26));
        title.setForeground(UIHelper.TEXT_PRIMARY);
        p.add(title, BorderLayout.WEST);

        JLabel hint = new JLabel("Thêm / sửa / xóa từ vựng và chủ đề");
        hint.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        hint.setForeground(UIHelper.TEXT_SECOND);
        p.add(hint, BorderLayout.EAST);
        return p;
    }

    private final JTextField txtWord    = new JTextField(20);
    private final JTextField txtType    = new JTextField(20);
    private final JTextField txtMeaning = new JTextField(20);
    private final JTextField txtPronun  = new JTextField(20);
    private final JTextField txtExample = new JTextField(20);
    private final JComboBox<Lesson> cbLesson = new JComboBox<>();

    private final JButton btnWordAdd    = makeBtn(" Thêm", new Color(209, 250, 229), new Color(6, 95, 70));
    private final JButton btnWordUpdate = makeBtn(" Sửa",  new Color(219, 234, 254), new Color(29, 78, 216));
    private final JButton btnWordDelete = makeBtn(" Xóa",  new Color(254, 226, 226), new Color(185, 28, 28));

    private final JLabel lblWordFilter = new JLabel("Hiển thị: tất cả từ vựng");

    private final String[] WORD_COLS = {"ID", "Từ", "Loại", "Nghĩa", "Phát âm", "Ví dụ", "Lesson ID"};
    private final DefaultTableModel wordTableModel = new DefaultTableModel(WORD_COLS, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable wordTable = new JTable(wordTableModel);
    private int selectedWordId = -1;
    private int filterLessonId = -1;

    private JPanel buildWordTab() {
        JPanel tab = new JPanel(new BorderLayout(0, 0));
        tab.setBackground(UIHelper.BG_PAGE);
        tab.setBorder(new EmptyBorder(16, 0, 0, 0));

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                buildWordFormCard(), buildWordTableCard());
        split.setResizeWeight(0.30);
        split.setDividerSize(6);
        split.setBorder(null);
        split.setBackground(UIHelper.BG_PAGE);
        tab.add(split, BorderLayout.CENTER);

        loadLessonsIntoCombo();
        refreshWordTable();
        wireWordListeners();
        return tab;
    }

    private JPanel buildWordFormCard() {
        JPanel card = createCard();
        card.setLayout(new BorderLayout(0, 12));

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(5, 8, 5, 8);
        gc.anchor = GridBagConstraints.WEST;

        String[]     labels = {"Từ:", "Loại từ:", "Nghĩa:", "Phát âm:", "Ví dụ:", "Lesson:"};
        JComponent[] fields = {txtWord, txtType, txtMeaning, txtPronun, txtExample, cbLesson};

        for (int i = 0; i < labels.length; i++) {
            gc.gridx = (i % 2) * 2;
            gc.gridy = i / 2;
            gc.fill  = GridBagConstraints.NONE;
            JLabel lbl = new JLabel(labels[i]);
            lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
            lbl.setForeground(UIHelper.TEXT_SECOND);
            form.add(lbl, gc);

            gc.gridx++;
            gc.fill    = GridBagConstraints.HORIZONTAL;
            gc.weightx = 1.0;
            if (fields[i] instanceof JTextField) UIHelper.styleTextField((JTextField) fields[i]);
            else UIHelper.styleComboBox((JComboBox<?>) fields[i]);
            form.add(fields[i], gc);
        }
        card.add(form, BorderLayout.CENTER);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        btnRow.setOpaque(false);
        btnRow.add(btnWordAdd);
        btnRow.add(btnWordUpdate);
        btnRow.add(btnWordDelete);
        card.add(btnRow, BorderLayout.SOUTH);
        return card;
    }

    private JPanel buildWordTableCard() {
        JPanel card = createCard();
        card.setLayout(new BorderLayout(0, 8));

        JPanel filterBar = new JPanel(new BorderLayout(8, 0));
        filterBar.setOpaque(false);
        lblWordFilter.setFont(new Font("Segoe UI", Font.ITALIC, 13));
        lblWordFilter.setForeground(UIHelper.TEXT_SECOND);
        filterBar.add(lblWordFilter, BorderLayout.WEST);

        JButton btnShowAll = new JButton("Xem tất cả");
        btnShowAll.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        btnShowAll.setForeground(new Color(29, 78, 216));
        btnShowAll.setBorderPainted(false);
        btnShowAll.setContentAreaFilled(false);
        btnShowAll.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnShowAll.addActionListener(e -> {
            filterLessonId = -1;
            lblWordFilter.setText("Hiển thị: tất cả từ vựng");
            cbLesson.setSelectedIndex(0);
            refreshWordTable();
        });
        filterBar.add(btnShowAll, BorderLayout.EAST);
        card.add(filterBar, BorderLayout.NORTH);

        styleTable(wordTable);
        wordTable.getColumnModel().getColumn(0).setMinWidth(0);
        wordTable.getColumnModel().getColumn(0).setMaxWidth(0);
        wordTable.getColumnModel().getColumn(0).setWidth(0);
        wordTable.getColumnModel().getColumn(6).setMaxWidth(90);

        JScrollPane scroll = new JScrollPane(wordTable);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(UIHelper.BG_CARD);
        card.add(scroll, BorderLayout.CENTER);
        return card;
    }

    private void loadLessonsIntoCombo() {
        cbLesson.removeAllItems();
        Lesson placeholder = new Lesson();
        cbLesson.addItem(placeholder);
        for (Lesson l : lessonDAO.getAllLessons()) cbLesson.addItem(l);
        cbLesson.setSelectedIndex(0);
    }

    public void refreshWordTable() {
        wordTableModel.setRowCount(0);
        List<Word> words = (filterLessonId > 0)
                ? wordDAO.getWordsByLesson(filterLessonId)
                : wordDAO.getAllWords();
        for (Word w : words) {
            wordTableModel.addRow(new Object[]{
                w.getId(), w.getWord(), w.getType(), w.getMeaning(),
                w.getPronunciation(), w.getExample(), w.getLessonId()
            });
        }
    }

    public void refreshTable() {
        filterLessonId = -1;
        lblWordFilter.setText("Hiển thị: tất cả từ vựng");
        refreshWordTable();
        loadLessonsIntoCombo();
        refreshLessonTable();
    }

    private void wireWordListeners() {
        wordTable.getSelectionModel().addListSelectionListener(ev -> {
            if (!ev.getValueIsAdjusting() && wordTable.getSelectedRow() != -1) {
                int row = wordTable.getSelectedRow();
                selectedWordId = (int) wordTableModel.getValueAt(row, 0);
                txtWord.setText(str(wordTableModel.getValueAt(row, 1)));
                txtType.setText(str(wordTableModel.getValueAt(row, 2)));
                txtMeaning.setText(str(wordTableModel.getValueAt(row, 3)));
                txtPronun.setText(str(wordTableModel.getValueAt(row, 4)));
                txtExample.setText(str(wordTableModel.getValueAt(row, 5)));
                int lid = (int) wordTableModel.getValueAt(row, 6);
                for (int i = 0; i < cbLesson.getItemCount(); i++) {
                    if (cbLesson.getItemAt(i).getId() == lid) { cbLesson.setSelectedIndex(i); break; }
                }
            }
        });

        btnWordAdd.addActionListener(e -> {
            StringBuilder missing = new StringBuilder();
            if (txtWord.getText().isBlank())    missing.append("• Từ\n");
            if (txtType.getText().isBlank())    missing.append("• Loại từ\n");
            if (txtMeaning.getText().isBlank()) missing.append("• Nghĩa\n");
            if (txtPronun.getText().isBlank())  missing.append("• Phát âm\n");
            if (txtExample.getText().isBlank()) missing.append("• Ví dụ\n");
            Lesson sel = (Lesson) cbLesson.getSelectedItem();
            if (sel == null || sel.getId() == 0) missing.append("• Lesson\n");

            if (missing.length() > 0) {
                warn("Không thể thêm từ vựng!\nVui lòng điền đầy đủ các trường sau:\n" + missing);
                return;
            }
            boolean ok = wordDAO.addWord(buildWordFromForm());
            if (ok) { info("Thêm từ thành công!"); clearWordForm(); refreshWordTable(); refreshLessonTable(); fireDataChanged(); }
            else warn("Thêm thất bại, kiểm tra kết nối DB.");
        });

        btnWordUpdate.addActionListener(e -> {
            if (selectedWordId < 0) { warn("Hãy chọn dòng cần sửa trong bảng!"); return; }
            if (txtWord.getText().isBlank() || txtMeaning.getText().isBlank()) {
                warn("Từ và Nghĩa không được để trống!"); return;
            }
            Lesson sel = (Lesson) cbLesson.getSelectedItem();
            if (sel == null || sel.getId() == 0) { warn("Hãy chọn Lesson cho từ này!"); return; }
            Word w = buildWordFromForm();
            w.setId(selectedWordId);
            boolean ok = wordDAO.updateWord(w);
            if (ok) { info("Cập nhật từ thành công!"); clearWordForm(); refreshWordTable(); refreshLessonTable(); fireDataChanged(); }
            else warn("Cập nhật thất bại.");
        });

        btnWordDelete.addActionListener(e -> {
            if (selectedWordId < 0) { warn("Hãy chọn dòng cần xóa trong bảng!"); return; }
            int c = JOptionPane.showConfirmDialog(this,
                "Xóa từ \"" + txtWord.getText() + "\"?\nHành động này không thể hoàn tác.",
                "Xác nhận xóa từ", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (c == JOptionPane.YES_OPTION) {
                boolean ok = wordDAO.deleteWord(selectedWordId);
                if (ok) { info("Đã xóa từ vựng!"); clearWordForm(); refreshWordTable(); refreshLessonTable(); fireDataChanged(); }
                else warn("Xóa thất bại.");
            }
        });
    }

    private Word buildWordFromForm() {
        Word w = new Word();
        w.setWord(txtWord.getText().trim());
        w.setType(txtType.getText().trim());
        w.setMeaning(txtMeaning.getText().trim());
        w.setPronunciation(txtPronun.getText().trim());
        w.setExample(txtExample.getText().trim());
        Lesson sel = (Lesson) cbLesson.getSelectedItem();
        if (sel != null && sel.getId() != 0) w.setLessonId(sel.getId());
        return w;
    }

    private void clearWordForm() {
        txtWord.setText(""); txtType.setText(""); txtMeaning.setText("");
        txtPronun.setText(""); txtExample.setText("");
        cbLesson.setSelectedIndex(0);
        selectedWordId = -1;
        wordTable.clearSelection();
    }

    private final JTextField txtLessonName = new JTextField(24);
    private final JTextField txtLessonDesc = new JTextField(36);

    private final JButton btnLessonAdd    = makeBtn(" Thêm lesson",  new Color(209, 250, 229), new Color(6, 95, 70));
    private final JButton btnLessonUpdate = makeBtn(" Sửa lesson",   new Color(219, 234, 254), new Color(29, 78, 216));
    private final JButton btnLessonDelete = makeBtn(" Xóa lesson",   new Color(254, 226, 226), new Color(185, 28, 28));

    private final String[] LESSON_COLS = {"ID", "Tên Lesson", "Mô tả", "Số từ"};
    private final DefaultTableModel lessonTableModel = new DefaultTableModel(LESSON_COLS, 0) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    private final JTable lessonTable = new JTable(lessonTableModel);
    private int selectedLessonId = -1;

    private JPanel buildLessonTab() {
        JPanel tab = new JPanel(new BorderLayout(0, 0));
        tab.setBackground(UIHelper.BG_PAGE);
        tab.setBorder(new EmptyBorder(16, 0, 0, 0));

        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                buildLessonFormCard(), buildLessonTableCard());
        split.setResizeWeight(0.28);
        split.setDividerSize(6);
        split.setBorder(null);
        split.setBackground(UIHelper.BG_PAGE);
        tab.add(split, BorderLayout.CENTER);

        refreshLessonTable();
        wireLessonListeners();
        return tab;
    }

    private JPanel buildLessonFormCard() {
        JPanel card = createCard();
        card.setLayout(new BorderLayout(0, 12));

        JPanel form = new JPanel(new GridBagLayout());
        form.setOpaque(false);
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(6, 8, 6, 8);
        gc.anchor = GridBagConstraints.WEST;

        gc.gridx = 0; gc.gridy = 0; gc.fill = GridBagConstraints.NONE;
        JLabel lblName = new JLabel("Tên chủ đề:");
        lblName.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblName.setForeground(UIHelper.TEXT_SECOND);
        form.add(lblName, gc);
        gc.gridx = 1; gc.fill = GridBagConstraints.HORIZONTAL; gc.weightx = 1.0;
        UIHelper.styleTextField(txtLessonName);
        form.add(txtLessonName, gc);

        gc.gridx = 0; gc.gridy = 1; gc.fill = GridBagConstraints.NONE;
        JLabel lblDesc = new JLabel("Mô tả:");
        lblDesc.setFont(new Font("Segoe UI", Font.BOLD, 13));
        lblDesc.setForeground(UIHelper.TEXT_SECOND);
        form.add(lblDesc, gc);
        gc.gridx = 1; gc.fill = GridBagConstraints.HORIZONTAL; gc.weightx = 1.0;
        UIHelper.styleTextField(txtLessonDesc);
        form.add(txtLessonDesc, gc);

        card.add(form, BorderLayout.CENTER);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        btnRow.setOpaque(false);
        btnRow.add(btnLessonAdd);
        btnRow.add(btnLessonUpdate);
        btnRow.add(btnLessonDelete);

        JLabel note = new JLabel("\"Xóa lesson\" sẽ xóa toàn bộ từ vựng thuộc chủ đề đó.");
        note.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        note.setForeground(new Color(185, 28, 28));
        note.setHorizontalAlignment(JLabel.CENTER);

        JPanel south = new JPanel(new BorderLayout(0, 6));
        south.setOpaque(false);
        south.add(btnRow, BorderLayout.CENTER);
        south.add(note,   BorderLayout.SOUTH);
        card.add(south, BorderLayout.SOUTH);
        return card;
    }

    private JPanel buildLessonTableCard() {
        JPanel card = createCard();
        card.setLayout(new BorderLayout(0, 8));

        JLabel hint = new JLabel("Click vào lesson để xem từ vựng của chủ đề đó");
        hint.setFont(new Font("Segoe UI", Font.ITALIC, 12));
        hint.setForeground(UIHelper.TEXT_SECOND);
        card.add(hint, BorderLayout.NORTH);

        styleTable(lessonTable);
        lessonTable.getColumnModel().getColumn(0).setMinWidth(0);
        lessonTable.getColumnModel().getColumn(0).setMaxWidth(0);
        lessonTable.getColumnModel().getColumn(0).setWidth(0);
        lessonTable.getColumnModel().getColumn(3).setMaxWidth(70);

        JScrollPane scroll = new JScrollPane(lessonTable);
        scroll.setBorder(null);
        scroll.getViewport().setBackground(UIHelper.BG_CARD);
        card.add(scroll, BorderLayout.CENTER);
        return card;
    }

    public void refreshLessonTable() {
        lessonTableModel.setRowCount(0);
        for (Lesson l : lessonDAO.getAllLessons()) {
            int count = wordDAO.countWordsByLesson(l.getId());
            lessonTableModel.addRow(new Object[]{
                l.getId(), l.getLessonName(),
                l.getDescription() != null ? l.getDescription() : "",
                count
            });
        }
    }

    private void wireLessonListeners() {
        lessonTable.getSelectionModel().addListSelectionListener(ev -> {
            if (!ev.getValueIsAdjusting() && lessonTable.getSelectedRow() != -1) {
                int row = lessonTable.getSelectedRow();
                selectedLessonId = (int) lessonTableModel.getValueAt(row, 0);
                txtLessonName.setText(str(lessonTableModel.getValueAt(row, 1)));
                txtLessonDesc.setText(str(lessonTableModel.getValueAt(row, 2)));

                String lessonName = str(lessonTableModel.getValueAt(row, 1));
                filterLessonId = selectedLessonId;
                lblWordFilter.setText("Hiển thị: từ thuộc \"" + lessonName + "\"");
                for (int i = 0; i < cbLesson.getItemCount(); i++) {
                    if (cbLesson.getItemAt(i).getId() == selectedLessonId) {
                        cbLesson.setSelectedIndex(i);
                        break;
                    }
                }
                refreshWordTable();
            }
        });

        btnLessonAdd.addActionListener(e -> {
            if (txtLessonName.getText().isBlank()) { warn("Tên chủ đề không được để trống!"); return; }
            Lesson l = new Lesson();
            l.setLessonName(txtLessonName.getText().trim());
            l.setDescription(txtLessonDesc.getText().trim());
            int newId = lessonDAO.addLesson(l);
            if (newId > 0) {
                info("Thêm lesson \"" + l.getLessonName() + "\" thành công!");
                clearLessonForm();
                refreshLessonTable();
                loadLessonsIntoCombo();
                fireDataChanged();
            } else warn("Thêm lesson thất bại.");
        });

        btnLessonUpdate.addActionListener(e -> {
            if (selectedLessonId < 0) { warn("Hãy chọn dòng cần sửa trong bảng!"); return; }
            if (txtLessonName.getText().isBlank()) { warn("Tên chủ đề không được để trống!"); return; }
            Lesson l = new Lesson();
            l.setId(selectedLessonId);
            l.setLessonName(txtLessonName.getText().trim());
            l.setDescription(txtLessonDesc.getText().trim());
            boolean ok = lessonDAO.updateLesson(l);
            if (ok) {
                info("Cập nhật lesson thành công!");
                clearLessonForm();
                refreshLessonTable();
                loadLessonsIntoCombo();
                fireDataChanged();
            } else warn("Cập nhật thất bại.");
        });

        btnLessonDelete.addActionListener(e -> {
            if (selectedLessonId < 0) { warn("Hãy chọn lesson cần xóa trong bảng!"); return; }
            String lessonName = str(lessonTableModel.getValueAt(lessonTable.getSelectedRow(), 1));
            int c = JOptionPane.showConfirmDialog(this,
                "Xóa lesson \"" + lessonName + "\"?\n" +
                "Toàn bộ từ vựng thuộc chủ đề này cũng sẽ bị xóa.\n" +
                "Hành động này không thể hoàn tác!",
                "Xác nhận xóa lesson", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (c == JOptionPane.YES_OPTION) {
                boolean ok = lessonDAO.deleteLesson(selectedLessonId);
                if (ok) {
                    info("Đã xóa lesson \"" + lessonName + "\" và toàn bộ từ vựng của nó!");
                    clearLessonForm();
                    filterLessonId = -1;
                    lblWordFilter.setText("Hiển thị: tất cả từ vựng");
                    refreshLessonTable();
                    loadLessonsIntoCombo();
                    refreshWordTable();
                    fireDataChanged();
                } else warn("Xóa thất bại.");
            }
        });
    }

    private void clearLessonForm() {
        txtLessonName.setText("");
        txtLessonDesc.setText("");
        selectedLessonId = -1;
        lessonTable.clearSelection();
    }

    private JPanel createCard() {
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 0, 0, 8));
                g2.fillRoundRect(4, 6, getWidth() - 8, getHeight() - 8, 16, 16);
                g2.setColor(UIHelper.BG_CARD);
                g2.fillRoundRect(0, 0, getWidth() - 4, getHeight() - 4, 16, 16);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(20, 28, 20, 28));
        return card;
    }

    private void styleTable(JTable tbl) {
        tbl.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tbl.setRowHeight(34);
        tbl.setShowGrid(false);
        tbl.setIntercellSpacing(new Dimension(0, 0));
        tbl.setSelectionBackground(new Color(219, 234, 254));
        tbl.setSelectionForeground(UIHelper.TEXT_PRIMARY);
        tbl.setBackground(UIHelper.BG_CARD);
        tbl.setForeground(UIHelper.TEXT_PRIMARY);
        tbl.setFocusable(false);

        JTableHeader th = tbl.getTableHeader();
        th.setFont(new Font("Segoe UI", Font.BOLD, 13));
        th.setBackground(new Color(241, 245, 249));
        th.setForeground(UIHelper.TEXT_SECOND);
        th.setPreferredSize(new Dimension(0, 36));
        th.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, UIHelper.BORDER_COLOR));
    }

    private String str(Object o) { return o == null ? "" : o.toString(); }

    private void info(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Thành công", JOptionPane.INFORMATION_MESSAGE);
    }

    private void warn(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Lưu ý", JOptionPane.WARNING_MESSAGE);
    }

    private static JButton makeBtn(String text, Color bg, Color fg) {
        JButton btn = new JButton(text) {
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
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        btn.setFocusPainted(false);
        btn.setContentAreaFilled(false);
        btn.setOpaque(false);
        btn.setBorder(new EmptyBorder(10, 20, 10, 20));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return btn;
    }
}
