import { useLiveGeoLocation } from "@/hooks/useGeoLocation";
interface GeoLocation {
    latitude: number;
    longitude: number;
}

function Map() {
    const { location, error } = useLiveGeoLocation(15000); // 5초마다 위치 갱신

    return (
        <div>
            <h1>실시간 위치 업데이트</h1>
            {error && <p style={{ color: "red" }}>{error}</p>}
            {location ? (
                <p>
                    현재 위치: {location.latitude}, {location.longitude}
                </p>
            ) : (
                <p>위치 정보를 가져오는 중...</p>
            )}
        </div>
    );
}

export default Map;
