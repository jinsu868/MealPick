import React, { createContext, useContext, useState } from "react";
import { z } from "zod";

// const passwordRegex =
//     /^(?=.*[a-zA-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,15}$/;

export const basicInfoSchema = z.object({
    email: z.string().email("올바른 이메일을 입력하세요."),
    name: z.string().min(2, {
        message: "최소 2글자를 입력하세요.",
    }),
    age: z.coerce.number().positive("나이는 양수여야 합니다."),
});

export const detailInfoSchema = z.object({
    nickname: z.string({ required_error: "닉네임을 입력하세요" }).min(2, {
        message: "닉네임은 두 글자 이상이어야 합니다.",
    }),
    // age: z.coerce
    //     .number({
    //         required_error: "나이를 입력하세요.",
    //         invalid_type_error: "나이는 숫자형식이어야 합니다.",
    //     })
    //     .positive("올바른 나이를 입력하세요."),
    generation: z
        .string({ required_error: "연령대를 선택하세요." })
        .refine((val) => val === "X" || val === "Y" || val === "Z", {
            message: "연령대를 선택하세요.",
        }),
    gender: z
        .string({ required_error: "성별을 선택하세요." })
        .refine((val) => val === "male" || val === "female", {
            message: "성별을 선택하세요.",
        }),
    tags: z.array(z.string()),
    // description: z.string().default(""),
    city: z
        .string({ required_error: "시/도를 선택하세요." })
        .nonempty({ message: "시/도를 선택하세요." }),
    region: z
        .string({ required_error: "구/군을 선택하세요." })
        .nonempty("구/군을 선택하세요"),
});

type BasicInfo = z.infer<typeof basicInfoSchema>;
type DetailInfo = z.infer<typeof detailInfoSchema>;

interface SignupContextType {
    basicInfo: BasicInfo | null;
    detailInfo: DetailInfo | null;
    setBasicInfo: (info: BasicInfo) => void;
    setDetailInfo: (info: DetailInfo) => void;
}

const SignupContext = createContext<SignupContextType | null>(null);

export const SignupProvider = ({ children }: { children: React.ReactNode }) => {
    const [basicInfo, setBasicInfo] = useState<BasicInfo | null>(null);
    const [detailInfo, setDetailInfo] = useState<DetailInfo | null>(null);

    return (
        <SignupContext.Provider
            value={{ basicInfo, detailInfo, setBasicInfo, setDetailInfo }}
        >
            {children}
        </SignupContext.Provider>
    );
};

export const useSignup = () => {
    const context = useContext(SignupContext);
    if (!context) {
        throw new Error("useSignup must be used within a SignupProvider");
    }
    return context;
};
