import { useRef, useState } from "react";
import { motion } from "framer-motion";
import { useNavigate } from "react-router-dom";

const YearlyTrend = () => {
    const navigate = useNavigate();
    const [isFlipped, setIsFlipped] = useState(false);
    const scrollRef = useRef(null);

    // 트렌드 이미지 리스트
    const trendingItems = [
        { id: 1, image: "/imgs/tiramisu.jpg", name: "밤 티라미수" },
        { id: 2, image: "/imgs/yoajeong.jpg", name: "요아정" },
        { id: 3, image: "/imgs/dubai_choco.jpg", name: "두바이 초콜릿" },
        { id: 4, image: "/imgs/crooky.jpg", name: "크루키" },
        { id: 5, image: "/imgs/amanchu.jpg", name: "아망추" },
        { id: 6, image: "/imgs/mangosiru.jpg", name: "망고시루" },
    ];

    return (
        <div className="relative w-full flex flex-col items-center my-10">
            <div className="flex justify-between w-full px-2">
                <div className="font-bold">유행 음식</div>
                <span className="text-gray-400 text-xs self-end">
                    삼성 welstory 2024 F&B 트렌드 요약
                </span>
            </div>

            {/* 🟢 첫 장 (소개) */}
            <motion.div
                className="relative w-full max-w-[600px] bg-yellow-300 text-black rounded-2xl flex flex-col items-center justify-center p-6"
                animate={{ rotateY: isFlipped ? 180 : 0 }}
                transition={{ duration: 0.6 }}
                style={{ transformStyle: "preserve-3d" }}
            >
                {/* 첫 장 (Front) */}
                <motion.div
                    className="w-full max-w-[600px] flex flex-col justify-center items-center mb-6 mt-8"
                    style={{ backfaceVisibility: "hidden" }}
                >
                    <p className="text-lg font-bold">@ 몇 개 먹어봤어?</p>
                    <h1 className="text-2xl font-extrabold mt-2">
                        2024 하반기 <br /> 유행 음식 <br /> 총 정리!
                    </h1>
                    <motion.button
                        className="mt-6 px-6 py-3 bg-black text-white rounded-full text-sm"
                        whileTap={{ scale: 0.95 }}
                        onClick={() => setIsFlipped(true)}
                    >
                        트렌드 보기 →
                    </motion.button>
                </motion.div>

                {/* 🟢 뒤집힌 후 스크롤 UI 포함 */}
                <motion.div
                    className="absolute inset-0 w-full h-full bg-gray-900 text-white rounded-2xl overflow-hidden"
                    style={{
                        transform: "rotateY(180deg)",
                        backfaceVisibility: "hidden",
                    }}
                >
                    <div
                        ref={scrollRef}
                        className="w-full h-full flex overflow-x-auto snap-x snap-mandatory scrollbar-hide"
                        style={{ scrollBehavior: "smooth" }}
                    >
                        {/* 뒤집힌 첫 장 (요약) */}
                        <div className="flex-none w-full max-w-[600px] snap-start relative">
                            {/* 🖼️ 흐릿한 배경으로 트렌드 요약 */}
                            <div className="absolute inset-0 grid grid-cols-3 grid-rows-3 gap-1 opacity-30 blur-sm">
                                {trendingItems.map((item, index) => (
                                    <div key={index} className="w-full h-full">
                                        <img
                                            src={item.image}
                                            alt={item.name}
                                            className="w-full h-full object-cover rounded-lg"
                                        />
                                    </div>
                                ))}
                            </div>

                            {/* 🌟 메인 텍스트 및 돌아가기 버튼 */}
                            <div className="absolute inset-0 flex flex-col items-center justify-center">
                                <h3 className="text-2xl font-bold mb-2">
                                    트렌드 요약
                                </h3>
                                <p className="text-base mb-4">
                                    최근 유행했던 대표적인 음식들을 모아봤어요!
                                </p>
                                <motion.button
                                    className="px-6 py-3 bg-white/20 backdrop-blur-md text-white rounded-full text-lg"
                                    whileTap={{ scale: 0.95 }}
                                    onClick={() => setIsFlipped(false)}
                                >
                                    돌아가기
                                </motion.button>
                            </div>
                        </div>

                        {/* 📌 음식 사진 스크롤 */}
                        {trendingItems.map((item) => (
                            <div
                                key={item.id}
                                className="flex-none w-full max-w-[600px] snap-start relative"
                            >
                                <img
                                    src={item.image}
                                    alt={item.name}
                                    className="w-full h-full object-cover rounded-2xl"
                                />
                                <div className="absolute bottom-4 left-4 bg-black/60 px-3 py-2 rounded-lg">
                                    <span className="text-white text-sm font-bold">
                                        {item.name}
                                    </span>
                                </div>
                            </div>
                        ))}

                        {/* 🟣 마지막 페이지 (참여 유도) */}
                        <div className="flex-none w-full max-w-[600px] snap-start flex flex-col items-center justify-center bg-gray-800">
                            <h2 className="text-xl font-bold mb-4 text-center">
                                얼마나 먹었는 지 공유해봐
                            </h2>
                            <motion.button
                                className="px-6 py-3 bg-yellow-300 text-black font-bold rounded-full flex items-center"
                                whileTap={{ scale: 0.95 }}
                                onClick={() => navigate("/post")}
                            >
                                <span>참여하기</span>
                                <span className="ml-2 text-lg">→</span>
                            </motion.button>
                        </div>
                    </div>
                </motion.div>
            </motion.div>
        </div>
    );
};

export default YearlyTrend;
