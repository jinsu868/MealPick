export enum MealTime {
    MORNING = "아침",
    LUNCH = "점심",
    DINNER = "저녁",
    NONE = "알 수 없음",
}

const MealTimeEnglishMap: Record<MealTime, string> = {
    [MealTime.MORNING]: "MORNING",
    [MealTime.LUNCH]: "LUNCH",
    [MealTime.DINNER]: "DINNER",
    [MealTime.NONE]: "UNKNOWN",
};

export function getMealTime(): string {
    const currentHour = new Date().getHours();

    let mealTime: MealTime;
    if (currentHour >= 4 && currentHour < 10) {
        mealTime = MealTime.MORNING;
    } else if (currentHour >= 10 && currentHour < 16) {
        mealTime = MealTime.LUNCH;
    } else {
        mealTime = MealTime.DINNER;
    }

    return MealTimeEnglishMap[mealTime];
}
