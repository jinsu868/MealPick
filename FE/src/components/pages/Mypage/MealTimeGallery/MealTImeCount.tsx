import { useEffect, useState } from "react";
import {
    DinnerTagBtn,
    LunchTagBtn,
    MorningTagBtn,
} from "@/components/commons/MealTagBtn";
import { getMyData, getOtherData, MyData } from "@/api/post";
import { MealTime } from "@/types/MealTime";

interface GallerySectionProps {
    id?: number;
    isMyPage: boolean;
}

export default function MealTimeCount({ id, isMyPage }: GallerySectionProps) {
    const [mealData, setMealData] = useState([
        {
            label: MealTime.MORNING,
            value: 0,
            color: "bg-cyan-500",
            Tag: MorningTagBtn,
        },
        {
            label: MealTime.LUNCH,
            value: 0,
            color: "bg-orange-400",
            Tag: LunchTagBtn,
        },
        {
            label: MealTime.DINNER,
            value: 0,
            color: "bg-indigo-500",
            Tag: DinnerTagBtn,
        },
    ]);

    const [myData, setMyData] = useState<MyData[]>([]);
    const fetchData = async () => {
        console.log("데이터 요청 중...", isMyPage);
        try {
            if (isMyPage) {
                const response = await getMyData();
                setMyData(response);
            } else {
                const response = await getOtherData(id ?? -1);
                setMyData(response);
            }
        } catch (error) {
            console.error("데이터 요청 중 오류 발생:", error);
        }
    };

    useEffect(() => {
        fetchData();
    }, []);

    useEffect(() => {
        fetchData();
    }, [id, isMyPage]);

    useEffect(() => {
        const updatedMealData = mealData.map((meal) => {
            const matchedData = myData.find(
                (data) =>
                    MealTime[data.mealTime as keyof typeof MealTime] ===
                    meal.label,
            );
            console.log("매칭된 데이터:", matchedData);
            return matchedData ? { ...meal, value: matchedData.count } : meal;
        });
        console.log("업데이트된 데이터:", updatedMealData);

        setMealData(updatedMealData);
    }, [myData]);

    const maxValue = Math.max(...mealData.map((meal) => meal.value));

    return (
        <div className="mt-2 w-full max-w-[600px]">
            <div className="font-semibold text-lg flex justify-start">
                나의 아점저 Pic
            </div>
            <div className="mt-2 bg-gray-50 dark:bg-slate-600 w-full rounded-xl flex justify-start flex-col p-4 pr-8 gap-3">
                {mealData.map(({ value, color, Tag }, index) => {
                    const widthPercentage = maxValue
                        ? (value / maxValue) * 100
                        : 0;
                    return (
                        <div
                            key={index}
                            className="flex-1 flex items-start gap-2"
                        >
                            <div className="w-fit">
                                <Tag />
                            </div>
                            <div className="flex-1">
                                <div
                                    className={`h-4 ${color} rounded-full relative flex items-center`}
                                    style={{ width: `${widthPercentage}%` }}
                                >
                                    <div className="absolute right-[-24px] font-medium text-gray-600 dark:text-gray-300">
                                        {value}
                                    </div>
                                </div>
                            </div>
                        </div>
                    );
                })}
            </div>
        </div>
    );
}
