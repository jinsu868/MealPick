import { ResponsiveLine } from "@nivo/line";
import { ScalePointSpec, ScaleLinearSpec } from "@nivo/scales";
import { useEffect } from "react";

interface ChartComponentProps {
    data: {
        id: string;
        color: string;
        data: { x: string | number; y: number }[];
    }[];
    period: "MONTH" | "QUARTER" | "YEAR";
    showAxiosBottom?: boolean;
    showAxiosLeft?: boolean;
    margin?: {
        top: number;
        right: number;
        bottom: number;
        left: number;
    };
}

export default function ChartComponent({
    data,
    period,
    showAxiosLeft = true,
    margin = { top: 20, right: 10, bottom: 50, left: 40 },
}: ChartComponentProps) {
    const xScale: ScalePointSpec | ScaleLinearSpec =
        period === "MONTH"
            ? { type: "point" }
            : period === "QUARTER"
              ? { type: "point" }
              : { type: "point" };

    useEffect(() => {
        console.log("Chart Data:", data);
    }, [data]);

    return (
        <ResponsiveLine
            data={data}
            margin={{
                top: margin.top,
                right: margin.right,
                bottom: margin.bottom,
                left: margin.left,
            }}
            xScale={xScale}
            yScale={{ type: "linear", min: 0, stacked: false, nice: true }}
            axisBottom={null}
            axisLeft={
                showAxiosLeft
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
            fill={data.map((item, index) => ({
                match: { id: item.id },
                id: `gradient${String.fromCharCode(65 + index)}`,
            }))}
            curve="monotoneX"
            tooltip={({ point }) => (
                <div className="bg-white p-2 rounded-md shadow-lg border border-gray-300 text-sm text-gray-700">
                    <div>{String(point.data.xFormatted || point.data.x)} </div>
                    <strong>{point.serieId}</strong>: {point.data.yFormatted}
                </div>
            )}
        />
    );
}
