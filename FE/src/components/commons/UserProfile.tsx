import { useEffect, useState } from "react";
import { AiOutlineUser } from "react-icons/ai";
import { Link } from "react-router-dom";

interface UserProfileProps {
    id: number;
    imageUrl: string;
    size: number;
}

export default function UserProfile({ id, imageUrl, size }: UserProfileProps) {
    const [imgError, setImgError] = useState(false);

    useEffect(() => {
        setImgError(!imageUrl || imageUrl.length === 0 ? true : false);
    }, [imageUrl]);

    return (
        <Link
            to={`/user/${id}`}
            className="bg-gray-300 rounded-full flex items-center justify-center overflow-hidden"
            style={{ width: size, height: size }}
        >
            {imgError || !imageUrl || imageUrl.length === 0 ? (
                <AiOutlineUser
                    style={{ width: size, height: size, color: "gray" }}
                />
            ) : (
                <img
                    src={imageUrl}
                    alt="Profile"
                    className="w-full h-full object-cover"
                    onError={() => setImgError(true)}
                />
            )}
        </Link>
    );
}
