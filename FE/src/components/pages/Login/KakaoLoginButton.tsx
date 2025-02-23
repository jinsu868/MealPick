import { Button, ButtonProps } from "@/components/ui/button";
import { cn } from "@/lib/utils";
import { FaComment } from "react-icons/fa";

interface KakaoLoginButtonProps extends ButtonProps {
    onKakaoLogin?: () => void;
}

const KakaoLoginButton: React.FC<KakaoLoginButtonProps> = ({
    className,
    onKakaoLogin,
    ...props
}) => {
    return (
        <Button
            className={cn(
                "bg-[#fee500] text-black hover:bg-[#ffe23f] w-[300px] h-[45px]",
                className,
            )}
            onClick={onKakaoLogin}
            {...props}
        >
            <FaComment />
            카카오 로그인
        </Button>
    );
};

export default KakaoLoginButton;
