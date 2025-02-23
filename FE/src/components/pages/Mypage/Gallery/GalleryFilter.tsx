interface GalleryFilterProps {
    selectedFilter: "ALBUM" | "SCRAP";
    setSelectedFilter: (filter: "ALBUM" | "SCRAP") => void;
}

export default function GalleryFilter({
    selectedFilter,
    setSelectedFilter,
}: GalleryFilterProps) {
    return (
        <div className="mt-4 flex justify-center py-2 border-b mb-6">
            {["ALBUM", "SCRAP"].map((filter) => (
                <div
                    key={filter}
                    className={`w-full mx-4 py-2 rounded-full hover:bg-gray-100 dark:hover:bg-gray-400 ${
                        selectedFilter === filter
                            ? "font-bold text-blue-600"
                            : ""
                    }`}
                    onClick={() =>
                        setSelectedFilter(filter as "ALBUM" | "SCRAP")
                    }
                >
                    {filter === "ALBUM" ? "앨범" : "스크랩"}
                </div>
            ))}
        </div>
    );
}
