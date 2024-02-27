package com.tish.db.bases;

public enum Season {
    WINTER("Зима", new String[]{"01", "02", "12"}),
    SPRING("Весна", new String[]{"03", "04", "05"}),
    SUMMER("Літо", new String[]{"06", "07", "08"}),
    AUTUMN("Осінь", new String[]{"09", "10", "11"});

    String name;
    String[] numbers;

    Season(String name, String[] numbers) {
        this.name = name;
        this.numbers = numbers;
    }

    public String getName() {
        return name;
    }

    public String[] getNumbers() {
        return numbers;
    }

    public String getNumberOfMonth(int index) {
        return numbers[index];
    }
}
