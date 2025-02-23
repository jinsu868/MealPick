import "./App.css";
import { Outlet, useLocation, useNavigate } from "react-router-dom";
import Footer from "./components/commons/Footer";
import { Toaster } from "./components/ui/toaster";
import { useEffect } from "react";
import { registerServiceWorker } from "./registerServiceWorker";
import { handleAllowNotification } from "./utils/notificationPermission";
import { firebaseConfig } from "./utils/initFirebase";
import Header from "./components/commons/Header";
import { useSelector } from "react-redux";
import { RootState } from "./redux/store";
import { getMyProfile } from "./api/user";

function App() {
    const location = useLocation();
    const navigate = useNavigate();

    // ChatRoom의 패턴을 정확히 체크 - /chat/숫자 형태만 찾음
    const isChatRoomPath = /^\/chat\/[^/]+$/.test(location.pathname);

    const isDark = useSelector((state: RootState) => state.theme.isDark);

    const isAccesTokenValidate = async () => {
        const accessToken = localStorage.getItem("accessToken");
        if (!accessToken) {
            navigate("/login");
        }

        try {
            await getMyProfile();
        } catch (error) {
            console.log(error);
            navigate("/login");
        }
    };

    useEffect(() => {
        isAccesTokenValidate();
    }, []);

    useEffect(() => {
        registerServiceWorker(firebaseConfig);
        handleAllowNotification();
    }, []);
    return (
        <div className={`${isDark ? "dark" : ""}`}>
            <div className="min-h-screen bg-white dark:bg-gray-800">
                <Header />
                <Outlet />

                {!isChatRoomPath && <Footer />}
                <Toaster />
            </div>
        </div>
    );
}

export default App;
