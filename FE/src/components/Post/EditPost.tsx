import { deletePost } from "@/api/post";
import { MealTime } from "@/types/MealTime";
import { useEffect, useRef, useState } from "react";

interface EditPostModalProps {
    post: {
        postId: number;
        title: string;
        mainImageUrl: string;
        content: string;
        tags: string[];
        mealTime: MealTime;
    };
    onClose: () => void;
    onSave: (updatedPost: { content: string; tags: string[] }) => void;
}

export default function EditPostModal({
    post,
    onClose,
    onSave,
}: EditPostModalProps) {
    // 수정 가능한 데이터 상태 관리
    const [content, setContent] = useState(post.content);
    const [tags, setTags] = useState<string[]>(post.tags);

    // 동적으로 태그 색상 변경
    const [tagColor, setTagColor] = useState("bg-gray-300");

    const [invalidSymbol, setInvalidSymbol] = useState<boolean>(false);
    const tagInputRef = useRef<HTMLInputElement>(null);

    useEffect(() => {
        const colorMap: Record<string, string> = {
            MORNING: "bg-cyan-200",
            LUNCH: "bg-orange-300",
            DINNER: "bg-indigo-400",
        };
        setTagColor(colorMap[post.mealTime] ?? "bg-gray-500");
    }, [post.mealTime]);

    // 태그 삭제
    const handleRemoveTag = (tagToRemove: string) => {
        setTags(tags.filter((tag) => tag !== tagToRemove));
    };

    // 태그 추가
    const handleAddTag = () => {
        if (tagInputRef.current?.value.trim()) {
            const inputValue = tagInputRef.current.value.trim();
            if (/^[a-zA-Z0-9가-힣\s]*$/.test(inputValue)) {
                setTags([...tags, inputValue]);
                tagInputRef.current.value = "";
                setInvalidSymbol(false);
            } else {
                setInvalidSymbol(true);
            }
        }
    };

    const handlePostDelete = async () => {
        try {
            await deletePost(post.postId);
            onClose();
        } catch (error) {
            console.error(error);
        }
    };

    return (
        <div className="fixed inset-0 flex items-center justify-center bg-black/50 z-50">
            <div className="bg-white p-6 rounded-lg w-[90vw] max-w-2xl max-h-[90vh] overflow-auto shadow-lg text-left dark:bg-slate-700">
                <h2 className="text-lg font-semibold mb-4 text-center">
                    게시글 수정
                </h2>

                {/* 대표 이미지 (수정 불가) */}
                <img
                    src={post.mainImageUrl}
                    alt="Main"
                    className="w-full object-cover rounded-md mb-4 aspect-square"
                />

                <div className="mb-4">
                    <label className="block text-sm font-medium text-gray-700">
                        태그
                    </label>
                    <div className="flex flex-wrap gap-2 mb-2">
                        {tags.map((tag) => (
                            <span
                                key={tag}
                                className={`px-3 py-1 text-sm flex items-center gap-1 cursor-pointer dark:text-black hover:opacity-75 transition rounded-full ${tagColor}`}
                                onClick={() => handleRemoveTag(tag)}
                            >
                                {tag} ×
                            </span>
                        ))}
                    </div>
                    <input
                        type="text"
                        ref={tagInputRef}
                        placeholder="태그 입력 후 Enter"
                        className={`border p-2 rounded-md w-full ${
                            invalidSymbol ? "border-red-500" : ""
                        }`}
                        onKeyDown={(e) => {
                            if (e.key === "Enter") {
                                e.preventDefault();
                                handleAddTag();
                            }
                        }}
                        onChange={(e) => {
                            const inputValue = e.target.value;
                            if (/^[a-zA-Z0-9가-힣\s]*$/.test(inputValue)) {
                                setInvalidSymbol(false);
                            } else {
                                setInvalidSymbol(true);
                            }
                        }}
                    />
                    {invalidSymbol && (
                        <p className="text-red-500 text-sm mt-1">
                            특수 문자는 입력할 수 없습니다.
                        </p>
                    )}
                </div>

                {/* 제목 (수정 불가) */}
                <div className="mb-2 font-semibold text-gray-800 dark:text-gray-200">
                    {post.title}
                </div>

                {/* 내용 수정 */}
                <div className="mb-4">
                    <textarea
                        className="w-full border p-2 rounded dark:bg-gray-700"
                        rows={4}
                        value={content}
                        onChange={(e) => setContent(e.target.value)}
                    />
                </div>

                {/* 버튼들 */}
                <div className="flex justify-between gap-2">
                    <button
                        className="bg-red-500 text-white px-4 rounded-full"
                        onClick={handlePostDelete}
                    >
                        Pic 삭제
                    </button>
                    <div className="flex gap-4">
                        <button
                            className="px-4 py-2 bg-gray-300 rounded"
                            onClick={onClose}
                        >
                            취소
                        </button>
                        <button
                            className="px-4 py-2 bg-blue-500 text-white rounded"
                            onClick={() => onSave({ content, tags })}
                        >
                            저장
                        </button>
                    </div>
                </div>
            </div>
        </div>
    );
}
