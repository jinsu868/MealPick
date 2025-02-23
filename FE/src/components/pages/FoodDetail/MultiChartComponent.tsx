import { ResponsiveLine } from "@nivo/line";

interface ChartComponentProps {
    categories: {
        id: string;
        data: { x: string | number; y: number }[];
    }[];
    timePeriods: ("아침" | "점심" | "저녁")[];
    colors?: string[];
    showAxisBottom?: boolean;
    showAxisLeft?: boolean;
    margin?: {
        top: number;
        right: number;
        bottom: number;
        left: number;
    };
}

export default function MultiChartComponent({
    categories,
    timePeriods,
    colors = ["hsl(25, 70%, 50%)", "hsl(200, 70%, 50%)", "hsl(130, 70%, 50%)"],
    showAxisBottom = true,
    showAxisLeft = true,
    margin = { top: 20, right: 10, bottom: 50, left: 40 },
}: ChartComponentProps) {
    const filteredData = categories.map((category, index) => ({
        id: category.id,
        color: colors[index % colors.length],
        data: category.data.filter((d) => {
            const hour = Number(d.x);
            if (timePeriods.includes("아침") && hour >= 6 && hour < 10)
                return true;
            if (timePeriods.includes("점심") && hour >= 10 && hour < 16)
                return true;
            if (timePeriods.includes("저녁") && hour >= 16 && hour <= 18)
                return true;
            return false;
        }),
    }));

    return (
        <ResponsiveLine
            data={filteredData}
            margin={margin}
            xScale={{ type: "point" }}
            yScale={{ type: "linear", min: 0, stacked: false, nice: true }}
            axisBottom={
                showAxisBottom
                    ? {
                          tickSize: 5,
                          tickPadding: 5,
                          tickRotation: 0,
                          legend: "시간",
                          legendOffset: 36,
                          legendPosition: "middle",
                      }
                    : null
            }
            axisLeft={
                showAxisLeft
                    ? {
                          tickSize: 5,
                          tickPadding: 5,
                          tickRotation: 0,
                          legendOffset: -40,
                          legendPosition: "middle",
                      }
                    : null
            }
            colors={{ datum: "color" }}
            pointSize={5}
            pointColor={{ theme: "background" }}
            pointBorderWidth={2}
            pointBorderColor={{ from: "serieColor" }}
            enableArea={true}
            areaOpacity={0.3}
            useMesh={true}
            enableGridX={false}
            enableGridY={true}
            defs={[
                {
                    id: "gradientA",
                    type: "linearGradient",
                    colors: [
                        { offset: 0, color: "rgba(64, 58, 221, 0.8)" },
                        { offset: 100, color: "rgba(115, 204, 231, 0.1)" },
                    ],
                },
            ]}
            fill={filteredData.map((item, index) => ({
                match: { id: item.id },
                id: `gradient${String.fromCharCode(65 + index)}`,
            }))}
            curve="monotoneX"
            tooltip={({ point }) => (
                <div className="bg-white p-2 rounded-md shadow-lg border border-gray-300 text-sm text-gray-700">
                    <strong>{point.serieId}</strong>: {point.data.yFormatted}
                </div>
            )}
        />
    );
}
