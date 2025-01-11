CREATE DATABASE IF NOT EXISTS mockcote_user;

use mockcote_user;

-- user_site_info 테이블 생성
CREATE TABLE user_info (
    id INT AUTO_INCREMENT PRIMARY KEY,       -- 고유 ID
    user_id VARCHAR(255) NOT NULL UNIQUE,     -- 사용자 ID
    pw TEXT NOT NULL,                -- 비밀번호
    handle VARCHAR(255) NOT NULL,       -- 백준 ID
    level INT NOT NULL,
    refresh_token TEXT,                      -- 리프레시 토큰
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP, -- 생성 시간
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP -- 수정 시간
);