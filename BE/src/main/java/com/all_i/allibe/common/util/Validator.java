package com.all_i.allibe.common.util;


import com.all_i.allibe.common.exception.UnexpectedException;
import com.all_i.allibe.common.exception.ValidException;

public final class Validator {

    private Validator() {

    }

    /**
     * 문자열은 null 또는 공백인지 검사
     * @param input 검증 문자열
     * @param fieldName 예외 메시지에 출력할 필드명
     * @throws ValidException input이 NULL 또는 공백이면
     * */
    public static void notBlank(String input, String fieldName) {
        if (input == null || input.isBlank()){
            throw new ValidException("%s은/는 null 또는 공백이 될 수 없습니다.".formatted(fieldName));
        }
    }

    /**
     * 문자열의 최소 길이를 검증합니다. null은 무시됩니다. 최소길이는 0이하라면 예외를 던집니다.
     *
     * @param input 검증 문자열
     * @param minLength 검증할 문자열의 최소 길이
     * @param fieldName 예외 메시지에 출력할 필드명
     * @throws UnexpectedException maxLength가 0이하라면
     * @throws ValidException input의 길이가 minLength 미만이면
     * */
    public static void minLength(CharSequence input, int minLength, String fieldName) {
        if (minLength <= 0) {
            throw new UnexpectedException("최소 길이는 0 이하일 수 없습니다.");
        }
        if (input == null) {
            return;
        }
        if (input.length() < minLength) {
            throw new ValidException("%s의 길이는 %d글자 이상이어야 합니다.".formatted(fieldName, minLength));
        }
    }

    /**
     * 문자열의 최대 길이를 검증합니다. null은 무시됩니다. 최대길이가 maxlength 초과시 예외를 던집니다.
     *
     * @param input 검증 문자열
     * @param maxLength 검증할 문자열의 최대 길이
     * @param fieldName 예외 메시지에 출력할 필드명
     * @throws UnexpectedException maxLength가 0이하라면
     * @throws ValidException input의 길이가 maxLength 초과라면
     * */
    public static void maxLength(String input, int maxLength, String fieldName) {
        if (maxLength <= 0) {
            throw new UnexpectedException("최대 길이는 0 이하일 수 없습니다.");
        }
        if (input == null) {
            return;
        }
        if (input.length() < maxLength) {
            throw new ValidException("%s의 길이는 %d글자 이상이어야 합니다.".formatted(fieldName, maxLength));
        }
    }
}
