import { useState } from "react";
import { v4 as uuidv4 } from "uuid";

type ToggleProps = {
    initialState?: boolean;
    onIcon?: React.ReactNode;
    offIcon?: React.ReactNode;
    onColor?: string;
    offColor?: string;
    size?: "sm" | "md" | "lg";
    onToggle?: (state: boolean) => void;
};

function Toggle({
    initialState = false,
    onIcon = null,
    offIcon = null,
    onColor = "bg-green-500",
    offColor = "bg-gray-300",
    size = "md",
    onToggle,
}: ToggleProps) {
    const [isToggled, setIsToggled] = useState(initialState);

    const sizeStyles = {
        sm: "w-10 h-5",
        md: "w-14 h-7",
        lg: "w-20 h-10",
    };

    const btnSizeStyles = {
        sm: "w-4 h-4",
        md: "w-6 h-6",
        lg: "w-9 h-9",
    };

    const handleToggle = () => {
        const newState = !isToggled;
        setIsToggled(newState);
        if (onToggle) {
            onToggle(newState);
        }
    };

    const buttonSize = btnSizeStyles[size];
    const inputId = uuidv4();

    return (
        <label
            htmlFor={inputId}
            className={`relative inline-block ${sizeStyles[size]}`}
        >
            <input
                id={inputId}
                type="checkbox"
                checked={isToggled}
                onChange={handleToggle}
                className="sr-only"
            />
            <div
                className={`block w-full h-full rounded-full transition-colors duration-300 ${isToggled ? onColor : offColor} [box-shadow:inset_0_2px_2px_rgba(0,0,0,0.3)]`}
            />
            <div
                className={`absolute left-1 top-0 bottom-0 m-auto transition-transform duration-300 transform bg-white bg-opacity-50 rounded-full ${buttonSize} flex items-center justify-center ${isToggled ? "translate-x-full" : "translate-x-0"} [box-shadow:0_2px_2px_rgba(0,0,0,0.3)]`}
            >
                <span>{isToggled ? onIcon : offIcon}</span>
            </div>
        </label>
    );
}

export default Toggle;
