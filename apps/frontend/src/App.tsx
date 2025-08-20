import React from "react";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";

import LoginPage from "./pages/auth/LoginPage";
import SignupPage from "./pages/auth/SignupPage";
import NoteListPage from "./pages/note/NoteListPage";
import NoteCreatePage from "./pages/note/NoteCreatePage";
import NoteDetailPage from "./pages/note/NoteDetailPage";
import NoteEditPage from "./pages/note/NoteEditPage";
import NoteListByTagPage from "./pages/note/NoteListByTagPage";
import NotePage from "./pages/note/NotePage";

// TODO 메인 페이지
const HomePage: React.FC = () => {
    return (
        <div className="min-h-screen flex items-center justify-center bg-sprout-background text-sprout-text">
            <h1 className="text-4xl font-bold">Welcome to Sprout!</h1>
            <p className="mt-4">
                <a href="/login" className="text-sprout-accent hover:underline mr-4">로그인</a>
                <a href="/signup" className="text-sprout-accent hover:underline">회원가입</a>
            </p>
        </div>
    );
};

function App() {
    return (
        <Router>
            <Routes>
                {/* 홈페이지 */}
                <Route path="/" element={<HomePage />} />

                {/* 로그인 페이지 */}
                <Route path="/login" element={<LoginPage />} />

                {/* 회원가입 페이지 */}
                <Route path="/signup" element={<SignupPage />} />

                {/* 노트 리스트 페이지 */}
                <Route path="/notes" element={<NoteListPage />} />

                {/* 새 노트 작성 페이지 */}
                <Route path="/notes/new" element={<NoteCreatePage />} />

                {/* 노트 상세 페이지 */}
                {/*<Route path="/notes/:id" element={<NoteDetailPage />} />*/}
                <Route path="/notes/:id" element={<NotePage />} />

                {/* 노트 수정 페이지 */}
                <Route path="/notes/:id/edit" element={<NoteEditPage />} />

                {/* 태그별 노트 리스트 페이지 */}
                <Route path="/notes/tags/:tagName" element={<NoteListByTagPage />} />
            </Routes>
        </Router>
    );
}

export default App;
