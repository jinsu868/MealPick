interface Props {
    toggleGrid: () => void;
    toggleBlur: () => void;
    gridEnabled: boolean;
    blurEnabled: boolean;
}

export default function SettingsPanel({
    toggleGrid,
    toggleBlur,
    gridEnabled,
    blurEnabled,
}: Props) {
    return (
        <div className="w-[50%] flex gap-4 justify-around">
            <button onClick={toggleGrid} className="text-gray-700 font-bold">
                {gridEnabled ? "그리드 ON" : "그리드 OFF"}
            </button>
            <button onClick={toggleBlur} className="text-gray-700 font-bold">
                {blurEnabled ? "블러 ON" : "블러 OFF"}
            </button>
        </div>
    );
}
