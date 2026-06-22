public class Record {
    private int id;
    private int exerciseId;
    private String exerciseName;
    private String muscleGroup;
    private double weight;
    private int reps;
    private String recordDate;
    private String createdAt;

    public Record(int id, int exerciseId, String exerciseName, String muscleGroup,
                  double weight, int reps, String recordDate, String createdAt) {
        this.id = id;
        this.exerciseId = exerciseId;
        this.exerciseName = exerciseName;
        this.muscleGroup = muscleGroup;
        this.weight = weight;
        this.reps = reps;
        this.recordDate = recordDate;
        this.createdAt = createdAt;
    }

    public int getId() { return id; }
    public int getExerciseId() { return exerciseId; }
    public String getExerciseName() { return exerciseName; }
    public String getMuscleGroup() { return muscleGroup; }
    public double getWeight() { return weight; }
    public int getReps() { return reps; }
    public String getRecordDate() { return recordDate; }
    public String getCreatedAt() { return createdAt; }
}