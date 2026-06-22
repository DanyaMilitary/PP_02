public class Exercise {
    private int id;
    private int userId;
    private String name;
    private String muscleGroup;
    private boolean isCustom;
    private String createdAt;

    public Exercise(int id, int userId, String name, String muscleGroup, boolean isCustom, String createdAt) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.muscleGroup = muscleGroup;
        this.isCustom = isCustom;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public int getUserId() { return userId; }
    public String getName() { return name; }
    public String getMuscleGroup() { return muscleGroup; }
    public boolean isCustom() { return isCustom; }
    public String getCreatedAt() { return createdAt; }
}
