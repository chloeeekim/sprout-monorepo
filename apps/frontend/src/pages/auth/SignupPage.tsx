import React, { useState } from "react";
import AuthLayout from "./AuthLayout";
import Input from "../../components/ui/Input";
import Button from "../../components/ui/Button";
import { Link } from "react-router-dom";

const SignupPage: React.FC = () => {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [confirmPassword, setConfirmPassword] = useState('');

    const handleSubmit = (e: React.FormEvent) => {
        e.preventDefault();
        if (password !== confirmPassword) {
            alert('비밀번호가 일치하지 않습니다.');
            return;
        }
        console.log('Signup attempt: ', {email, password});
        // TODO implement actual signup logic
    };

    return (
        <AuthLayout title="회원가입">
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
                    minLength={8}
                    required
                />
                <Input
                    id="confirmPassword"
                    label="비밀번호 확인"
                    type="password"
                    placeholder="비밀번호를 다시 입력하세요"
                    value={confirmPassword}
                    onChange={(e) => setConfirmPassword(e.target.value)}
                    required
                />
                <Button type="submit" className="w-full mt-4">
                    회원가입
                </Button>
            </form>
            <p className="text-center text-sm text-gray-600 mt-4">
                이미 계정이 있으신가요?{' '}
                <Link to="/login" className="text-sprout-accent hover:underline">
                    로그인
                </Link>
            </p>
        </AuthLayout>
    );
};

export default SignupPage;