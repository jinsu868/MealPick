import { MealTime } from "@/types/MealTime";

export function getMealTimeColor(mealTime: MealTime): string {
    console.log("mealTime", mealTime);
    const mealTimeClassMap: Record<MealTime, string> = {
        [MealTime.MORNING]: "bg-cyan-300",
        [MealTime.LUNCH]: "bg-orange-400",
        [MealTime.DINNER]: "bg-indigo-400",
        [MealTime.NONE]: "bg-gray-200",
    };

    return mealTimeClassMap[mealTime] ?? "bg-gray-200";
}
