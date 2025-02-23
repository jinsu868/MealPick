import { useNavigate } from "react-router-dom";
import { zodResolver } from "@hookform/resolvers/zod";
import { useForm } from "react-hook-form";
import { z } from "zod";
import { Button } from "@/components/ui/button";
import {
    Card,
    CardContent,
    CardDescription,
    CardHeader,
    CardTitle,
} from "@/components/ui/card";
import {
    Form,
    FormControl,
    FormDescription,
    FormField,
    FormItem,
    FormLabel,
    FormMessage,
} from "@/components/ui/form";
import { Input } from "@/components/ui/input";
import { basicInfoSchema } from "../../../contexts/SignupContext";
import { signup } from "@/api/auth";
import { useDispatch } from "react-redux";
import { login } from "@/redux/slices/userSlice";

export default function BasicInfo() {
    const navigate = useNavigate();
    const dispatch = useDispatch();

    const form = useForm<z.infer<typeof basicInfoSchema>>({
        resolver: zodResolver(basicInfoSchema),
        mode: "onChange",
        defaultValues: {
            email: "",
            name: "",
            age: 20,
        },
    });

    async function onSubmit(values: z.infer<typeof basicInfoSchema>) {
        try {
            const accessToken = localStorage.getItem("accessToken");
            if (!accessToken) {
                navigate("/login", {
                    state: { error: "로그인이 필요합니다." },
                });
                return;
            }

            const signupData = { ...values };
            const newUser = await signup(signupData);

            dispatch(
                login({
                    id: newUser.id,
                    name: newUser.name,
                    email: newUser.email,
                    nickname: newUser.nickname,
                    profileImage: newUser.profileImage,
                    alias: newUser.alias,
                }),
            );

            navigate("/");
        } catch (error) {
            console.error("회원가입 실패:", error);
        }
    }

    return (
        <div>
            <Card>
                <CardHeader className="text-left">
                    <CardTitle>회원가입</CardTitle>
                    <CardDescription>
                        회원가입에 필요한 정보들을 입력해주세요.
                    </CardDescription>
                </CardHeader>
                <CardContent>
                    <Form {...form}>
                        <div className="">
                            <form
                                onSubmit={form.handleSubmit(onSubmit)}
                                className="flex flex-col space-y-1.5"
                            >
                                <FormField
                                    control={form.control}
                                    name="name"
                                    render={({ field }) => (
                                        <FormItem className="text-left">
                                            <FormLabel className="mx-2">
                                                이름
                                            </FormLabel>
                                            <FormControl>
                                                <Input
                                                    placeholder="이름을 입력하세요."
                                                    {...field}
                                                />
                                            </FormControl>
                                            <FormMessage className="mx-2" />
                                        </FormItem>
                                    )}
                                />
                                <FormField
                                    control={form.control}
                                    name="email"
                                    render={({ field }) => (
                                        <FormItem className="text-left">
                                            <FormLabel className="mx-2">
                                                이메일
                                            </FormLabel>
                                            <FormControl>
                                                <Input
                                                    placeholder="meal@pic.com"
                                                    {...field}
                                                />
                                            </FormControl>
                                            <FormDescription></FormDescription>
                                            <FormMessage className="mx-2" />
                                        </FormItem>
                                    )}
                                />
                                <FormField
                                    control={form.control}
                                    name="age"
                                    render={({ field }) => (
                                        <FormItem className="text-left">
                                            <FormLabel className="mx-2">
                                                나이
                                            </FormLabel>
                                            <FormControl>
                                                <Input
                                                    placeholder="20"
                                                    {...field}
                                                />
                                            </FormControl>
                                            <FormDescription></FormDescription>
                                            <FormMessage className="mx-2" />
                                        </FormItem>
                                    )}
                                />
                                {/* <FormField
                                    control={form.control}
                                    name="password"
                                    render={({ field }) => (
                                        <FormItem className="text-left">
                                            <FormLabel className="mx-2">
                                                비밀번호
                                            </FormLabel>
                                            <FormControl>
                                                <Input
                                                    type="password"
                                                    placeholder="비밀번호를 입력하세요."
                                                    {...field}
                                                />
                                            </FormControl>
                                            <FormDescription></FormDescription>
                                            <FormMessage className="mx-2" />
                                        </FormItem>
                                    )}
                                />
                                <FormField
                                    control={form.control}
                                    name="passwordConfirm"
                                    render={({ field }) => (
                                        <FormItem className="text-left">
                                            <FormLabel className="mx-2">
                                                비밀번호 확인
                                            </FormLabel>
                                            <FormControl>
                                                <Input
                                                    type="password"
                                                    placeholder="비밀번호 확인"
                                                    {...field}
                                                />
                                            </FormControl>
                                            <FormDescription></FormDescription>
                                            <FormMessage className="mx-2" />
                                        </FormItem>
                                    )}
                                /> */}
                                <div className="grid place-items-center">
                                    <Button
                                        className="mt-5 hover:border-black"
                                        type="submit"
                                        style={{ width: "100%" }}
                                    >
                                        다음
                                    </Button>
                                </div>
                            </form>
                        </div>
                    </Form>
                </CardContent>
            </Card>
        </div>
    );
}
