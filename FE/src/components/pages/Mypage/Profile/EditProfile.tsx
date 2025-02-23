import { useState, useEffect } from "react";
import { updateMemberProfile, getMyProfile } from "@/api/user";
import { MemberDetail } from "@/types/MemberDetail";
import { FaUserCircle } from "react-icons/fa";
import { Button } from "@/components/ui/button";
// import { useDispatch } from "react-redux";
import Darktoggle from "./Darktoggle"; // ✅ 다크 모드 토글 추가
import {
    Dialog,
    DialogClose,
    DialogContent,
    DialogFooter,
    DialogHeader,
    DialogTitle,
} from "@/components/ui/dialog";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";

interface ProfileEditProps {
    profile: MemberDetail;
    setProfile: (profile: MemberDetail) => void;
    onClose: () => void;
}

// ✅ 프로필 이미지 URL 처리 함수
const getImageUrl = (path?: string | null): string => {
    return path ? `https://i12a803.p.ssafy.io/${path}` : "";
};

export default function ProfileEdit({
    profile,
    setProfile,
    onClose,
}: ProfileEditProps) {
    const [nickname, setNickname] = useState<string>(profile.nickname);
    const [uploadedFile, setUploadedFile] = useState<File | null>(null);
    const [profileImage, setProfileImage] = useState<string>(
        getImageUrl(profile.profileImage),
    );
    const [loading, setLoading] = useState<boolean>(false);
    // const dispatch = useDispatch();

    // ✅ 프로필 데이터 변경 감지 후 이미지 URL 업데이트
    useEffect(() => {
        setProfileImage(getImageUrl(profile.profileImage));
    }, [profile.profileImage]);

    // ✅ 프로필 사진 변경 시 미리보기 즉시 적용
    const handleImageUpload = (event: React.ChangeEvent<HTMLInputElement>) => {
        const file = event.target.files?.[0];
        if (file) {
            setUploadedFile(file);
            setProfileImage(URL.createObjectURL(file));
        }
    };

    const handleSave = async () => {
        setLoading(true);
        try {
            await updateMemberProfile({ nickname }, uploadedFile || undefined);

            console.log("✅ 프로필 업데이트 요청 완료, 최신 데이터 요청 중...");
            await new Promise((resolve) => setTimeout(resolve, 500)); // 서버 반영 대기

            const updatedProfile = await getMyProfile();
            console.log("✅ 서버에서 가져온 최신 프로필:", updatedProfile);

            if (updatedProfile) {
                setProfile(updatedProfile);
                setProfileImage(getImageUrl(updatedProfile.profileImage)); // ✅ 강제 업데이트
            }

            onClose();
        } catch (error) {
            console.error("❌ 프로필 수정 실패:", error);
        } finally {
            setLoading(false);
        }
    };

    return (
        <Dialog open onOpenChange={onClose}>
            <DialogContent className="bg-white text-black dark:bg-gray-700 dark:text-gray-200 sm:max-w-[360px] p-4">
                <DialogHeader className="text-center">
                    <DialogTitle className="text-lg font-semibold text-gray-800 dark:text-gray-200">
                        프로필 수정
                    </DialogTitle>
                </DialogHeader>

                {/* ✅ 프로필 사진 변경 */}
                <div className="flex flex-col items-center mt-4">
                    <label
                        htmlFor="profile-upload"
                        className="relative w-20 h-20 rounded-full bg-gray-100 flex items-center justify-center border border-gray-300 cursor-pointer"
                    >
                        {profileImage ? (
                            <img
                                src={profileImage}
                                onError={(e) => {
                                    console.warn(
                                        "⚠️ 이미지 로드 실패, 기본 아이콘 적용",
                                    );
                                    setProfileImage("");
                                    e.currentTarget.src = "";
                                }}
                                alt="Profile"
                                className="w-full h-full rounded-full object-cover"
                            />
                        ) : (
                            <FaUserCircle className="text-gray-300 w-20 h-20" />
                        )}
                    </label>
                    <input
                        type="file"
                        id="profile-upload"
                        accept="image/*"
                        className="hidden"
                        onChange={handleImageUpload}
                    />
                    <p className="text-xs text-gray-500 mt-2">
                        프로필 사진을 변경하려면 클릭하세요
                    </p>
                </div>

                {/* ✅ 닉네임 변경 */}
                <div className="mt-6">
                    <Label
                        htmlFor="nickname"
                        className="text-gray-700 dark:text-gray-200 text-sm"
                    >
                        닉네임
                    </Label>
                    <Input
                        id="nickname"
                        value={nickname}
                        className="border-black dark:border-gray-200 mt-1 w-full text-sm"
                        type="text"
                        onChange={(e) => setNickname(e.target.value)}
                    />
                </div>

                {/* ✅ 다크 모드 설정 (푸시 알림 제거) */}
                <div className="mt-6 flex justify-around items-center">
                    <span className="text-sm text-gray-800 dark:text-gray-200">
                        다크 모드
                    </span>
                    <Darktoggle />{" "}
                    {/* ✅ 헤더 디자인과 동일한 다크 모드 토글 적용 */}
                </div>

                {/* ✅ 저장 버튼 */}
                <DialogFooter className="mt-6">
                    <DialogClose asChild>
                        <Button
                            type="button"
                            onClick={handleSave}
                            disabled={loading}
                        >
                            {loading ? "저장 중..." : "저장하기"}
                        </Button>
                    </DialogClose>
                </DialogFooter>
            </DialogContent>
        </Dialog>
    );
}
