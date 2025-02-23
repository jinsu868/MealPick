import { createChatRoom } from "@/api/chat";
import { Button } from "@/components/ui/button";
import { FiMessageCircle } from "react-icons/fi";
import { useNavigate } from "react-router-dom";

interface StartChatButtonProps {
    name: string | null;
    memberId: number;
}

function StartChatButton({ name, memberId }: StartChatButtonProps) {
    console.log(name, memberId);
    const navigate = useNavigate();
    const handleStartChat = async (name: string, id: number) => {
        try {
            console.log(name, id);
            const response = await createChatRoom(name, id);
            console.log(`${name}(${id})와의 채팅방`, response);
            const roomId = response.id;
            navigate(`/chat/${roomId}`);
        } catch (error) {
            console.log(error);
        }
    };
    return (
        <Button
            className="flex items-center gap-2 bg-blue-500 text-white px-4 py-2 rounded-full shadow-none"
            onClick={() => handleStartChat(name ? name : "채팅", memberId)}
        >
            <FiMessageCircle size={16} />
            메시지
        </Button>
    );
}

export default StartChatButton;
