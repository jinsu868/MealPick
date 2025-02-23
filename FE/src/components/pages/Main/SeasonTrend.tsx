import { useRef, useState } from "react";
import { motion } from "framer-motion";
import { useNavigate } from "react-router-dom";

const SeasonTrend = () => {
    const navigate = useNavigate();
    const [isFlipped, setIsFlipped] = useState(false);
    const scrollRef = useRef(null);

    const currentMonth = Number(
        new Date().toLocaleDateString([], { month: "numeric" }).slice(0, -1),
    );

    // const currentSeason = "summer";
    const currentSeason =
        currentMonth < 3
            ? "winter"
            : currentMonth < 6
              ? "spring"
              : currentMonth < 9
                ? "summer"
                : currentMonth < 12
                  ? "autumn"
                  : "winter";

    const seasonalData = {
        spring: {
            name: "봄",
            color: "bg-pink-200",
            emoji: "🌸",
        },
        summer: {
            name: "여름",
            color: "summer-wave",
            emoji: "🌊",
        },
        autumn: {
            name: "가을",
            color: "bg-orange-400",
            emoji: "🍁",
        },
        winter: {
            name: "겨울",
            color: "bg-sky-300",
            emoji: "⛄",
        },
    };

    // 시즌 베스트 음식
    const topFood = "쌀국수";

    const feedItems = [
        {
            id: 1,
            likes: 234,
            comments: 42,
            image: "/imgs/ssal1.jpg",
            title: "쌀국수",
            content: "",
        },
    ];

    const SeasonalBackground = ({ season }: { season: string }) => {
        const elements = Array(12).fill(null);

        return (
            <div className="absolute inset-0 overflow-hidden pointer-events-none">
                {elements.map((_, index) => (
                    <motion.div
                        key={index}
                        className="absolute"
                        initial={{
                            top: -20,
                            left: `${Math.random() * 100}%`,
                            opacity: 0,
                        }}
                        animate={{
                            top: "120%",
                            opacity: [0, 0.4, 0.4, 0],
                            x: [
                                0,
                                Math.sin(index) * 100,
                                Math.sin(index) * -100,
                                0,
                            ],
                        }}
                        transition={{
                            duration: 5 + Math.random() * 5,
                            repeat: Infinity,
                            delay: Math.random() * 5,
                        }}
                    >
                        <span className="text-2xl">
                            {season === "spring" && "🌸"}
                            {season === "autumn" && "🍁"}
                            {season === "winter" && "❄️"}
                        </span>
                    </motion.div>
                ))}
            </div>
        );
    };

    return (
        <div className="w-full h-full">
            <div className="flex justify-between w-full px-2">
                <div className="font-bold">계절 트렌드</div>
                <span className="text-gray-400 text-xs self-end">
                    삼성 welstory 소개 기반 겨울하면 생각나는 요리
                </span>
            </div>
            <div className="w-full h-full">
                <motion.div
                    className={`w-full h-full rounded-2xl ${seasonalData[currentSeason].color} relative overflow-hidden`}
                    style={{ perspective: 1000 }}
                >
                    <SeasonalBackground season={currentSeason} />

                    <motion.div
                        className="absolute inset-0"
                        animate={{ rotateY: isFlipped ? 180 : 0 }}
                        transition={{ duration: 0.6 }}
                        style={{ transformStyle: "preserve-3d" }}
                    >
                        {/* Front side */}
                        <motion.div
                            className="absolute inset-0 p-6 flex flex-col items-center justify-center text-white"
                            style={{ backfaceVisibility: "hidden" }}
                        >
                            <span className="text-4xl mb-4">
                                {seasonalData[currentSeason].emoji}
                            </span>
                            <p className="text-center text-lg font-medium">
                                올 {seasonalData[currentSeason].name},
                                {/* {topFood}{" "} */} 이 음식은 어떠세요?
                            </p>
                            <motion.button
                                className="mt-4 px-4 py-2 bg-white/20 rounded-full text-sm"
                                whileTap={{ scale: 0.95 }}
                                onClick={() => setIsFlipped(true)}
                            >
                                자세히 보기
                            </motion.button>
                        </motion.div>

                        {/* Back side - Horizontal Scroll */}
                        <motion.div
                            className="absolute inset-0"
                            style={{
                                backfaceVisibility: "hidden",
                                transform: "rotateY(180deg)",
                            }}
                        >
                            <div
                                ref={scrollRef}
                                className="w-full h-full overflow-x-auto flex snap-x snap-mandatory scrollbar-hide"
                                style={{ scrollBehavior: "smooth" }}
                            >
                                {/* Introduction slide */}
                                <div className="flex-none w-full h-full snap-start relative">
                                    <img
                                        src="/imgs/ssal2.jpg"
                                        alt="계절 음식"
                                        className="w-full h-full object-cover"
                                    />
                                    <div className="absolute inset-0 bg-black/40 flex flex-col items-center justify-center text-white">
                                        <h3 className="text-2xl font-bold mb-2">
                                            {topFood}
                                        </h3>
                                        <p className="text-base mb-4">
                                            이번{" "}
                                            {seasonalData[currentSeason].name}{" "}
                                            인기 메뉴
                                        </p>

                                        <motion.button
                                            className="px-4 py-2 bg-white/20 rounded-full text-sm"
                                            whileTap={{ scale: 0.95 }}
                                            onClick={() => setIsFlipped(false)}
                                        >
                                            돌아가기
                                        </motion.button>
                                    </div>
                                </div>

                                {/* Pic 미리보기 */}
                                {feedItems.map((item, index) => (
                                    <div
                                        key={item.id}
                                        className="flex-none w-full h-full snap-start relative"
                                    >
                                        <img
                                            src={item.image}
                                            alt={`대표 피드 ${index + 1}`}
                                            className="w-full h-full object-cover"
                                        />
                                    </div>
                                ))}
                                <div className="flex-none w-full h-full snap-start relative">
                                    <img
                                        src={
                                            feedItems.filter(
                                                (feed) =>
                                                    feed.id ===
                                                    feedItems.length,
                                            )[0].image
                                        }
                                        alt="계절 음식"
                                        className="w-full h-full object-cover"
                                    />
                                    <div className="absolute inset-0 bg-black/40 flex flex-col items-center justify-center text-white">
                                        <h3 className="text-2xl font-bold mb-2">
                                            {topFood} Pic 구경하기
                                        </h3>

                                        <motion.button
                                            className="px-4 py-2 bg-white/20 rounded-full text-lg"
                                            whileTap={{ scale: 0.95 }}
                                            onClick={() =>
                                                navigate(
                                                    `/food-detail?tagName=${topFood}`,
                                                )
                                            }
                                        >
                                            →
                                        </motion.button>
                                    </div>
                                </div>
                            </div>
                        </motion.div>
                    </motion.div>
                </motion.div>
            </div>
        </div>
    );
};

export default SeasonTrend;
