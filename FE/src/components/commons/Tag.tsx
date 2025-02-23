import { motion } from "framer-motion";
import { useNavigate } from "react-router-dom";

interface TagProps {
    colorClass: string;
    tagName: string;
    animated?: boolean;
}

const Tag: React.FC<TagProps> = ({ colorClass, tagName, animated = false }) => {
    const navigate = useNavigate();

    const handleClick = () => {
        navigate(`/food-detail?tagName=${tagName}`);
    };

    const className = `px-2 py-1 rounded-full text-xs ${colorClass} cursor-pointer`;

    return animated ? (
        <motion.div
            className={className}
            initial={{ opacity: 0, y: -10 }}
            animate={{ opacity: 1, y: 0 }}
            transition={{ duration: 0.1 }}
            onClick={handleClick}
        >
            {tagName}
        </motion.div>
    ) : (
        <span className={className} onClick={handleClick}>
            {tagName}
        </span>
    );
};

export default Tag;
