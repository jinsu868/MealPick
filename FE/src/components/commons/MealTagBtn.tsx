export function MorningTagBtn() {
    return (
        <div className="h-4 pl-0.5 pr-1 bg-cyan-500 rounded-full flex justify-center items-center">
            <div className="w-3 h-3 p-0.5 rounded-full bg-white mr-1 flex justify-center items-center shadow-inner">
                <img src="/imgs/halfsun.webp" alt="sun icon" />
            </div>
            <div className="text-xs pr-1 text-white font-medium">아침</div>
        </div>
    );
}

export function LunchTagBtn() {
    return (
        <div className="h-4 pl-0.5 pr-1 bg-orange-400 rounded-full flex justify-center items-center">
            <div className="w-3 h-3 p-0.5 rounded-full bg-white mr-1 flex justify-center items-center shadow-inner">
                <img src="/imgs/sun.webp" alt="sun icon" />
            </div>
            <div className="text-xs pr-1 text-white font-medium">점심</div>
        </div>
    );
}

export function DinnerTagBtn() {
    return (
        <div className="h-4 pl-0.5 pr-1 bg-indigo-500 rounded-full flex justify-center items-center">
            <div className="w-3 h-3 p-0.5 rounded-full bg-white mr-1 flex justify-center items-center shadow-inner">
                <img src="/imgs/moon.webp" alt="moon icon" />
            </div>
            <div className="text-xs pr-1 text-white font-medium">저녁</div>
        </div>
    );
}
