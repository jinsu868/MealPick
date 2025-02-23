import { useState } from "react";
import { ChevronDown, ChevronUp } from "lucide-react";

const categoryIcons: Record<string, { icon: string; label: string }> = {
    ALL: { icon: "🍽️", label: "전체" },
    KOREAN: { icon: "🍱", label: "한식" },
    WESTERN: { icon: "🍔", label: "양식" },
    JAPANESE: { icon: "🍣", label: "일식" },
    CHINESE: { icon: "🥡", label: "중식" },
    BUNSIK: { icon: "🍢", label: "분식" },
    ASIAN: { icon: "🥘", label: "아시안" },
    FASTFOOD: { icon: "🍟", label: "패스트푸드" },
    CHICKEN: { icon: "🍗", label: "치킨" },
    PIZZA: { icon: "🍕", label: "피자" },
    DESSERT: { icon: "🍰", label: "디저트" },
};

export default function Category() {
    const [selectedCategory, setSelectedCategory] = useState<string | null>(
        null,
    );
    const [showAllCategories, setShowAllCategories] = useState(false);
    const categories = Object.keys(categoryIcons);

    let displayedCategories = categories.slice(0, 5);
    if (showAllCategories) {
        displayedCategories = categories;
    }

    return (
        <div className="pb-1 border-b border-gray-300 dark:border-gray-700 mb-2 px-2">
            <div className="grid grid-cols-6 gap-2 items-center justify-center text-xs">
                {displayedCategories.map((category) => (
                    <div
                        key={category}
                        className={`flex flex-col items-center cursor-pointer p-2 w-full h-24 ${
                            selectedCategory === category
                                ? "font-bold"
                                : "text-gray-700 dark:text-gray-300"
                        }`}
                        onClick={() => setSelectedCategory(category)}
                    >
                        <div
                            className={`w-14 h-14 flex items-center justify-center bg-[#FAFAFA] dark:bg-gray-700 rounded-lg transition-all ${
                                selectedCategory === category
                                    ? "shadow-md"
                                    : "hover:bg-gray-300 dark:hover:bg-gray-600"
                            }`}
                        >
                            <span className="text-2xl">
                                {categoryIcons[category].icon}
                            </span>
                        </div>
                        <span className="mt-1 text-[12px] text-center w-full">
                            {categoryIcons[category].label}
                        </span>
                    </div>
                ))}
                <div
                    className="flex flex-col items-center justify-center cursor-pointer text-gray-600 dark:text-gray-400 p-1 w-full h-24 col-start-6"
                    onClick={() => setShowAllCategories(!showAllCategories)}
                >
                    <div className="w-12 h-12 flex items-center justify-center border-2 border-black rounded-xl">
                        {showAllCategories ? (
                            <ChevronUp size={20} />
                        ) : (
                            <ChevronDown size={20} />
                        )}
                    </div>
                    <span className="mt-1 text-[12px] text-center w-full">
                        {showAllCategories ? "닫기" : "더보기"}
                    </span>
                </div>
            </div>
        </div>
    );
}
