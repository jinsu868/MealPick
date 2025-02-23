import { zodResolver } from "@hookform/resolvers/zod";
import { useForm } from "react-hook-form";
import { z } from "zod";
import { useNavigate } from "react-router-dom";
import { useSignup } from "../../../contexts/SignupContext";
// import axios from "axios";
import { regionData } from "@/lib/regionData";
import { Button } from "@/components/ui/button";
import {
    Card,
    CardContent,
    CardDescription,
    // CardFooter,
    CardHeader,
    CardTitle,
} from "@/components/ui/card";
import {
    Select,
    SelectContent,
    SelectGroup,
    SelectItem,
    SelectLabel,
    SelectTrigger,
    SelectValue,
} from "@/components/ui/select";

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
import { ToggleGroup, ToggleGroupItem } from "@/components/ui/toggle-group";
import { detailInfoSchema } from "../../../contexts/SignupContext";
import { useEffect } from "react";
import { allTags } from "@/lib/tagData";
import { Toggle } from "@/components/ui/toggle";

export default function DetailInfo() {
    const navigate = useNavigate();
    const { basicInfo, setDetailInfo } = useSignup();

    const form = useForm<z.infer<typeof detailInfoSchema>>({
        resolver: zodResolver(detailInfoSchema),
        defaultValues: {
            // 다른 필드들(nickname, gender...) 생략
            tags: [],
        },
    });

    const cityValue = form.watch("city");
    const districtList = cityValue ? regionData[cityValue] || [] : [];

    useEffect(() => {
        if (!basicInfo) {
            navigate("/signup");
        }
    }, [basicInfo, navigate]);

    // axios 요청
    async function onSubmit(values: z.infer<typeof detailInfoSchema>) {
        try {
            setDetailInfo(values);

            if (!basicInfo) {
                navigate("/signup");
                return;
            }

            const { ...rest } = values;

            const signupData = {
                ...basicInfo,
                ...rest,
            };

            console.log(signupData);

            // const response = await axios.post("/api/signup", signupData);
            // if (response.status === 200) {
            //     navigate("/signup/success");
            // }
        } catch (error) {
            console.error("회원가입 실패:", error);
        }
    }

    return (
        <div>
            <Card>
                <CardHeader className="text-left">
                    <CardTitle>회원가입</CardTitle>
                    <CardDescription>상세 정보를 입력하세요.</CardDescription>
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
                                    name="nickname"
                                    render={({ field }) => (
                                        <FormItem className="text-left">
                                            <FormLabel className="mx-2">
                                                닉네임
                                            </FormLabel>
                                            <FormControl>
                                                <Input
                                                    placeholder="닉네임을 입력하세요."
                                                    {...field}
                                                />
                                            </FormControl>
                                            <FormMessage className="mx-2" />
                                        </FormItem>
                                    )}
                                />
                                <FormField
                                    control={form.control}
                                    name="gender"
                                    render={({ field }) => (
                                        <FormItem className="text-left">
                                            <FormLabel className="mx-2">
                                                성별
                                            </FormLabel>
                                            <FormControl>
                                                <ToggleGroup
                                                    type="single"
                                                    onValueChange={
                                                        field.onChange
                                                    }
                                                    className="flex w-full gap-4"
                                                    variant="outline"
                                                >
                                                    <ToggleGroupItem
                                                        value="male"
                                                        className="
                                                        flex-1 
                                                        text-sm 
                                                        rounded
                                                        data-[state=on]:bg-black data-[state=on]:text-white hover:border-black"
                                                    >
                                                        남성
                                                    </ToggleGroupItem>
                                                    <ToggleGroupItem
                                                        value="female"
                                                        className="
                                                        flex-1 
                                                        text-sm 
                                                        rounded 
                                                        data-[state=on]:bg-black data-[state=on]:text-white 
                                                        hover:border-black"
                                                    >
                                                        여성
                                                    </ToggleGroupItem>
                                                </ToggleGroup>
                                            </FormControl>
                                            <FormDescription>
                                                {/* This is your public display
                                            name. */}
                                            </FormDescription>
                                            <FormMessage className="mx-2" />
                                        </FormItem>
                                    )}
                                />
                                <FormField
                                    control={form.control}
                                    // name="age"
                                    name="generation"
                                    render={({ field }) => (
                                        <FormItem className="text-left">
                                            <FormLabel className="mx-2">
                                                연령대
                                            </FormLabel>
                                            <FormControl>
                                                <Select
                                                    onValueChange={
                                                        field.onChange
                                                    }
                                                >
                                                    <SelectTrigger className="w-full hover:border-black">
                                                        <SelectValue placeholder="연령대를 선택하세요" />
                                                    </SelectTrigger>
                                                    <SelectContent>
                                                        <SelectGroup>
                                                            <SelectLabel>
                                                                연령대
                                                            </SelectLabel>
                                                            <SelectItem value="X">
                                                                X
                                                            </SelectItem>
                                                            <SelectItem value="Y">
                                                                Y
                                                            </SelectItem>
                                                            <SelectItem value="Z">
                                                                Z
                                                            </SelectItem>
                                                        </SelectGroup>
                                                    </SelectContent>
                                                </Select>
                                            </FormControl>
                                            <FormDescription>
                                                {/* This is your public display
                                            name. */}
                                            </FormDescription>
                                            <FormMessage className="mx-2" />
                                        </FormItem>
                                    )}
                                />
                                {/* <FormField
                                    control={form.control}
                                    name="description"
                                    render={({ field }) => (
                                        <FormItem className="text-left">
                                            <FormLabel className="mx-2">
                                                소개
                                            </FormLabel>
                                            <FormControl>
                                                <Input
                                                    placeholder="나에 대한 소개를 작성하세요."
                                                    {...field}
                                                />
                                            </FormControl>
                                            <FormMessage className="mx-2" />
                                        </FormItem>
                                    )}
                                /> */}
                                <div className="flex gap-4">
                                    {/* 시/도 (city) */}
                                    <FormField
                                        control={form.control}
                                        name="city"
                                        render={({ field }) => (
                                            <FormItem className="text-left flex-1">
                                                <FormLabel className="mx-2">
                                                    시/도
                                                </FormLabel>
                                                <FormControl>
                                                    <Select
                                                        onValueChange={(
                                                            selectedCity,
                                                        ) => {
                                                            field.onChange(
                                                                selectedCity,
                                                            );
                                                            // region 초기화
                                                            form.setValue(
                                                                "region",
                                                                "",
                                                            );
                                                        }}
                                                        value={field.value}
                                                    >
                                                        <SelectTrigger className="w-full hover:border-black">
                                                            <SelectValue placeholder="시/도 선택" />
                                                        </SelectTrigger>
                                                        <SelectContent>
                                                            {Object.keys(
                                                                regionData,
                                                            ).map(
                                                                (cityName) => (
                                                                    <SelectItem
                                                                        key={
                                                                            cityName
                                                                        }
                                                                        value={
                                                                            cityName
                                                                        }
                                                                    >
                                                                        {
                                                                            cityName
                                                                        }
                                                                    </SelectItem>
                                                                ),
                                                            )}
                                                        </SelectContent>
                                                    </Select>
                                                </FormControl>
                                                <FormDescription>
                                                    {/* This is your public display
                                            name. */}
                                                </FormDescription>
                                                <FormMessage className="mx-2" />
                                            </FormItem>
                                        )}
                                    />

                                    {/* 구/군 (region) */}
                                    <FormField
                                        control={form.control}
                                        name="region"
                                        render={({ field }) => (
                                            <FormItem className="text-left flex-1">
                                                <FormLabel className="mx-2">
                                                    구/군
                                                </FormLabel>
                                                <FormControl>
                                                    <Select
                                                        onValueChange={
                                                            field.onChange
                                                        }
                                                        value={field.value}
                                                        disabled={!cityValue}
                                                    >
                                                        <SelectTrigger className="w-full hover:border-black">
                                                            <SelectValue placeholder="구/군 선택" />
                                                        </SelectTrigger>
                                                        <SelectContent>
                                                            {districtList.map(
                                                                (dist) => (
                                                                    <SelectItem
                                                                        key={
                                                                            dist.value
                                                                        }
                                                                        value={
                                                                            dist.value
                                                                        }
                                                                    >
                                                                        {
                                                                            dist.label
                                                                        }
                                                                    </SelectItem>
                                                                ),
                                                            )}
                                                        </SelectContent>
                                                    </Select>
                                                </FormControl>
                                                <FormDescription>
                                                    {/* This is your public display
                                            name. */}
                                                </FormDescription>
                                                <FormMessage className="mx-2" />
                                            </FormItem>
                                        )}
                                    />
                                </div>
                                <FormField
                                    control={form.control}
                                    name="tags"
                                    render={({ field }) => (
                                        <FormItem className="text-left">
                                            <FormLabel className="mx-2">
                                                관심 태그
                                            </FormLabel>
                                            <FormControl>
                                                <div className="flex flex-wrap gap-2">
                                                    {allTags.map((tag) => {
                                                        // 이미 선택된 태그인지 체크
                                                        const isSelected =
                                                            field.value.includes(
                                                                tag,
                                                            );

                                                        return (
                                                            <Toggle
                                                                variant="outline"
                                                                key={tag}
                                                                // pressed: 토글 on/off 상태
                                                                pressed={
                                                                    isSelected
                                                                }
                                                                onPressedChange={(
                                                                    pressed,
                                                                ) => {
                                                                    if (
                                                                        pressed
                                                                    ) {
                                                                        // on -> 배열에 태그 추가
                                                                        field.onChange(
                                                                            [
                                                                                ...field.value,
                                                                                tag,
                                                                            ],
                                                                        );
                                                                    } else {
                                                                        // off -> 배열에서 태그 제거
                                                                        field.onChange(
                                                                            field.value.filter(
                                                                                (
                                                                                    t,
                                                                                ) =>
                                                                                    t !==
                                                                                    tag,
                                                                            ),
                                                                        );
                                                                    }
                                                                }}
                                                                className="px-4 py-2 text-sm font-medium rounded data-[state=on]:bg-black data-[state=on]:text-white hover:border-black"
                                                            >
                                                                {/* 버튼 안의 텍스트 + 'x' 표시 (on일 때만) */}
                                                                {tag}{" "}
                                                            </Toggle>
                                                        );
                                                    })}
                                                </div>
                                            </FormControl>
                                            <FormDescription>
                                                {/* This is your public display
                                            name. */}
                                            </FormDescription>
                                            <FormMessage className="mx-2" />
                                        </FormItem>
                                    )}
                                />
                                <div className="grid place-items-center">
                                    <Button
                                        className="mt-5 hover:border-black"
                                        type="submit"
                                        style={{ width: "100%" }}
                                    >
                                        가입하기
                                    </Button>
                                </div>
                            </form>
                        </div>
                    </Form>
                </CardContent>
                {/* <CardFooter /> */}
            </Card>
        </div>
    );
}
