import React, { useState } from "react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@radix-ui/react-label";
import { useNavigate } from "react-router-dom";
import { useDispatch, useSelector } from "react-redux";
import { RootState } from "@/redux/store";
import { emailLogin } from "@/api/auth";
import axios from "axios";

const LoginPage: React.FC = () => {
    const navigate = useNavigate();
    const dispatch = useDispatch();

    // 전역에서 user, token 상태 가져오기(이미 로그인했는지 체크 용도)
    const user = useSelector((state: RootState) => state.user?.user);
    const token = useSelector((state: RootState) => state.user?.token);

    // 로컬 폼 상태
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");

    // 에러/로딩 상태를 간단히 로컬에서 관리 (Redux로 관리해도 됨)
    const [error, setError] = useState("");
    const [loading, setLoading] = useState(false);

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();

        try {
            console.log(email, password);
            setLoading(true);
            setError("");

            // 서버에 로그인 요청
            const loginResponse = await emailLogin(email, password);
            const accessToken = loginResponse?.accessToken;

            if (!accessToken) {
                // 토큰이 없다면 로그인 실패 처리
                setError("로그인 실패: 토큰이 없습니다.");
                setLoading(false);
                return;
            }

            const userResponse = await axios.get("", {
                headers: {
                    Authorization: `Bearer ${accessToken}`,
                },
                withCredentials: true,
            });
            const {
                socialId,
                nickname,
                profileImage,
                email: userEmail,
                description,
                noticeCheck,
                darkModeCheck,
            } = userResponse.data;

            dispatch(
                login({
                    user: {
                        socialId,
                        nickname,
                        profileImage,
                        email: userEmail,
                        description,
                        noticeCheck,
                        darkModeCheck,
                    },
                    token: accessToken,
                }),
            );
            navigate("/");
        } catch (err) {
            setError("로그인 에러가 발생했습니다.");
        } finally {
            setLoading(false);
        }
    };

    // 이미 로그인된 경우 간단히 메시지만 표시
    // if (user && token) {
    //     return (
    //         <div>
    //             <h2>이미 로그인되어 있습니다.</h2>
    //             <p>닉네임: {user.nickname}</p>
    //         </div>
    //     );
    // }

    return (
        <div className="grid grid-cols-1">
            <div className="text-2xl font-bold mb-10">로그인</div>
            <form onSubmit={handleSubmit}>
                <div className="flex flex-col items-center gap-5">
                    <div className="grid gap-2">
                        <div className="flex items-center">
                            <Label htmlFor="email">이메일</Label>
                        </div>
                        <Input
                            id="email"
                            type="email"
                            placeholder="meal@pic.com"
                            value={email}
                            onChange={(e) => setEmail(e.target.value)}
                            style={{
                                width: "300px",
                            }}
                            required
                        />
                    </div>
                    <div className="grid gap-2">
                        <div className="flex items-center">
                            <Label htmlFor="password">비밀번호</Label>
                            {/* <a
                                        href="#"
                                        className="ml-auto inline-block text-sm underline-offset-4 hover:underline"
                                    >
                                        Forgot your password?
                                    </a> */}
                        </div>
                        <Input
                            id="password"
                            type="password"
                            value={password}
                            onChange={(e) => setPassword(e.target.value)}
                            required
                            style={{
                                width: "300px",
                            }}
                        />
                    </div>
                    <Button
                        type="submit"
                        disabled={loading}
                        className="mt-5 w-[300px]"
                    >
                        로그인
                    </Button>
                </div>
                <div className="mt-10 text-center text-sm">
                    아직 회원이 아니신가요?{" "}
                    <a
                        href="/signup"
                        className="text-gray-600 underline hover:text-gray-800 font-bold"
                    >
                        회원가입
                    </a>
                </div>
            </form>
        </div>
    );
};

export default LoginPage;
