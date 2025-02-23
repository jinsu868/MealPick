import { createBrowserRouter, Outlet } from "react-router-dom";
import App from "./App";
import AddPost from "./routes/AddPost";
import Mainpage from "./routes/Mainpage";
// import Gallery from "./routes/Gallery";
import Map from "./components/map/Map";
import Login from "./routes/Login";
import KakaoCallback from "./components/pages/Login/KakaoCallback";
import SignUp from "./routes/SignUp";
import BasicInfo from "./components/pages/Signup/BasicInfo";
import DetailInfo from "./components/pages/Signup/DetailInfo";
import UserPage from "./routes/Userpage";
// import Mypage from "./routes/Profile/Mypage";
import EmailLogin from "./components/pages/Login/EmailLogin";
import Camera from "./components/pages/camera/Camera";
import ChatRoom from "./routes/ChatRoom";
import ChatList from "./routes/ChatList";
import FoodDetail from "./routes/FoodDetail";
import Main from "./routes/Main";
import { WebSocketProvider } from "./contexts/WebSocketContext";

const router = createBrowserRouter([
    {
        path: "/",
        element: <App />,
        children: [
            {
                path: "",
                element: (
                    <WebSocketProvider pageId="main">
                        <Main />,
                    </WebSocketProvider>
                ),
            },
            {
                path: "/main",
                element: <Mainpage />,
            },
            {
                path: "/map",
                element: <Map />,
            },
            {
                path: "/chat",
                element: (
                    <WebSocketProvider pageId="chatlist">
                        <ChatList />
                    </WebSocketProvider>
                ),
            },
        ],
    },
    {
        path: "/food-detail",
        element: <FoodDetail />,
    },
    {
        path: "/login",
        element: <Login />,
    },
    {
        path: "/post",
        element: <AddPost />,
    },
    {
        path: "/email-login",
        element: <EmailLogin />,
    },
    {
        path: "auth/redirect",
        element: <KakaoCallback />,
    },
    {
        path: "/signup",
        element: <SignUp />,
        children: [
            {
                path: "",
                element: <BasicInfo />,
            },
            {
                path: "detail",
                element: <DetailInfo />,
            },
        ],
    },
    {
        path: "/camera",
        element: <Camera />,
    },
    {
        path: "/user/:id",
        element: <UserPage />,
    },
    {
        path: "/chat/:roomId",
        element: (
            <WebSocketProvider pageId="chatroom">
                <ChatRoom />
            </WebSocketProvider>
        ),
    },
]);

export default router;
