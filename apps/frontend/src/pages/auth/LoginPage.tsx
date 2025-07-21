import React, { useState } from "react";
import AuthLayout from "./AuthLayout";
import Input from "../../components/ui/Input";
import Button from "../../components/ui/Button";
import { Link } from "react-router-dom";

const LoginPage: React.FC = () => {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');

    const handleSubmit = (e: React.FormEvent) => {
        e.preventDefault();
        console.log('Login attempt: ', { email, password });
        // TODO implement actual login logic
    };

    return (
        <AuthLayout title="로그인">
            <form onSubmit={handleSubmit}>
                <Input
                    id="email"
                    label="이메일"
                    type="email"
                    placeholder="이메일을 입력하세요"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    required
                />
                <Input
                    id="password"
                    label="비밀번호"
                    type="password"
                    placeholder="비밀번호를 입력하세요"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    required
                />
                <Button type="submit" className="w-full mt-4">
                    로그인
                </Button>
            </form>
            <p className="text-center text-sm text-gray-600 mt-4">
                계정이 없으신가요?{' '}
                <Link to="/signup" className="text-sprout-accent hover:underline">
                    회원가입
                </Link>
            </p>
        </AuthLayout>
    );
};

export default LoginPage;