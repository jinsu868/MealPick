import { useRef, useState, useEffect } from "react";
import exifr from "exifr";
import { getMealTime } from "@/types/MealTime";
import { motion, AnimatePresence } from "framer-motion";
import { searchTags } from "@/api/tag"; // Import the searchTags API

interface PostEditorProps {
    selectedImages: string[];
    selectedFiles: File[];
    onCancel: () => void;
    onSubmit: (
        title: string,
        content: string,
        mealTime: string,
        foodTag: string,
        tags: string[],
        images: File[],
        latitude: number,
        longitude: number,
    ) => void;
}

const FOOD_TAGS: { [key: string]: string } = {
    한식: "KOREAN",
    양식: "WESTERN",
    일식: "JAPANESE",
    중식: "CHINESE",
    분식: "BUNSIK",
    아시안: "ASIAN",
    패스트푸드: "FASTFOOD",
    치킨: "CHICKEN",
    피자: "PIZZA",
    디저트: "DESSERT",
};

export default function PostEditor({
    selectedImages,
    selectedFiles,
    onCancel,
    onSubmit,
}: PostEditorProps) {
    const [title, setTitle] = useState("");
    const [content, setContent] = useState("");
    const [longitude, setLongitude] = useState<number>(0);
    const [latitude, setLatitude] = useState<number>(0);
    const [tags, setTags] = useState<string[]>([]);
    const [selectedFoodTag, setSelectedFoodTag] = useState<string>("");
    const [invalidSymbol, setInvalidSymbol] = useState<boolean>(false);
    const [tagInputValue, setTagInputValue] = useState<string>("");
    const [tagSuggestions, setTagSuggestions] = useState<string[]>([]);
    const tagInputRef = useRef<HTMLInputElement>(null);
    const mealTime = getMealTime();

    useEffect(() => {
        if (selectedFiles.length > 0) {
            const firstFile = selectedFiles[0];
            exifr
                .parse(firstFile)
                .then((exifData) => {
                    if (exifData && exifData.latitude && exifData.longitude) {
                        setLatitude(exifData.latitude);
                        setLongitude(exifData.longitude);
                    } else {
                        if (navigator.geolocation) {
                            navigator.geolocation.getCurrentPosition(
                                (position) => {
                                    setLatitude(position.coords.latitude);
                                    setLongitude(position.coords.longitude);
                                },
                                (error) => {
                                    console.error("Geolocation error:", error);
                                },
                            );
                        } else {
                            console.error(
                                "Geolocation is not supported by this browser.",
                            );
                        }
                    }
                })
                .catch((error) => {
                    if (navigator.geolocation) {
                        navigator.geolocation.getCurrentPosition(
                            (position) => {
                                setLatitude(position.coords.latitude);
                                setLongitude(position.coords.longitude);
                            },
                            (error) => {
                                console.error("Geolocation error:", error);
                            },
                        );
                    } else {
                        console.error(
                            "Geolocation is not supported by this browser.",
                            error,
                        );
                    }
                });
        }
    }, [selectedFiles]);

    const fetchTagSuggestions = async (query: string) => {
        if (!query.trim()) {
            setTagSuggestions([]);
            return;
        }

        try {
            const result = await searchTags(query);
            const suggestions = result.map((tag) => tag.tagName);
            setTagSuggestions(suggestions);
        } catch (error) {
            console.error("Error fetching tag suggestions:", error);
        }
    };

    const debounce = <T extends (...args: any[]) => void>(
        func: T,
        delay: number,
    ) => {
        let timeoutId: NodeJS.Timeout;
        return (...args: Parameters<T>) => {
            clearTimeout(timeoutId);
            timeoutId = setTimeout(() => func(...args), delay);
        };
    };

    const debouncedFetchSuggestions = debounce(fetchTagSuggestions, 300);

    const handleTagInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
        const inputValue = e.target.value;
        setTagInputValue(inputValue);

        debouncedFetchSuggestions(inputValue);

        if (/^[a-zA-Z0-9가-힣\s]*$/.test(inputValue)) {
            setInvalidSymbol(false);
        } else {
            setInvalidSymbol(true);
        }
    };

    const handleAddTagFromSuggestion = (tag: string) => {
        if (!tags.includes(tag)) {
            setTags([...tags, tag]);
        }
        setTagInputValue("");
        setTagSuggestions([]);
    };

    const handleAddTag = () => {
        if (tagInputValue.trim() && !tags.includes(tagInputValue.trim())) {
            setTags([...tags, tagInputValue.trim()]);
            setTagInputValue("");
            setTagSuggestions([]);
            setInvalidSymbol(false);
        }
    };

    const handleRemoveTag = (index: number) => {
        setTags(tags.filter((_, i) => i !== index));
    };

    return (
        <div className="w-screen max-w-[600px] h-screen justify-center mx-auto flex flex-col p-3">
            <h2 className="text-gray-700 text-md font-semibold mb-2">
                선택한 이미지
            </h2>
            <div className="w-screen max-w-[600px] flex justify-center">
                <div className="grid grid-cols-3 gap-2 gap-x-6">
                    {selectedImages.map((img, index) => (
                        <img
                            key={index}
                            src={img}
                            className="w-24 h-24 object-cover rounded-md"
                            alt="선택된 이미지"
                        />
                    ))}
                </div>
            </div>

            <div className="flex flex-col gap-3 mt-4">
                <input
                    type="text"
                    placeholder="제목을 입력하세요"
                    value={title}
                    onChange={(e) => setTitle(e.target.value)}
                    className="p-2 border rounded-md w-full"
                />
                <textarea
                    placeholder="내용을 입력하세요"
                    value={content}
                    onChange={(e) => setContent(e.target.value)}
                    className="p-2 border rounded-md w-full h-32 resize-none"
                />
            </div>

            <div className="mt-3">
                <label className="block text-gray-700 text-sm mb-1">
                    음식 태그 선택
                </label>
                <select
                    value={selectedFoodTag}
                    onChange={(e) => setSelectedFoodTag(e.target.value)}
                    className="w-full p-2 border rounded-md bg-white"
                >
                    <option value="" disabled>
                        음식 태그 선택
                    </option>
                    {Object.keys(FOOD_TAGS).map((tag) => (
                        <option key={tag} value={tag}>
                            {tag}
                        </option>
                    ))}
                </select>
            </div>

            <div className="mt-3">
                <label className="block text-gray-700 text-sm mb-1">
                    태그 입력
                </label>
                <div className="flex flex-wrap gap-2">
                    {tags.map((tag, index) => (
                        <span
                            key={index}
                            className="bg-blue-500 text-white px-3 py-1 rounded-full text-sm cursor-pointer"
                            onClick={() => handleRemoveTag(index)}
                        >
                            {tag} ✖
                        </span>
                    ))}
                </div>
                <input
                    type="text"
                    ref={tagInputRef}
                    placeholder="태그 입력 후 Enter"
                    value={tagInputValue}
                    onChange={handleTagInputChange}
                    className={`border p-2 rounded-md w-full mt-2 ${
                        invalidSymbol ? "border-red-500" : ""
                    }`}
                    onKeyDown={(e) => {
                        if (invalidSymbol) return;
                        if (e.key === "Enter") {
                            e.preventDefault();
                            handleAddTag();
                        }
                    }}
                />
                {invalidSymbol && (
                    <p className="text-red-500 text-sm mt-1">
                        특수 문자는 입력할 수 없습니다.
                    </p>
                )}
            </div>

            {/* Tag Suggestions */}
            <AnimatePresence>
                {tagSuggestions.length > 0 && (
                    <div className="flex flex-wrap gap-2 mt-2">
                        {tagSuggestions.map((tag, index) => (
                            <motion.button
                                key={tag}
                                initial={{ y: -10, opacity: 0 }}
                                animate={{ y: 0, opacity: 1 }}
                                exit={{ y: -10, opacity: 0 }}
                                transition={{
                                    duration: 0.1,
                                    ease: "easeInOut",
                                    delay: index * 0.1,
                                }}
                                onClick={() => handleAddTagFromSuggestion(tag)}
                                className="bg-gray-200 text-gray-800 px-3 py-1 rounded-full text-sm"
                            >
                                {tag}
                            </motion.button>
                        ))}
                    </div>
                )}
            </AnimatePresence>

            <div className="flex gap-3 mt-4">
                <button
                    onClick={onCancel}
                    className="w-1/2 bg-gray-400 text-white py-3 rounded-lg font-bold"
                >
                    취소
                </button>
                <button
                    onClick={() =>
                        onSubmit(
                            title,
                            content,
                            mealTime,
                            FOOD_TAGS[selectedFoodTag],
                            tags,
                            selectedFiles,
                            latitude,
                            longitude,
                        )
                    }
                    className="w-1/2 bg-blue-600 text-white py-3 rounded-lg font-bold"
                    disabled={!selectedFoodTag}
                >
                    게시글 업로드
                </button>
            </div>
        </div>
    );
}
