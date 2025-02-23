import { useEffect, useState } from "react";

export function useKakaoAddress(latitude: number, longitude: number) {
    const [address, setAddress] = useState<string | null>(null);

    useEffect(() => {
        if (typeof window === "undefined" || !window.kakao) {
            console.warn("Kakao Maps API가 아직 로드되지 않았습니다.");
            return;
        }

        window.kakao.maps.load(() => {
            console.log("Kakao Maps API가 로드됨");

            const geocoder = new window.kakao.maps.services.Geocoder();
            geocoder.coord2Address(
                longitude,
                latitude,
                (result: any, status: any) => {
                    if (
                        status === window.kakao.maps.services.Status.OK &&
                        result.length > 0
                    ) {
                        const fullAddress = result[0].address.address_name;
                        const addressParts = fullAddress.split(" ");
                        setAddress(`${addressParts[0]} ${addressParts[1]}`);
                    }
                },
            );
        });
    }, [latitude, longitude]);

    return address;
}

export function useKakaoPlaces() {
    const { kakao } = window;
    const [places, setPlaces] =
        useState<kakao.maps.services.PlacesSearchResult>([]);

    const searchPlaces = (keyword: string) => {
        if (!keyword.trim()) {
            alert("키워드를 입력해주세요!");
            return;
        }
        const ps = new kakao.maps.services.Places();
        ps.keywordSearch(keyword, (data, status) => {
            if (status === kakao.maps.services.Status.OK) {
                setPlaces(data);
            } else {
                alert("검색 결과가 없습니다.");
                setPlaces([]);
            }
        });
    };

    return { places, searchPlaces };
}
