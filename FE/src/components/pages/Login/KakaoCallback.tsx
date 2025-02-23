import { useDispatch } from "react-redux";
import { useNavigate } from "react-router-dom";
import { kakaoLogin } from "../../../api/auth";
import { useEffect } from "react";
import { login } from "@/redux/slices/userSlice";
import { getMyProfile } from "@/api/user";

export default function KakaoCallback() {
    const navigate = useNavigate();
    const dispatch = useDispatch();

    // const signupData = {
    //     age: 12,
    //     name: "test",
    //     email: "test_email@gmail.com",
    // };

    useEffect(() => {
        const query = new URLSearchParams(window.location.search);
        const code = query.get("code");

        const handleKakaoLogin = async () => {
            if (!code) {
                navigate("/login", {
                    state: { error: "카카오 로그인에 실패했습니다." },
                });
                return;
            }

            try {
                // 1. 카카오 로그인 API 호출
                console.log("code", code);
                // eslint-disable-next-line @typescript-eslint/no-explicit-any
                const loginResponse: any = await kakaoLogin(code);
                const accessToken = loginResponse.accessToken;
                console.log("accessToken", accessToken);

                if (!accessToken) {
                    navigate("/login", {
                        state: { error: "카카오 로그인에 실패했습니다." },
                    });
                    return;
                }

                localStorage.setItem("accessToken", accessToken);

                try {
                    const userProfile = await getMyProfile();
                    dispatch(login(userProfile));
                    // 4. 정상적으로 로그인되었을 경우만 이동
                    navigate("/");
                } catch (error) {
                    console.error("프로필 불러오기 실패:", error);
                    navigate("/signup", { state: { accessToken } }); // 에러 발생 시 회원가입 페이지로 이동
                }
            } catch {
                navigate("/login", {
                    state: { error: "카카오 로그인에 실패했습니다." },
                });
            }
        };

        handleKakaoLogin();
    }, [dispatch, navigate]);

    return (
        <>
            <div>카카오 로그인 중...</div>
        </>
    );
}
