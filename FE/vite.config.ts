import path from "path";
import react from "@vitejs/plugin-react";
import { defineConfig } from "vite";
import { VitePWA } from "vite-plugin-pwa";

export default defineConfig({
    plugins: [
        react(),
        VitePWA({
            registerType: "autoUpdate",
            devOptions: {
                enabled: true,
            },
        }),
    ],
    server: {
        allowedHosts: true,
    },
    resolve: {
        alias: {
            "@": path.resolve(__dirname, "./src"),
        },
    },
    build: {
        rollupOptions: {
            input: {
                main: "./index.html",
                sw: "./sw.js",
            },
        },
    },
    define: {
        global: {},
        "process.env.VITE_KAKAO_MAP_API_KEY": `"${process.env.VITE_KAKAO_MAP_API_KEY}"`,
    },
});
