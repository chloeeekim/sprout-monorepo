import React, { useState } from "react";
import AuthLayout from "./AuthLayout";
import Input from "../../components/ui/Input";
import Button from "../../components/ui/Button";
import { Link, useNavigate } from "react-router-dom";
import axios from "axios";

const SignupPage: React.FC = () => {
    const [email, setEmail] = useState('');
    const [name, setName] = useState('');
    const [password, setPassword] = useState('');
    const [confirmPassword, setConfirmPassword] = useState('');
    const [error, setError] = useState<string | null>(null);
    const navigate = useNavigate();

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        if (password !== confirmPassword) {
            alert('비밀번호가 일치하지 않습니다.');
            return;
        }

        try {
            const response = await axios.post(
                'http://localhost:8080/api/users/signup', {
                    email: email,
                    password: password,
                    name: name
                }
            );

            // 회원가입 성공 시
            console.log('Signup successful: ', response.data);
            alert("회원가입이 성공적으로 완료되었습니다. 로그인해주세요");
            navigate('/login');
        } catch (err) {
            console.log('Signup failed: ', err);
            if (axios.isAxiosError(err) && err.response) {
                setError(err.response.data.message || '회원가입에 실패했습니다. 다시 시도해주세요.');
            } else {
                setError('네트워크 오류 또는 알 수 없는 오류가 발생했습니다.');
            }
        }
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
                    id="name"
                    label="이름"
                    type="name"
                    placeholder="이름을 입력하세요"
                    value={name}
                    onChange={(e) => setName(e.target.value)}
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
                {/* 에러 메시지 표시 */}
                {error && <p className="text-red-500 text-sm mb-4">{error}</p> }
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