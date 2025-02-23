import { useSelector } from "react-redux";
import { RootState } from "@/redux/store";

export function useUser() {
    const isLoggedIn = useSelector((state: RootState) => state.user.isLoggedIn);
    return isLoggedIn;
}
