import { useState, useEffect } from "react";
import { motion } from "framer-motion";
import { LunchTagBtn } from "@/components/commons/MealTagBtn";
import { Button } from "@/components/ui/button";

const TEXT_ITEMS = [
    "떡볶이",
    "김밥",
    "순대",
    "오뎅",
    "튀김",
    "라면",
    "스파게티",
    "피자",
];

export default function Bubble() {
    const [positions, setPositions] = useState(
        TEXT_ITEMS.map(() => ({
            x: 0,
            y: 0,
            time: Math.random() * 100,
            speed: Math.random() * 0.02 + 0.005,
        })),
    );

    useEffect(() => {
        const updatePositions = () => {
            setPositions((prevPositions) =>
                prevPositions.map((pos) => {
                    const maxX = 150;
                    const maxY = 75;

                    return {
                        x: Math.cos(pos.time) * maxX,
                        y: Math.sin(pos.time * 1.5) * maxY,
                        time: pos.time + pos.speed,
                        speed: pos.speed,
                    };
                }),
            );
        };

        const interval = setInterval(updatePositions, 100);

        return () => clearInterval(interval);
    }, []);

    return (
        <div className="w-full max-w-[600px] flex flex-col justify-center items-center my-6">
            <div className="w-full flex justify-start pl-2 font-bold gap-x-2 items-center">
                오늘의{" "}
                <span className="text-orange-400 font-extrabold">점심</span>{" "}
                뽑기
            </div>
            <div className="mt-2 shadow-[inset_0_0px_40px_rgba(210,210,225,0.2)] relative w-full h-[300px] rounded-2xl overflow-hidden flex justify-center items-center">
                {positions.map((pos, index) => (
                    <motion.div
                        key={index}
                        className="shadow-[inset_0_0px_5px_rgba(100,100,200,0.1)] aspect-square absolute text-sm font-bold text-blue-600 bg-[rgba(200,225,250,0.1)] p-3 rounded-full flex justify-center items-center backdrop-blur-sm hover:shadow-[inset_0_0px_12px_rgba(225,240,250,1)]"
                        animate={{
                            x: pos.x,
                            y: pos.y,
                        }}
                        transition={{
                            duration: 0.5,
                            ease: "easeInOut",
                        }}
                    >
                        {TEXT_ITEMS[index]}
                    </motion.div>
                ))}
            </div>
            {/* <div className="mt-2">
                <Button className="bg-blue-500 rounded-full text-lg">
                    뽑기!
                </Button>
            </div> */}
        </div>
    );
}
