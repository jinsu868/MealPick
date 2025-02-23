import { useEffect, useState } from "react";
import ChartComponent from "./ChartComponent";
import { getMontlyPostCountInTag } from "@/api/tag";
import {
    DropdownMenu,
    DropdownMenuContent,
    DropdownMenuRadioGroup,
    DropdownMenuRadioItem,
    DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";
import { Button } from "@/components/ui/button";

export default function FoodChart() {
    const params = new URLSearchParams(location.search);
    const tagName = params.get("tagName") || "태그";
    const [selectedPeriod, setSelectedPeriod] = useState<
        "MONTH" | "QUARTER" | "YEAR"
    >("MONTH");
    const [chartData, setChartData] = useState<
        { id: string; color: string; data: { x: string; y: number }[] }[]
    >([]);

    const transformData = (
        data: { daysAgo: number; tagId: number; count: number }[],
        period: "MONTH" | "QUARTER" | "YEAR",
    ) => {
        if (!Array.isArray(data)) {
            console.error("Invalid data format:", data);
            return [];
        }

        let transformedData: { x: string; y: number }[] = [];
        const today = new Date();

        if (period === "MONTH") {
            transformedData = data
                .map((item) => {
                    const date = new Date();
                    date.setDate(today.getDate() - item.daysAgo);
                    return {
                        x: `${date.getMonth() + 1}/${date.getDate()}`,
                        y: item.count,
                    };
                })
                .sort(
                    (a, b) => new Date(a.x).getTime() - new Date(b.x).getTime(),
                );
        } else if (period === "QUARTER") {
            const groupedByWeek: { [week: number]: number } = {};

            data.sort((a, b) => a.daysAgo - b.daysAgo).forEach((item) => {
                const week = Math.floor(item.daysAgo / 7);
                if (!groupedByWeek[week]) groupedByWeek[week] = 0;
                groupedByWeek[week] += item.count;
            });

            const last12Weeks = Object.entries(groupedByWeek)
                .sort((a, b) => parseInt(b[0]) - parseInt(a[0]))
                .slice(-12)
                .map(([week, count]) => ({
                    x: `${week}주 전`,
                    y: count,
                }));

            transformedData = last12Weeks;
        } else if (period === "YEAR") {
            const groupedByMonth: { [month: string]: number } = {};

            data.sort((a, b) => a.daysAgo - b.daysAgo).forEach((item) => {
                const date = new Date();
                date.setDate(today.getDate() - item.daysAgo);
                const key = `${date.getFullYear()}-${date.getMonth() + 1}`; // "YYYY-MM"

                if (!groupedByMonth[key]) groupedByMonth[key] = 0;
                groupedByMonth[key] += item.count;
            });

            const last12Months = Object.entries(groupedByMonth)
                .map(([month, count]) => ({
                    x: month.split("-")[1] + "월", // "3월", "4월", ...
                    y: count,
                }))
                .reverse()
                .slice(-12);

            transformedData = last12Months;
        }

        console.log("Transformed Data:", transformedData);
        return transformedData;
    };

    const fetchChartData = async () => {
        if (!tagName) return;

        try {
            const rawData = await getMontlyPostCountInTag(
                tagName,
                selectedPeriod,
            );
            console.log("Raw API Data:", rawData);

            const transformedData = transformData(rawData, selectedPeriod);
            console.log("Transformed Data:", transformedData);

            if (transformedData.length === 0) {
                console.warn("No data available for the selected period.");
                setChartData([]);
                return;
            }

            setChartData([
                {
                    id: "태그 데이터",
                    color: "hsl(228,74%,61%)",
                    data: transformedData,
                },
            ]);
        } catch (error) {
            console.error("차트 데이터 조회 실패:", error);
        }
    };

    useEffect(() => {
        fetchChartData();
    }, [tagName, selectedPeriod]);

    return (
        <div className="w-full text-gray-900">
            <div className="p-4 pt-6">
                {/* Header */}
                <div className="flex flex-col items-start">
                    <div className="flex items-center">
                        <div className="text-xl font-semibold dark:text-slate-300">
                            {tagName}
                        </div>
                    </div>
                </div>

                {/* Chart */}
                <div className="flex flex-col pt-4">
                    <div className="flex justify-start w-full pb-2 border-b-2 items-center">
                        <div className="font-semibold dark:text-slate-500">
                            차트
                        </div>
                        <DropdownMenu>
                            <DropdownMenuTrigger asChild>
                                <Button
                                    variant="outline"
                                    className="ml-2 w-fit px-4 h-fit py-0.5 bg-blue-500 text-white rounded-full text-xs flex justify-center items-center"
                                >
                                    {selectedPeriod}
                                </Button>
                            </DropdownMenuTrigger>
                            <DropdownMenuContent className="w-fit">
                                <DropdownMenuRadioGroup
                                    value={selectedPeriod}
                                    onValueChange={(value) =>
                                        setSelectedPeriod(
                                            value as
                                                | "MONTH"
                                                | "QUARTER"
                                                | "YEAR",
                                        )
                                    }
                                >
                                    <DropdownMenuRadioItem value="MONTH">
                                        월별
                                    </DropdownMenuRadioItem>
                                    <DropdownMenuRadioItem value="QUARTER">
                                        분기별
                                    </DropdownMenuRadioItem>
                                    <DropdownMenuRadioItem value="YEAR">
                                        연간
                                    </DropdownMenuRadioItem>
                                </DropdownMenuRadioGroup>
                            </DropdownMenuContent>
                        </DropdownMenu>
                    </div>
                    <div className="flex w-full">
                        <div className="mt-2 w-full h-60 pl-2 pr-8">
                            <ChartComponent
                                data={chartData}
                                period={selectedPeriod}
                                showAxiosBottom={true}
                                showAxiosLeft={true}
                                margin={{
                                    top: 20,
                                    right: 40,
                                    bottom: 50,
                                    left: 40,
                                }}
                            />
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
}
