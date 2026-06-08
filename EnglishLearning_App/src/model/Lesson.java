package model;

public class Lesson {
    private int id;
    private String lessonName;
    private String description;

    public Lesson() {}
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getLessonName() { return lessonName; }
    public void setLessonName(String lessonName) { this.lessonName = lessonName; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    @Override
    public String toString() {
        if (id == 0 && (lessonName == null || lessonName.isBlank())) return "-- Chọn lesson --";
        return id + ". " + lessonName;
    }
}