import {
    useEffect,
    useLayoutEffect,
    useRef,
    useState,
    useCallback,
} from "react";
import { getPosts } from "@/api/post";
import { toast } from "@/hooks/use-toast";
import { ErrorMessages } from "@/utils/ErrorMessages";
import { PostDetailDefault } from "@/types/PostDetail";
import Post from "@/components/Post/Post";

export default function Content() {
    const [isLoading, setIsLoading] = useState(false);
    const [isRefreshing, setIsRefreshing] = useState(false);
    const [posts, setPosts] = useState<PostDetailDefault[]>(() => {
        const savedPosts = sessionStorage.getItem("posts");
        return savedPosts ? JSON.parse(savedPosts) : [];
    });

    const [nextPageToken, setNextPageToken] = useState<string | null>(() => {
        return sessionStorage.getItem("nextPageToken") || null;
    });

    const observer = useRef<IntersectionObserver | null>(null);
    const lastPostRef = useRef<HTMLDivElement | null>(null);
    const isFetching = useRef(false);
    const startY = useRef(0);

    // `useLayoutEffect` 활용해서 렌더링 전에 스크롤 위치 복원
    useLayoutEffect(() => {
        const savedScrollPosition = sessionStorage.getItem("scrollPosition");
        if (savedScrollPosition) {
            window.scrollTo(0, parseInt(savedScrollPosition, 10));
        }
    }, []);

    // 스크롤할 때마다 현재 스크롤 위치 저장
    useEffect(() => {
        const saveScrollPosition = () => {
            sessionStorage.setItem("scrollPosition", window.scrollY.toString());
        };

        window.addEventListener("scroll", saveScrollPosition);
        return () => {
            window.removeEventListener("scroll", saveScrollPosition);
        };
    }, []);

    const loadPosts = useCallback(
        async (pageToken?: string) => {
            if (isFetching.current || pageToken === null) return;

            isFetching.current = true;
            setIsLoading(true);
            console.log("Fetching posts with pageToken:", pageToken);

            try {
                const data = await getPosts(pageToken);
                const newPosts = [...posts, ...data.content];

                setPosts(newPosts);
                setNextPageToken(data.pageToken ?? null);

                sessionStorage.setItem("posts", JSON.stringify(newPosts));
                sessionStorage.setItem("nextPageToken", data.pageToken ?? "");
            } catch (error) {
                console.error("게시글 요청 중 에러: ", error);
                toast({ description: ErrorMessages.DEFAULT_ERROR });
            } finally {
                setIsLoading(false);
                isFetching.current = false;
            }
        },
        [posts],
    );

    const refreshPosts = useCallback(async () => {
        setIsRefreshing(true);
        try {
            const data = await getPosts();
            setPosts(data.content);
            setNextPageToken(data.pageToken ?? null);

            sessionStorage.setItem("posts", JSON.stringify(data.content));
            sessionStorage.setItem("nextPageToken", data.pageToken ?? "");
        } catch (error) {
            console.error("게시글 요청 중 에러: ", error);
            toast({ description: ErrorMessages.DEFAULT_ERROR });
        } finally {
            setIsRefreshing(false);
        }
    }, []);

    useEffect(() => {
        if (posts.length === 0) {
            loadPosts(undefined);
        }
    }, []);

    useEffect(() => {
        if (!nextPageToken) return;

        if (observer.current) observer.current.disconnect();

        observer.current = new IntersectionObserver(
            (entries) => {
                if (entries[0].isIntersecting && nextPageToken && !isLoading) {
                    loadPosts(nextPageToken);
                }
            },
            { threshold: 1 },
        );

        if (lastPostRef.current) {
            observer.current.observe(lastPostRef.current);
        }

        return () => {
            if (observer.current) observer.current.disconnect();
        };
    }, [nextPageToken]);

    useEffect(() => {
        const handleTouchStart = (e: TouchEvent) => {
            startY.current = e.touches[0].clientY;
        };

        const handleTouchMove = (e: TouchEvent) => {
            const currentY = e.touches[0].clientY;
            if (currentY > startY.current && window.scrollY === 0) {
                setIsRefreshing(true);
            }
        };

        const handleTouchEnd = () => {
            if (isRefreshing) {
                refreshPosts();
            }
        };

        window.addEventListener("touchstart", handleTouchStart);
        window.addEventListener("touchmove", handleTouchMove);
        window.addEventListener("touchend", handleTouchEnd);

        return () => {
            window.removeEventListener("touchstart", handleTouchStart);
            window.removeEventListener("touchmove", handleTouchMove);
            window.removeEventListener("touchend", handleTouchEnd);
        };
    }, [isRefreshing, refreshPosts]);

    useEffect(() => {
        const handleKeyDown = (event: KeyboardEvent) => {
            if (event.key === "F5" || event.keyCode === 116) {
                event.preventDefault();
                sessionStorage.clear();
                window.location.reload();
            }
        };

        window.addEventListener("keydown", handleKeyDown);
        return () => {
            window.removeEventListener("keydown", handleKeyDown);
        };
    }, []);

    return (
        <div className="pt-[52px] pb-[80px] w-screen max-w-[600px]">
            {isRefreshing && <p>Refreshing...</p>}
            {isLoading && posts.length === 0 ? (
                <p>Loading Posts...</p>
            ) : posts.length === 0 ? (
                <p>No posts yet.</p>
            ) : (
                posts.map((post, idx) => (
                    <Post
                        key={idx}
                        {...post}
                        ref={idx === posts.length - 1 ? lastPostRef : null}
                    />
                ))
            )}
            {isLoading && posts.length > 0 && <p>Loading more posts...</p>}
        </div>
    );
}
