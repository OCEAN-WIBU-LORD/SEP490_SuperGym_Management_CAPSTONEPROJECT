package com.supergym.sep490_supergymmanagement.models;

import java.util.List;
import java.util.Objects;

public class Exercise {
    private String id;
    private String name;
    private String muscleGroup;
    private String equipment;
    private List<Set> sets; // Danh sách các set trong bài tập

    // Constructor không tham số (Firebase yêu cầu)
    public Exercise() {}

    // Constructor có tham số
    public Exercise(String id, String name, String muscleGroup, String equipment, List<Set> sets) {
        this.id = id;
        this.name = name;
        this.muscleGroup = muscleGroup;
        this.equipment = equipment;
        this.sets = sets;
    }

    // Getter và Setter
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getMuscleGroup() { return muscleGroup; }
    public void setMuscleGroup(String muscleGroup) { this.muscleGroup = muscleGroup; }

    public String getEquipment() { return equipment; }
    public void setEquipment(String equipment) { this.equipment = equipment; }

    public List<Set> getSets() { return sets; }
    public void setSets(List<Set> sets) { this.sets = sets; }

    /**
     * Kiểm tra xem bài tập có ít nhất một set hay không.
     *
     * @return true nếu có ít nhất một set, ngược lại false
     */
    public boolean hasSets() {
        return sets != null && !sets.isEmpty();
    }
    public boolean isValid() {
        // Kiểm tra các trường cơ bản không được null hoặc rỗng
        if (id == null || id.trim().isEmpty()) {
            return false;
        }
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        if (muscleGroup == null || muscleGroup.trim().isEmpty()) {
            return false;
        }
        if (equipment == null) {
            // Equipment có thể là chuỗi rỗng, tùy thuộc vào yêu cầu của bạn
            // Nếu equipment bắt buộc, hãy kiểm tra như các trường khác
            return false;
        }
        // Bạn có thể thêm các kiểm tra bổ sung khác nếu cần
        return true;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Exercise exercise = (Exercise) o;
        return Objects.equals(id, exercise.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
