import { getMonthlyTrend, TrendTagMonthResponse } from "@/api/ranking";
import { ArrowBigUp } from "lucide-react";
import { useState, useEffect } from "react";
import { useNavigate } from "react-router-dom";

const RookieMenu = () => {
    const navigate = useNavigate();
    const [activeItem, setActiveItem] = useState(1);
    const [menuData, setMenuData] = useState<TrendTagMonthResponse[]>([]);

    useEffect(() => {
        const fetchData = async () => {
            try {
                const data = await getMonthlyTrend();
                setMenuData(Object.values(data));
            } catch (error) {
                console.error("데이터 로딩 실패:", error);
            }
        };

        fetchData();
        // console.log(typeof menuData);
    }, []);

    useEffect(() => {
        // menuData가 비어있으면 setInterval 실행하지 않음
        if (menuData.length === 0) return;

        const interval = setInterval(() => {
            // 이전 activeItem 값을 기반으로 새로운 activeItem을 계산
            setActiveItem((prev) => (prev % menuData.length) + 1);
        }, 3000);

        // 컴포넌트 언마운트 시 혹은 menuData가 변경될 때 clearInterval
        return () => clearInterval(interval);
    }, [menuData]);

    return (
        <div className="w-full pb-12">
            <div className="font-bold text-left p-2">이달의 루키 메뉴</div>
            <div className="w-full h-80 bg-slate-900 rounded-xl relative overflow-hidden">
                {/* 메뉴 아이템들 */}
                <div className="flex justify-center items-center h-full md:gap-20 gap-8 mx-2">
                    {menuData.map((item, index) => (
                        <div
                            key={index}
                            className="relative h-full flex items-center"
                        >
                            {/* 전체 열 스포트라이트 효과 */}
                            {activeItem === index + 1 && (
                                <div className="absolute inset-0 w-full pointer-events-none">
                                    {/* 상단 빛 효과 */}
                                    <div className="absolute top-0 left-1/2 -translate-x-1/2 w-32 h-full flex justify-center z-[9]">
                                        <div className="w-full h-full blur-sm bg-gradient-to-b from-yellow-100/30 via-yellow-100/10 to-transparent" />
                                    </div>
                                </div>
                            )}

                            {/* 메뉴 카드 */}
                            <div
                                className={`w-[6rem] bg-slate-800 p-2 rounded-lg transform transition-all duration-300 lg:text-base text-sm relative ${
                                    activeItem === index + 1
                                        ? "scale-110 shadow-xl shadow-yellow-100/10"
                                        : "scale-100"
                                }`}
                                onMouseOver={() => setActiveItem(index + 1)}
                                onClick={() =>
                                    navigate(
                                        `/food-detail?tagName=${item.tagName}`,
                                    )
                                }
                            >
                                <div
                                    className={`text-center mb-2 font-bold ${
                                        index === 0
                                            ? "text-yellow-400"
                                            : index === 1
                                              ? "text-gray-300"
                                              : "text-orange-400"
                                    }`}
                                >
                                    {index + 1}위
                                </div>
                                <div className="text-white font-bold lg:text-lg mb-2">
                                    {item.tagName}
                                </div>
                                <div className="text-white/80">
                                    <span className="flex justify-center ">
                                        <ArrowBigUp className="text-red-500" />{" "}
                                        {item.postCount}
                                    </span>
                                </div>
                                {/* <div
                                    className={`lg:text-sm text-xs ${
                                        item.trend > 0
                                            ? "text-blue-200"
                                            : "text-red-200"
                                    }`}
                                > */}
                                {/* {item.trend > 0 ? "+" : ""}
                                    {item.trend}% */}
                                {/* </div> */}
                            </div>
                        </div>
                    ))}
                </div>
            </div>
        </div>
    );
};

export default RookieMenu;
