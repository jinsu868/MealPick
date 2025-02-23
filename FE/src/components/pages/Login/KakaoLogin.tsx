import KakaoLoginButton from "../../pages/Login/KakaoLoginButton";
import { Link, useLocation } from "react-router-dom";
import { useEffect } from "react";
import { useToast } from "@/hooks/use-toast";
import { Toaster } from "@/components/ui/toaster";

export default function KakaoLogin() {
    const KAKAO_CLIENT_ID = import.meta.env.VITE_KAKAO_CLIENT_ID;
    const KAKAO_REDIRECT_URI = import.meta.env.VITE_KAKAO_REDIRECT_URI;

    const location = useLocation();
    const { toast } = useToast();

    useEffect(() => {
        if (location.state?.error) {
            toast({
                description: location.state.error,
            });
        }
    }, [location, toast]);

    const handleKakaoLogin = () => {
        window.location.href = `https://kauth.kakao.com/oauth/authorize?response_type=code&client_id=${KAKAO_CLIENT_ID}&redirect_uri=${KAKAO_REDIRECT_URI}`;
    };

    return (
        <div className="grid grid-cols-1">
            <div className="fixed inset-0 bg-[url('/imgs/qwewqe.jpg')] bg-cover bg-center bg-no-repeat blur-sm"></div>
            <div className="z-10 w-fit h-fit bg-[rgba(255,255,255,0.8)] rounded-lg mx-auto p-10 relative">
                <div className="my-10 text-3xl font-bold">MeaLPic</div>
                <div className="align-center">
                    <div className="flex justify-center p-1">
                        <KakaoLoginButton onKakaoLogin={handleKakaoLogin} />
                    </div>

                    <div className="flex justify-center m-5">
                        <div>
                            <span className="mt-4 text-center text-sm">
                                아직 회원이 아니신가요?{" "}
                            </span>{" "}
                            <Link
                                to="/signup"
                                className="text-sm text-gray-600 underline hover:text-gray-800 font-bold"
                            >
                                회원가입
                            </Link>
                        </div>
                    </div>
                </div>
            </div>
            <Toaster />
        </div>
    );
}
