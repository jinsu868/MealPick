import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { searchTags, TagSearchResponse } from "@/api/tag";
import { motion, AnimatePresence } from "framer-motion";

export default function SearchBar() {
    const navigate = useNavigate();
    const [searchQuery, setSearchQuery] = useState("");
    const [results, setResults] = useState<TagSearchResponse[]>([]);

    const handleSearch = async (query: string) => {
        if (!query.trim()) {
            setResults([]);
            return;
        }

        try {
            const result = await searchTags(query);
            setResults(result);
        } catch (error) {
            console.log(error);
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

    const debouncedSearch = debounce(handleSearch, 200);

    const handleNavigate = (isTag: boolean, tagName: string) => {
        if (isTag) {
            navigate(`/food-detail?tagName=${tagName}`);
        } else {
            if (results.length < 1) return;
            if (tagName === results[0].tagName) {
                navigate(`/food-detail?tagName=${tagName}`);
            }
        }
    };

    useEffect(() => {
        debouncedSearch(searchQuery);
    }, [searchQuery]);

    return (
        <div>
            <div className="mb-2 flex flex-col items-center w-full">
                {/* 검색창 */}
                <div className="flex items-center justify-center w-full h-12 bg-white dark:bg-gray-200 rounded-full border border-gray-300 focus-within:ring-2 transition-all duration-300">
                    <input
                        type="text"
                        value={searchQuery}
                        onChange={(e) => {
                            setSearchQuery(e.target.value);
                            debouncedSearch(e.target.value);
                        }}
                        placeholder="태그를 검색해보세요!"
                        className="w-3/4 h-full px-4 text-lg focus-within:outline-none bg-transparent"
                        onKeyDown={(e) => {
                            if (e.key === "Enter" && !e.shiftKey) {
                                e.preventDefault();
                                handleNavigate(false, searchQuery);
                            }
                        }}
                    />
                    <button
                        onClick={() => handleNavigate(false, searchQuery)}
                        className="w-1/4 h-full bg-gradient-to-tr from-blue-500 to-blue-600 text-white text-lg rounded-r-full"
                    >
                        검색
                    </button>
                </div>
            </div>

            <AnimatePresence>
                {results.length > 0 && (
                    <div className="flex w-full gap-2 justify-start p-4">
                        {results.map((result, index) => (
                            <motion.button
                                key={result.tagName}
                                initial={{ y: -10, opacity: 0 }}
                                animate={{ y: 0, opacity: 1 }}
                                exit={{ y: -10, opacity: 0 }}
                                transition={{
                                    duration: 0.1,
                                    ease: "easeInOut",
                                    delay: index * 0.1,
                                }}
                                onClick={() => {
                                    handleNavigate(true, result.tagName);
                                }}
                                className={`dark:bg-gray-800 text-gray-800 dark:text-gray-200 px-4 py-2 rounded-full ${searchQuery === result.tagName ? "bg-blue-500 text-white" : "bg-gray-200"}`}
                            >
                                {result.tagName}
                            </motion.button>
                        ))}
                    </div>
                )}
            </AnimatePresence>
        </div>
    );
}
