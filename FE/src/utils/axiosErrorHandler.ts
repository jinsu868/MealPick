import { AxiosError } from "axios";

export function handleAxiosError(
    error: unknown,
    defaultMessage: string,
): never {
    const message =
        error instanceof AxiosError
            ? error.response?.data || error.message
            : defaultMessage;

    console.error(defaultMessage, message);
    throw message;
}
