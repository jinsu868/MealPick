import Popular from "@/components/pages/Gallery/Popular";
import Category from "@/components/pages/Gallery/Category";
import GalleryList from "@/components/pages/Gallery/GalleryList";

export default function GalleryPage() {
    return (
        <div className="w-screen max-w-[600px] mx-auto min-h-screen overflow-auto bg-white dark:bg-gray-900 text-gray-900 dark:text-gray-100">
            <Popular />
            <Category />
            <GalleryList />
        </div>
    );
}
