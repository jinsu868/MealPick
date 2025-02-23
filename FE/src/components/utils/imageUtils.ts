// Data URL → Blob 변환 (업로드를 위해 필요)
export const dataURLtoBlob = (dataURL: string) => {
    try {
        if (!dataURL.includes(",")) {
            throw new Error("Invalid Data URL format");
        }

        const byteString = atob(dataURL.split(",")[1]);
        const mimeString = dataURL.split(",")[0].split(":")[1].split(";")[0];

        const arrayBuffer = new ArrayBuffer(byteString.length);
        const intArray = new Uint8Array(arrayBuffer);

        for (let i = 0; i < byteString.length; i++) {
            intArray[i] = byteString.charCodeAt(i);
        }

        return new Blob([arrayBuffer], { type: mimeString });
    } catch (error) {
        console.error("dataURLtoBlob 변환 오류:", error);
        return null; // ✅ 변환 실패 시 `null` 반환
    }
};

// 다중 이미지 업로드 함수
export const uploadPhotos = async (photos: string[]) => {
    if (photos.length === 0) {
        alert("업로드할 사진이 없습니다.");
        return false;
    }

    const formData = new FormData();
    photos.forEach((photo, index) => {
        const blob = dataURLtoBlob(photo);
        if (!blob) {
            console.error(`Photo ${index} 변환 실패`);
            return;
        }
        formData.append(`photo${index}`, blob, `photo${index}.png`);
    });

    try {
        const response = await fetch("/api/upload", {
            method: "POST",
            body: formData,
        });

        if (response.ok) {
            alert("업로드 완료!");
            return true;
        } else {
            const errorMessage = await response.text();
            console.error("업로드 실패:", errorMessage);
            alert("업로드 실패. 서버 응답을 확인하세요.");
            return false;
        }
    } catch (error) {
        console.error("업로드 오류:", error);
        alert("업로드 중 오류가 발생했습니다.");
        return false;
    }
};
