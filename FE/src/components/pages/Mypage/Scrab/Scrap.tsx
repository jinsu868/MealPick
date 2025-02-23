import { ScrapDefault } from "@/types/Scrap";
import { PostDetailDefault } from "@/types/PostDetail";
import { FoodTag } from "@/types/FoodTag";
import { MealTime } from "@/types/MealTime";
import { getMealTimeColor } from "@/types/MealTimeColor";
import { Skeleton } from "@/components/ui/skeleton";
import { Checkbox } from "@/components/ui/checkbox";
import Tag from "@/components/commons/Tag";

interface ScrapProps {
    scrap: ScrapDefault;
    postDetail?: PostDetailDefault;
    isLoading: boolean;
    isSelected: boolean;
    onSelect: (postId: number) => void;
    onToggleSelection: (postId: number) => void;
}

export default function Scrap({
    scrap,
    postDetail,
    isLoading,
    isSelected,
    onSelect,
    onToggleSelection,
}: ScrapProps) {
    return isLoading ? (
        <Skeleton className="w-full h-36 rounded-lg mb-4 flex p-4 items-center">
            <Skeleton className="w-28 h-28 rounded-md" />
        </Skeleton>
    ) : (
        <div
            className="relative flex flex-col p-4 mb-4 cursor-pointer rounded-lg bg-white dark:bg-gray-700"
            onClick={() => onSelect(scrap.postId)}
        >
            {/* 체크박스 (선택용) */}
            <div
                className="absolute top-2 right-2"
                onClick={(e) => e.stopPropagation()}
            >
                <Checkbox
                    checked={isSelected}
                    onCheckedChange={() => onToggleSelection(scrap.postId)}
                />
            </div>

            {/* 제목 및 썸네일 */}
            <div className="flex">
                <img
                    src={scrap.representImage}
                    alt={scrap.title}
                    className="w-28 h-28 object-cover rounded-md"
                    onError={(e) => {
                        (e.target as HTMLImageElement).src =
                            "https://via.placeholder.com/150";
                    }}
                />

                <div className="ml-4 flex flex-col justify-center items-start">
                    <h3 className="mt-2 text-lg font-bold">{scrap.title}</h3>

                    {/* 날짜, 식사시간, 태그 */}
                    <div className="flex gap-2 items-center text-sm text-gray-600 dark:text-gray-400">
                        <div>
                            {postDetail?.createdAt?.split("T")[0] ||
                                "날짜 없음"}
                        </div>
                        <div>
                            {
                                MealTime[
                                    scrap.mealTime as unknown as keyof typeof MealTime
                                ]
                            }
                        </div>
                        <div>
                            {
                                FoodTag[
                                    scrap.foodTag as unknown as keyof typeof FoodTag
                                ]
                            }
                        </div>
                    </div>

                    <div className="mt-2 flex flex-wrap gap-1">
                        {scrap.tags?.map((tag, idx) => (
                            // 임시 태그 아이디
                            <Tag
                                key={idx}
                                tagName={tag}
                                colorClass={getMealTimeColor(
                                    MealTime[
                                        scrap.mealTime as unknown as keyof typeof MealTime
                                    ],
                                )}
                            />
                        ))}
                    </div>
                </div>
            </div>
        </div>
    );
}
