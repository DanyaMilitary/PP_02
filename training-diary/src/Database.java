import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Database {
    private static final String DB_URL = "jdbc:sqlite:TrainingDiary.db";
    private Connection connection;

    public Database() {
        try {
            Class.forName("org.sqlite.JDBC");
            connect();
            createTables();
        } catch (ClassNotFoundException e) {
            System.err.println("SQLite JDBC Driver not found!");
            e.printStackTrace();
            System.exit(1);
        }
    }

    private void connect() {
        try {
            connection = DriverManager.getConnection(DB_URL);
            connection.setAutoCommit(true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createTables() {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS users (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    username TEXT UNIQUE NOT NULL,
                    password TEXT NOT NULL,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS exercises (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    user_id INTEGER NOT NULL,
                    name TEXT NOT NULL,
                    muscle_group TEXT,
                    type TEXT DEFAULT 'standard',
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (user_id) REFERENCES users(id),
                    UNIQUE(user_id, name)
                )
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS sets (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    exercise_id INTEGER NOT NULL,
                    user_id INTEGER NOT NULL,
                    workout_date DATE NOT NULL,
                    weight REAL NOT NULL,
                    reps INTEGER NOT NULL,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (exercise_id) REFERENCES exercises(id),
                    FOREIGN KEY (user_id) REFERENCES users(id)
                )
            """);

            stmt.execute("""
                CREATE TABLE IF NOT EXISTS records (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    exercise_id INTEGER NOT NULL,
                    user_id INTEGER NOT NULL,
                    weight REAL NOT NULL,
                    reps INTEGER NOT NULL,
                    record_date DATE NOT NULL,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (exercise_id) REFERENCES exercises(id),
                    FOREIGN KEY (user_id) REFERENCES users(id)
                )
            """);

            System.out.println("Таблицы созданы/проверены");
            addAllExercises();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addAllExercises() {
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM exercises WHERE user_id = 0")) {
            if (rs.next() && rs.getInt(1) > 0) {
                System.out.println("Упражнения уже есть в базе");
                return;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        System.out.println("Добавляем упражнения...");

        String[][] exercises = {
                {"Жим штанги лежа", "Грудь"},
                {"Жим гантелей лежа", "Грудь"},
                {"Жим штанги на наклонной скамье", "Грудь"},
                {"Жим гантелей на наклонной скамье", "Грудь"},
                {"Разведение гантелей лежа", "Грудь"},
                {"Сведение рук в кроссовере", "Грудь"},
                {"Отжимания на брусьях", "Грудь"},
                {"Отжимания от пола", "Грудь"},
                {"Пуловер с гантелью", "Грудь"},
                {"Становая тяга", "Спина"},
                {"Становая тяга сумо", "Спина"},
                {"Тяга штанги в наклоне", "Спина"},
                {"Тяга гантели в наклоне", "Спина"},
                {"Тяга верхнего блока", "Спина"},
                {"Тяга нижнего блока", "Спина"},
                {"Тяга горизонтального блока", "Спина"},
                {"Подтягивания широким хватом", "Спина"},
                {"Подтягивания обратным хватом", "Спина"},
                {"Гиперэкстензия", "Спина"},
                {"Шраги со штангой", "Спина"},
                {"Шраги с гантелями", "Спина"},
                {"Мертвая тяга", "Спина"},
                {"Румынская тяга", "Спина"},
                {"Приседания со штангой", "Ноги"},
                {"Приседания с гантелями", "Ноги"},
                {"Фронтальные приседания", "Ноги"},
                {"Жим ногами", "Ноги"},
                {"Разгибание ног сидя", "Ноги"},
                {"Сгибание ног лежа", "Ноги"},
                {"Выпады со штангой", "Ноги"},
                {"Выпады с гантелями", "Ноги"},
                {"Болгарские выпады", "Ноги"},
                {"Гакк-приседания", "Ноги"},
                {"Подъем на носки стоя", "Ноги"},
                {"Подъем на носки сидя", "Ноги"},
                {"Ягодичный мостик", "Ноги"},
                {"Жим штанги стоя", "Плечи"},
                {"Жим гантелей сидя", "Плечи"},
                {"Жим Арнольда", "Плечи"},
                {"Разводка гантелей в стороны", "Плечи"},
                {"Разводка гантелей вперед", "Плечи"},
                {"Тяга штанги к подбородку", "Плечи"},
                {"Подъем штанги на бицепс", "Руки"},
                {"Подъем гантелей на бицепс", "Руки"},
                {"Подъем гантелей на бицепс хаммер", "Руки"},
                {"Концентрационный подъем на бицепс", "Руки"},
                {"Подъем на бицепс в скамье Скотта", "Руки"},
                {"Французский жим", "Руки"},
                {"Жим лежа узким хватом", "Руки"},
                {"Разгибание рук с гантелью из-за головы", "Руки"},
                {"Разгибание рук на блоке", "Руки"},
                {"Скручивания на пресс", "Пресс"},
                {"Подъем ног в висе", "Пресс"},
                {"Планка", "Пресс"},
                {"Боковые скручивания", "Пресс"},
                {"Русский твист", "Пресс"},
                {"Махи гирей", "Другое"},
                {"Рывок гири", "Другое"},
                {"Толчок гири", "Другое"},
                {"Берпи", "Другое"},
                {"Выпрыгивания", "Другое"}
        };

        String sql = "INSERT INTO exercises (user_id, name, muscle_group, type) VALUES (0, ?, ?, 'standard')";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            for (String[] ex : exercises) {
                pstmt.setString(1, ex[0]);
                pstmt.setString(2, ex[1]);
                try {
                    pstmt.executeUpdate();
                } catch (SQLException e) {
                    // Пропускаем дубликаты
                }
            }
            System.out.println("Упражнения добавлены");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean registerUser(String username, String password) {
        String sql = "INSERT INTO users (username, password) VALUES (?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            System.err.println("Ошибка регистрации: " + e.getMessage());
            return false;
        }
    }

    public int loginUser(String username, String password) {
        String sql = "SELECT id FROM users WHERE username = ? AND password = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            System.err.println("Ошибка входа: " + e.getMessage());
        }
        return -1;
    }

    public boolean addExercise(int userId, String name, String muscleGroup, boolean isCustom) {
        String type = isCustom ? "custom" : "standard";
        String sql = "INSERT INTO exercises (user_id, name, muscle_group, type) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, name);
            pstmt.setString(3, muscleGroup);
            pstmt.setString(4, type);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Exercise> getExercises(int userId) {
        List<Exercise> exercises = new ArrayList<>();
        String sql = """
            SELECT id, user_id, name, muscle_group, type, created_at 
            FROM exercises 
            WHERE user_id = ? OR user_id = 0 
            ORDER BY muscle_group, name
        """;
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String type = rs.getString("type");
                boolean isCustom = type != null && type.equals("custom");
                exercises.add(new Exercise(
                        rs.getInt("id"),
                        rs.getInt("user_id"),
                        rs.getString("name"),
                        rs.getString("muscle_group"),
                        isCustom,
                        rs.getString("created_at")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return exercises;
    }

    public boolean deleteExercise(int exerciseId) {
        try {
            String deleteSets = "DELETE FROM sets WHERE exercise_id = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(deleteSets)) {
                pstmt.setInt(1, exerciseId);
                pstmt.executeUpdate();
            }

            String deleteRecords = "DELETE FROM records WHERE exercise_id = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(deleteRecords)) {
                pstmt.setInt(1, exerciseId);
                pstmt.executeUpdate();
            }

            String deleteExercise = "DELETE FROM exercises WHERE id = ?";
            try (PreparedStatement pstmt = connection.prepareStatement(deleteExercise)) {
                pstmt.setInt(1, exerciseId);
                pstmt.executeUpdate();
            }

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateExerciseName(int exerciseId, String newName) {
        String sql = "UPDATE exercises SET name = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, newName);
            pstmt.setInt(2, exerciseId);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ===== ИСПРАВЛЕННЫЙ МЕТОД addSet - ДОБАВЛЯЕМ user_id =====
    public boolean addSet(int userId, int exerciseId, String workoutDate, double weight, int reps) {
        String sql = "INSERT INTO sets (exercise_id, user_id, workout_date, weight, reps) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, exerciseId);
            pstmt.setInt(2, userId);
            pstmt.setString(3, workoutDate);
            pstmt.setDouble(4, weight);
            pstmt.setInt(5, reps);
            int affected = pstmt.executeUpdate();
            System.out.println("Добавлен подход: userId=" + userId + ", exerciseId=" + exerciseId + ", weight=" + weight + ", reps=" + reps);
            return true;
        } catch (SQLException e) {
            System.err.println("Ошибка добавления подхода: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    // ===== ИСПРАВЛЕННЫЙ МЕТОД getSetsByDate - ИЩЕМ ТОЛЬКО ПО user_id =====
    public List<Set> getSetsByDate(int userId, String date) {
        List<Set> sets = new ArrayList<>();
        String sql = """
            SELECT s.*, e.name as exercise_name 
            FROM sets s 
            JOIN exercises e ON s.exercise_id = e.id 
            WHERE s.user_id = ? AND s.workout_date = ?
            ORDER BY e.name
        """;
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            pstmt.setString(2, date);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Set set = new Set(
                        rs.getInt("id"),
                        rs.getInt("exercise_id"),
                        rs.getString("workout_date"),
                        rs.getDouble("weight"),
                        rs.getInt("reps"),
                        rs.getString("created_at")
                );
                set.setExerciseName(rs.getString("exercise_name"));
                sets.add(set);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return sets;
    }

    public boolean updateSet(int setId, double weight, int reps) {
        String sql = "UPDATE sets SET weight = ?, reps = ? WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setDouble(1, weight);
            pstmt.setInt(2, reps);
            pstmt.setInt(3, setId);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteSet(int setId) {
        String sql = "DELETE FROM sets WHERE id = ?";
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, setId);
            pstmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ===== ИСПРАВЛЕННЫЙ МЕТОД getRecords =====
    public List<Record> getRecords(int userId) {
        List<Record> records = new ArrayList<>();

        String sql = """
            SELECT DISTINCT e.id as exercise_id, e.name as exercise_name, e.muscle_group
            FROM exercises e
            WHERE e.user_id = ? OR e.user_id = 0
            ORDER BY e.name
        """;

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int exerciseId = rs.getInt("exercise_id");
                String exerciseName = rs.getString("exercise_name");
                String muscleGroup = rs.getString("muscle_group");

                // Максимальный вес - теперь с user_id
                String maxWeightSql = """
                    SELECT weight, reps, workout_date 
                    FROM sets 
                    WHERE exercise_id = ? AND user_id = ?
                    ORDER BY weight DESC 
                    LIMIT 1
                """;

                try (PreparedStatement pstmt2 = connection.prepareStatement(maxWeightSql)) {
                    pstmt2.setInt(1, exerciseId);
                    pstmt2.setInt(2, userId);
                    ResultSet rs2 = pstmt2.executeQuery();

                    if (rs2.next()) {
                        double maxWeight = rs2.getDouble("weight");
                        int repsAtMaxWeight = rs2.getInt("reps");
                        String date = rs2.getString("workout_date");

                        records.add(new Record(
                                0,
                                exerciseId,
                                exerciseName + " (макс вес)",
                                muscleGroup,
                                maxWeight,
                                repsAtMaxWeight,
                                date,
                                null
                        ));
                    }
                }

                // Максимальные повторения - теперь с user_id
                String maxRepsSql = """
                    SELECT weight, reps, workout_date 
                    FROM sets 
                    WHERE exercise_id = ? AND user_id = ?
                    ORDER BY reps DESC 
                    LIMIT 1
                """;

                try (PreparedStatement pstmt3 = connection.prepareStatement(maxRepsSql)) {
                    pstmt3.setInt(1, exerciseId);
                    pstmt3.setInt(2, userId);
                    ResultSet rs3 = pstmt3.executeQuery();

                    if (rs3.next()) {
                        double weightAtMaxReps = rs3.getDouble("weight");
                        int maxReps = rs3.getInt("reps");
                        String date = rs3.getString("workout_date");

                        boolean isDuplicate = false;
                        for (Record r : records) {
                            if (r.getExerciseName().equals(exerciseName + " (макс вес)") &&
                                    r.getWeight() == weightAtMaxReps &&
                                    r.getReps() == maxReps) {
                                isDuplicate = true;
                                break;
                            }
                        }

                        if (!isDuplicate) {
                            records.add(new Record(
                                    0,
                                    exerciseId,
                                    exerciseName + " (макс повторения)",
                                    muscleGroup,
                                    weightAtMaxReps,
                                    maxReps,
                                    date,
                                    null
                            ));
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return records;
    }

    // ===== ИСПРАВЛЕННЫЙ МЕТОД getWorkoutHistory - ТОЛЬКО ПО user_id =====
    public List<Workout> getWorkoutHistory(int userId) {
        List<Workout> workouts = new ArrayList<>();
        String sql = """
            SELECT s.workout_date,
                   e.name as exercise_name,
                   s.weight,
                   s.reps,
                   e.muscle_group
            FROM sets s
            JOIN exercises e ON s.exercise_id = e.id
            WHERE s.user_id = ?
            ORDER BY s.workout_date DESC, e.name
        """;
        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                workouts.add(new Workout(
                        rs.getString("workout_date"),
                        rs.getString("exercise_name"),
                        rs.getDouble("weight"),
                        rs.getInt("reps"),
                        rs.getString("muscle_group")
                ));
            }
            System.out.println("Загружено тренировок в истории для user_id=" + userId + ": " + workouts.size());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return workouts;
    }

    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Соединение с БД закрыто");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}