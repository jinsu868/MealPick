import {
    Carousel,
    CarouselContent,
    CarouselItem,
} from "@/components/ui/carousel";

export default function Popular() {
    return (
        <div className="mb-2 px-2 pt-6">
            <h2 className="text-xs font-medium text-gray-800 dark:text-gray-200 mb-1 text-left">
                🔥 Hot Pics
            </h2>
            <Carousel className="w-full">
                <CarouselContent className="gap-1">
                    {[...Array(10)].map((_, index) => (
                        <CarouselItem
                            key={index}
                            className="basis-1/3 md:basis-1/4 px-0"
                        >
                            <div className="h-28 rounded-xl bg-gray-200 dark:bg-gray-800 flex items-center justify-center">
                                <span className="text-gray-500 dark:text-gray-400">
                                    Hot {index + 1}
                                </span>
                            </div>
                        </CarouselItem>
                    ))}
                </CarouselContent>
            </Carousel>
        </div>
    );
}
