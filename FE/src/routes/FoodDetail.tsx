import FoodChart from "@/components/pages/FoodDetail/FoodChart";
import MyPic from "@/components/pages/FoodDetail/MyPic";
import PicList from "@/components/pages/FoodDetail/PicList";
import { useEffect } from "react";
import { useNavigate } from "react-router-dom";

export default function FoodDetail() {
    const navigate = useNavigate();

    // Scroll to top on component mount
    useEffect(() => {
        window.scrollTo(0, 0);
    }, []);

    return (
        <div className="mt-8 w-screen max-w-[600px] overflow-hidden">
            <div className="w-full rounded-full flex justify-start">
                <div
                    className="w-fit text-sm px-2 py-1 rounded-full dark:text-slate-300 hover:text-blue-500 cursor-pointer text-gray-700 font-semibold"
                    onClick={() => navigate(-1)}
                >
                    뒤로가기
                </div>
            </div>
            <FoodChart />
            <MyPic />
            <PicList />
        </div>
    );
}
