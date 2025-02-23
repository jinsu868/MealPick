import { createSlice } from "@reduxjs/toolkit";

const initialState = {
    isDark: localStorage.getItem("darkMode") === "true",
};

const themeSlice = createSlice({
    name: "theme",
    initialState,
    reducers: {
        lightMode: (state) => {
            state.isDark = false;
            document.documentElement.classList.remove("dark");
            localStorage.setItem("darkMode", "false");
        },
        darkMode: (state) => {
            state.isDark = true;
            document.documentElement.classList.add("dark");
            localStorage.setItem("darkMode", "true");
        },
    },
});

export const themeActions = themeSlice.actions;
export default themeSlice.reducer;
