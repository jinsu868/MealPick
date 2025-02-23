import { FaTimes } from "react-icons/fa";
import { motion, AnimatePresence } from "framer-motion";

interface ImagePreviewProps {
    images: string[];
    onRemove: (index: number) => void;
}

export default function ImagePreview({ images, onRemove }: ImagePreviewProps) {
    return (
        <div>
            <div className="flex items-center gap-2 w-full text-start font-semibold text-slate-600 dark:text-slate-50">
                <img src="/imgs/image.webp" className="w-6" />
                이미지 미리보기
            </div>
            <div className="mt-2 p-3 w-full bg-gray-100 dark:bg-slate-300 rounded-lg overflow-x-auto whitespace-nowrap h-28">
                <div className="flex gap-2 h-full items-center">
                    <AnimatePresence>
                        {images.map((img, index) => (
                            <motion.div
                                key={img}
                                className="relative flex-shrink-0"
                                initial={{ scale: 0, opacity: 0 }}
                                animate={{ scale: 1, opacity: 1 }}
                                exit={{ scale: 0, opacity: 0 }}
                                transition={{ duration: 0.2, ease: "easeOut" }}
                            >
                                <img
                                    src={img}
                                    className="w-20 h-20 object-cover rounded-md border border-gray-300"
                                    alt="선택된 이미지"
                                />
                                <button
                                    onClick={() => onRemove(index)}
                                    className="absolute top-1 right-1 bg-red-600 text-white p-1 rounded-full"
                                >
                                    <FaTimes size={12} />
                                </button>
                            </motion.div>
                        ))}
                    </AnimatePresence>
                </div>
            </div>
        </div>
    );
}
