public class Workout {
    private String date;
    private String exerciseName;
    private double weight;
    private int reps;
    private String muscleGroup;

    public Workout(String date, String exerciseName, double weight, int reps, String muscleGroup) {
        this.date = date;
        this.exerciseName = exerciseName;
        this.weight = weight;
        this.reps = reps;
        this.muscleGroup = muscleGroup;
    }

    // Геттеры и сеттеры
    public String getDate() { return date; }
    public String getExerciseName() { return exerciseName; }
    public double getWeight() { return weight; }
    public int getReps() { return reps; }
    public String getMuscleGroup() { return muscleGroup; }
}