import { useEffect, useState } from "react";
import { Carousel, CarouselContent, CarouselItem } from "../ui/carousel";
import { type CarouselApi } from "../ui/carousel";
import { PostDetailDefault } from "@/types/PostDetail";
import Tag from "../commons/Tag";
import { convertLinks } from "../commons/ConvertLinks";

export default function PostContent(props: PostDetailDefault) {
    const [isPopupOpen, setIsPopupOpen] = useState(false);
    const [expandedTags, setExpandedTags] = useState(3);
    const [api, setApi] = useState<CarouselApi | null>(null);
    const [currentIndex, setCurrentIndex] = useState(0);
    const [indicatorColor, setIndicatorColor] = useState("bg-gray-400");
    const [color, setColor] = useState("bg-gray-500");

    const tags = props.tags;
    const postTitle = props.title;
    const postContent = props.content;
    const postImages = props.postImageUrls;

    // 인디케이터 색상 변경
    useEffect(() => {
        const indicatorColorMap: Record<string, string> = {
            MORNING: "bg-cyan-400",
            LUNCH: "bg-orange-500",
            DINNER: "bg-indigo-600",
        };
        const colorMap: Record<string, string> = {
            MORNING: "bg-cyan-200",
            LUNCH: "bg-orange-300",
            DINNER: "bg-indigo-400",
        };
        setIndicatorColor(indicatorColorMap[props.mealTime] ?? "bg-gray-400");
        setColor(colorMap[props.mealTime] ?? "bg-gray-500");
    }, [props.mealTime]);

    // content 접기/펼치기
    const togglePopup = () => setIsPopupOpen(!isPopupOpen);

    // tag 펼치기
    const toggleExpand = () => {
        const target = expandedTags < tags.length ? tags.length : 3;
        const updateTags = (i: number) => {
            if (i === target) return;
            setExpandedTags((prev) => (prev < target ? prev + 1 : prev - 1));
            setTimeout(() => updateTags(i < target ? i + 1 : i - 1), 50);
        };
        updateTags(expandedTags);
    };

    useEffect(() => {
        if (!api) return;
        const updateIndex = () => {
            setCurrentIndex(api.selectedScrollSnap());
        };
        api.on("select", updateIndex);
        return () => {
            api.off("select", updateIndex);
        };
    }, [api]);

    // Convert URLs in postContent to clickable links
    const convertedContent = convertLinks(postContent);

    return (
        <>
            {/* 이미지 슬라이드 */}
            <div className="pt-1 relative h-full max-h-[600px] w-full max-w-[600px]">
                <Carousel setApi={setApi}>
                    <CarouselContent className="flex gap-0">
                        {postImages.map((img, index) => (
                            <CarouselItem
                                key={index}
                                className="justify-center items-center flex"
                            >
                                <img
                                    src={img}
                                    className="w-full max-w-[600px] aspect-square object-cover overflow-hidden"
                                />
                            </CarouselItem>
                        ))}
                    </CarouselContent>
                </Carousel>
                {/* 이미지 인디케이터 */}
                <div className="absolute bottom-3 left-1/2 transform -translate-x-1/2 flex gap-3">
                    {postImages.map((_, index) => (
                        <div
                            key={index}
                            className={`w-2 h-2 rounded-full transition-all shadow-white ${currentIndex === index ? indicatorColor : "bg-[RGBA(100,100,100,0.8)]"}`}
                        />
                    ))}
                </div>
            </div>

            {/* 태그 */}
            <div className="w-full pt-3 pl-2 pr-2">
                <div className="text-left text-gray-800 text-xs font-semibold flex flex-wrap gap-1.5">
                    {tags.slice(0, expandedTags).map((tag, index) => (
                        <Tag
                            key={index}
                            tagName={tag}
                            colorClass={color}
                            animated={true}
                        />
                    ))}
                    {tags.length > 3 && (
                        <button
                            onClick={toggleExpand}
                            className="p-1 pl-2 pr-2 rounded-full bg-gray-300 opacity-50 text-xs font-bold"
                        >
                            {expandedTags > 3 ? "-" : "+"}
                        </button>
                    )}
                </div>
            </div>

            {/* 내용 */}
            <div className="pl-4 mt-4 text-left text-sm font-bold">
                {postTitle}
            </div>
            <div className="pl-4 pr-4 min-h-4 mt-1">
                <div className="text-left text-sm">
                    {postContent.length > 50 && !isPopupOpen
                        ? convertLinks(postContent.slice(0, 50))
                        : convertedContent}{" "}
                    {postContent.length > 50 && (
                        <span
                            onClick={togglePopup}
                            className="ml-1 text-gray-500 hover:underline cursor-pointer"
                        >
                            {isPopupOpen ? " 접기" : " ...더보기"}
                        </span>
                    )}
                </div>
            </div>
        </>
    );
}
