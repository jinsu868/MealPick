import { useEffect, useState } from "react";
import {
    Carousel,
    CarouselContent,
    CarouselItem,
} from "@/components/ui/carousel";
import { type CarouselApi } from "@/components/ui/carousel";

interface PostDetailCarouselProps {
    postImages: string[];
}

export default function ScrapDetailCarousel({
    postImages,
}: PostDetailCarouselProps) {
    const [api, setApi] = useState<CarouselApi | null>(null);
    const [currentIndex, setCurrentIndex] = useState(0);

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

    return (
        <div className="relative h-full max-h-[400px] w-full max-w-[600px]">
            <Carousel setApi={setApi}>
                <CarouselContent className="flex gap-0">
                    {postImages.map((img, index) => (
                        <CarouselItem
                            key={index}
                            className="flex justify-center items-center"
                        >
                            <img
                                src={img}
                                className="w-[80%] max-w-[600px] aspect-square object-cover overflow-hidden"
                                alt={`게시글 이미지 ${index + 1}`}
                                onError={(e) => {
                                    (e.target as HTMLImageElement).src =
                                        "https://via.placeholder.com/150";
                                }}
                            />
                        </CarouselItem>
                    ))}
                </CarouselContent>
            </Carousel>

            <div className="absolute bottom-3 left-1/2 transform -translate-x-1/2 flex gap-2">
                {postImages.map((_, index) => (
                    <div
                        key={index}
                        className={`w-2 h-2 rounded-full transition-all ${
                            currentIndex === index
                                ? "bg-gray-900"
                                : "bg-gray-400"
                        }`}
                    />
                ))}
            </div>
        </div>
    );
}
