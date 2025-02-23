import { Outlet } from "react-router-dom";
import { SignupProvider } from "@/contexts/SignupContext";

export default function SignUp() {
    return (
        <SignupProvider>
            <Outlet />
        </SignupProvider>
    );
}
