import { FoodTag } from "./FoodTag";
import { MealTime } from "./MealTime";

export default interface Scrap {
    postId: number;
    title: string;
    representImage: string;
    foodTag: FoodTag;
    mealTime: MealTime;
    tags: string[];
}

export type ScrapDefault = Required<Scrap>;

export const getScrapDefault = (scrap?: Partial<Scrap>): ScrapDefault => ({
    postId: scrap?.postId ?? 0,
    title: scrap?.title ?? "제목 없음",
    representImage: scrap?.representImage ?? "",
    foodTag: scrap?.foodTag ?? FoodTag.NONE,
    mealTime: scrap?.mealTime ?? MealTime.NONE,
    tags: scrap?.tags ?? [],
});
