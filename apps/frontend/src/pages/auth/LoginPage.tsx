import React, { useState } from "react";
import AuthLayout from "./AuthLayout";
import Input from "../../components/ui/Input";
import Button from "../../components/ui/Button";
import { Link, useNavigate } from "react-router-dom";
import axios from "axios";

const LoginPage: React.FC = () => {
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState<string | null>(null);
    const navigate = useNavigate();

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setError(null);

        try {
            const response = await axios.post(
                'http://localhost:8080/api/users/login', {
                    email: email,
                    password: password,
                }
            );

            // 로그인 성공 시
            console.log(response.headers)
            const authHeader = response.headers['authorization'];
            if (authHeader && authHeader.startsWith('Bearer ')) {
                const token = authHeader.substring(7);
                localStorage.setItem('accessToken', token);

                console.log('Login Successful: ', response.data);
                alert("로그인 성공");
                navigate('/');
            } else {
                setError("로그인에 성공했지만 인증 토큰을 받지 못했습니다.");
            }
        } catch (err) {
            // 로그인 실패 시
            console.error('Login failed: ', err);

            if (axios.isAxiosError(err) && err.response) {
                setError(err.response.data.message || '로그인에 실패했습니다.');
            } else {
                setError('네트워크 오류 또는 알 수 없는 오류가 발생했습니다.');
            }
        }
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
                {/* 에러 메시지 표시 */}
                {error && <p className="text-red-500 text-sm mb-4">{error}</p> }
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