import { useEffect, useState, useRef } from "react";
import { ResponsiveBar } from "@nivo/bar";
import { ChartBar } from "lucide-react";
import { getRanking } from "@/api/ranking";
import { useNavigate } from "react-router-dom";
import { Button } from "@/components/ui/button";
import { useWebSocket } from "@/contexts/WebSocketContext";

// 데이터 인터페이스 정의
interface Data {
    tag: string;
    count: number;
}

// 추천 색상 팔레트
const colorPalette = [
    "#DAE4FF",
    "#C8D9FF",
    "#C6D7FF",
    "#B7CCFF",
    "#ACC5FF",
    "#9EBBFF",
    "#87ABFD",
    "#709BFF",
    "#5A8BFF",
    "#5084FF",
];

const Ranking = () => {
    const navigate = useNavigate();
    const { isConnected, subscribe, unsubscribe } = useWebSocket();

    // 예시 초기 데이터 (실제 환경에서는 동적으로 업데이트될 수 있음)
    const initialData: Data[] = [
        {
            tag: "",
            count: 0,
        },
    ];

    const [data, setData] = useState<Data[]>(initialData);

    // 음식 이름별 색상을 저장하는 객체 (동적 매핑)
    const colorMapping = useRef<{ [key: string]: string }>({});

    // 음식이 새로 등장하면 추천 색상 팔레트에서 순서대로 할당하고,
    // 이미 매핑된 음식은 그대로 사용
    const getColor = (tag: string) => {
        if (!colorMapping.current[tag]) {
            const keys = Object.keys(colorMapping.current);
            // 팔레트에 있는 색상을 순환하며 할당 (팔레트보다 항목 수가 많으면 재사용)
            colorMapping.current[tag] =
                colorPalette[keys.length % colorPalette.length];
        }
        return colorMapping.current[tag];
    };

    const handleRanking = async () => {
        try {
            const ranking = await getRanking();
            return ranking;
        } catch (error) {
            console.error("랭킹에러: ", error);
            return initialData;
        }
    };

    useEffect(() => {
        const updateData = async () => {
            const newDataObj = await handleRanking();
            const newDataArr = Object.values(newDataObj)
                .filter((elem: any) => elem.count)
                .reverse();
            if (newDataArr && newDataArr.length > 0) {
                setData(newDataArr);
            }
        };

        // 최초 한 번 데이터 업데이트
        updateData();

        // 구독 설정
        const topicPath = "/topic/rankings/real-time";

        // 메시지 핸들러 함수
        const handleMessage = (rankingData: any) => {
            console.log("Received ranking data:", rankingData);
            const newDataArr = Object.values(rankingData)
                .filter((elem: any) => elem.count)
                .reverse();

            if (newDataArr && newDataArr.length > 0) {
                setData(newDataArr);
            }
        };

        // 구독
        subscribe(topicPath, handleMessage);

        // 클린업 함수
        return () => {
            // 구독 해제
            unsubscribe(topicPath);
        };
    }, [isConnected, subscribe, unsubscribe]);

    return (
        <div className="flex flex-col h-[400px]">
            <div className="Header flex items-center p-2 text-left font-bold gap-2">
                <div className="font-bold">실시간 TOP 10</div>
                <ChartBar className="w-4 text-gray-500" />{" "}
            </div>

            {data.some((item) => item.count > 0) ? (
                <ResponsiveBar
                    data={data}
                    theme={{
                        background: "transparent",
                        text: {
                            fontSize: "lg",
                            //fontWeight: "bold",
                            fill: "black",
                            outlineWidth: 3,
                            outlineColor: "transparent",
                        },
                    }}
                    keys={["count"]} // count 값을 표시
                    indexBy="tag" // 항목 구분 기준은 tag
                    margin={{
                        top: 0,
                        right: 40,
                        bottom: 0,
                        left: 100,
                    }}
                    onClick={(bar) => {
                        navigate(`/food-detail?tagName=${bar.data.tag}`);
                    }}
                    padding={0.1}
                    layout="horizontal"
                    valueScale={{
                        type: "linear",
                        min: Math.min(...data.map((v) => v.count)) * 0.8,
                        clamp: true,
                    }}
                    indexScale={{ type: "band", round: true }}
                    colors={(bar) => getColor(bar.data.tag)}
                    borderColor={{
                        from: "color",
                        modifiers: [["darker", 1.6]],
                    }}
                    borderRadius={4}
                    axisTop={null}
                    axisRight={null}
                    axisBottom={null}
                    axisLeft={{
                        tickSize: 0,
                        tickPadding: 5,
                        tickRotation: 0,
                    }}
                    enableGridY={false}
                    animate={true}
                    motionStiffness={90}
                    motionDamping={15}
                    height={data.length * 35}
                />
            ) : (
                <div className="flex flex-col justify-center items-center h-full text-lg">
                    <h2 className="text-xl mb-4">
                        오늘은 아직 등록된 Pic이 없어요!
                    </h2>
                    <Button
                        className="bg-blue-500 hover:bg-blue-500"
                        onClick={() => navigate("/post")}
                    >
                        Pic 업로드하기
                    </Button>
                </div>
            )}
        </div>
    );
};

export default Ranking;
