import { useState, useEffect } from "react";

interface GeoLocation {
    latitude: number;
    longitude: number;
}

/**
 * const location = useGeoLocation();
 * 과 같은 방식으로 호출 시, location 객체에 다음과 같이 위치 정보 저장
 * @returns {
 *     latitude: number,
 *     longitude: number,
 * }
 */

export function useGeoLocation() {
    const [location, setLocation] = useState<GeoLocation | null>(null);

    useEffect(() => {
        if (!navigator.geolocation) {
            console.error("Geolocation을 지원하지 않는 브라우저입니다.");
            return;
        }

        navigator.geolocation.getCurrentPosition(
            (position) => {
                setLocation({
                    latitude: position.coords.latitude,
                    longitude: position.coords.longitude,
                });
            },
            (error) => {
                console.error("위치 정보를 가져올 수 없습니다.", error);
            },
        );
    }, []);

    return location;
}

export function useLiveGeoLocation(updateInterval = 5000) {
    const initialLocation = useGeoLocation();
    const [location, setLocation] = useState<GeoLocation | null>(
        initialLocation,
    );
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        if (!navigator.geolocation) {
            setError("Geolocation을 지원하지 않는 브라우저입니다.");
            return;
        }

        console.log(location);

        const updateLocation = () => {
            navigator.geolocation.getCurrentPosition(
                (position) => {
                    setLocation({
                        latitude: position.coords.latitude,
                        longitude: position.coords.longitude,
                    });
                },
                (err) => {
                    setError("위치 정보를 가져오는 데 실패했습니다.");
                },
            );
        };

        // 초기 위치 설정 (useGeoLocation에서 받은 값 적용)
        if (initialLocation) {
            setLocation(initialLocation);
        }

        // 일정 주기마다 위치 업데이트
        const interval = setInterval(updateLocation, updateInterval);

        return () => clearInterval(interval); // 컴포넌트 언마운트 시 정리
    }, [updateInterval, initialLocation]);

    return { location, error };
}
