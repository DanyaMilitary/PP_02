// Set.java
public class Set {
    private int id;
    private int exerciseId;
    private String workoutDate;
    private double weight;
    private int reps;
    private String createdAt;
    private String exerciseName;

    public Set(int id, int exerciseId, String workoutDate, double weight, int reps, String createdAt) {
        this.id = id;
        this.exerciseId = exerciseId;
        this.workoutDate = workoutDate;
        this.weight = weight;
        this.reps = reps;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public int getExerciseId() { return exerciseId; }
    public String getWorkoutDate() { return workoutDate; }
    public double getWeight() { return weight; }
    public int getReps() { return reps; }
    public String getCreatedAt() { return createdAt; }
    public String getExerciseName() { return exerciseName; }
    public void setExerciseName(String exerciseName) { this.exerciseName = exerciseName; }
}