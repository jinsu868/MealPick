import { useSelector, useDispatch } from "react-redux";
import { RootState } from "@/redux/store";
import { themeActions } from "@/redux/slices/themeSlice";
import Toggle from "@/components/commons/ToggleBtn";
import { CiDark, CiBrightnessUp } from "react-icons/ci";

const Darktoggle = () => {
    const isDark = useSelector((state: RootState) => state.theme.isDark);
    const dispatch = useDispatch();

    const handleDarkMode = (state: boolean) => {
        dispatch(state ? themeActions.darkMode() : themeActions.lightMode());
    };

    return (
        <Toggle
            initialState={isDark}
            size="md"
            onIcon={<CiDark className="w-4 h-4 text-white" />}
            offIcon={<CiBrightnessUp className="w-4 h-4 text-yellow-700" />}
            onColor="bg-gray-800 border border-gray-600"
            offColor="bg-yellow-500 border border-gray-400"
            onToggle={handleDarkMode}
        />
    );
};

export default Darktoggle;
