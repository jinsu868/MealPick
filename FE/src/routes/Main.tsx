import Ranking from "@/components/pages/Main/Ranking";
import SeasonTrend from "@/components/pages/Main/SeasonTrend";
//import Bubble from "@/components/pages/Main/Bubble";
import Location from "@/components/pages/Main/Location";
import RookieMenu from "@/components/pages/Main/RookieMenu";
import YearlyTrend from "@/components/pages/Main/RecentTrend";
import { useEffect } from "react";
import SearchBar from "@/components/pages/Main/SearchBar";
import { WebSocketProvider } from "@/contexts/WebSocketContext";

export default function Main() {
    useEffect(() => {
        window.scrollTo(0, 0);
    }, []);

    return (
        <WebSocketProvider>
            <div className="flex flex-col w-screen max-w-[600px] justify-center items-start p-4 my-4">
                {/*<Bubble />*/}
                <Location />
                <div className="w-full max-w-[600px]">
                    <SearchBar />
                </div>
                <div className="w-full max-w-[600px]">
                    <Ranking></Ranking>
                </div>
                <div className="w-full max-w-[600px] lg:h-[20rem] h-[16rem] xl:h-[24rem]">
                    <SeasonTrend />
                </div>
                <div className="w-full max-w-[600px] lg:h-[20rem] h-[24rem] xl:h-[24rem]">
                    <YearlyTrend />
                </div>
                <div className="w-full max-w-[600px] lg:h-[20rem] h-[24rem] xl:h-[24rem] mb-20">
                    <RookieMenu />
                </div>
            </div>
        </WebSocketProvider>
    );
}
