import { MemberDetail } from "@/types/MemberDetail";
import { createSlice, PayloadAction } from "@reduxjs/toolkit";

interface UserState {
    isLoggedIn: boolean;
    profile: MemberDetail | null;
}

const initialState: UserState = {
    isLoggedIn: !!localStorage.getItem("accessToken"),
    profile: null,
};

const userSlice = createSlice({
    name: "user",
    initialState,
    reducers: {
        login: (state, action: PayloadAction<MemberDetail>) => {
            state.isLoggedIn = true;
            state.profile = action.payload;
        },
        logout: (state) => {
            state.isLoggedIn = false;
            state.profile = null;
            localStorage.removeItem("accessToken");
        },
        setProfile: (state, action: PayloadAction<MemberDetail | null>) => {
            state.profile = action.payload;
        },
    },
});

export const { login, logout, setProfile } = userSlice.actions;
export default userSlice.reducer;
