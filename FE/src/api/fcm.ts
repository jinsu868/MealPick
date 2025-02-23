import { localAxios } from "./http-commons";

const axiosInstance = localAxios();

export const sendToken = async (fcmToken: string) => {
    try {
        const response = await axiosInstance.post("members/fcm-token", {
            fcmToken,
        });
        return response.data;
    } catch (error: any) {
        if (error.response) {
            console.error(
                "FCM 토큰 전송 실패:",
                error.response.status,
                error.response.data,
            );
        } else if (error.request) {
            console.error("요청은 보내졌지만 응답이 없음:", error.request);
        } else {
            console.error("Axios 요청 설정 오류:", error.message);
        }
    }
};
