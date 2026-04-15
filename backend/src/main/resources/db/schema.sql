-- =============================================================
-- whereIsMyHouse Schema
-- =============================================================

-- 사용자 (기존)
CREATE TABLE IF NOT EXISTS users (
    id         BIGSERIAL    PRIMARY KEY,
    username   VARCHAR(50)  NOT NULL UNIQUE,
    email      VARCHAR(100) NOT NULL UNIQUE,
    password   VARCHAR(255) NOT NULL,
    created_at TIMESTAMP    NOT NULL DEFAULT NOW()
);

-- 사용자 청약/재무 프로필
CREATE TABLE IF NOT EXISTS user_profile (
    id                  BIGSERIAL PRIMARY KEY,
    user_id             BIGINT    NOT NULL UNIQUE REFERENCES users(id) ON DELETE CASCADE,
    annual_income       INT,                        -- 연봉 (만원)
    total_assets        INT,                        -- 총 재산 (만원)
    is_house_owner      BOOLEAN   NOT NULL DEFAULT FALSE,
    house_count         SMALLINT  NOT NULL DEFAULT 0,
    family_count        SMALLINT  NOT NULL DEFAULT 0,
    is_married          BOOLEAN   NOT NULL DEFAULT FALSE,
    subscription_period SMALLINT,                   -- 청약통장 가입기간 (개월)
    no_house_period     SMALLINT,                   -- 무주택 기간 (개월)
    updated_at          TIMESTAMP NOT NULL DEFAULT NOW()
);

-- 아파트 단지 마스터
CREATE TABLE IF NOT EXISTS apt_complex (
    id               BIGSERIAL    PRIMARY KEY,
    complex_no       VARCHAR(20)  NOT NULL UNIQUE,  -- API 단지코드
    name             VARCHAR(100) NOT NULL,
    road_addr        VARCHAR(200),
    sido             VARCHAR(20),
    sigungu          VARCHAR(30),
    dong             VARCHAR(30),
    bjdong_code      CHAR(5),                       -- 법정동코드 앞 5자리
    lat              DECIMAL(10, 7),                -- 위도 (카카오 지오코딩)
    lng              DECIMAL(10, 7),                -- 경도 (카카오 지오코딩)
    total_household  INT,
    build_year       SMALLINT,
    created_at       TIMESTAMP    NOT NULL DEFAULT NOW()
);

-- 아파트 매매 실거래가
CREATE TABLE IF NOT EXISTS apt_trade (
    id          BIGSERIAL      PRIMARY KEY,
    complex_id  BIGINT         NOT NULL REFERENCES apt_complex(id),
    deal_year   SMALLINT       NOT NULL,
    deal_month  SMALLINT       NOT NULL,
    deal_day    SMALLINT       NOT NULL,
    area        DECIMAL(6, 2)  NOT NULL,            -- 전용면적 (㎡)
    floor       SMALLINT,
    price       INT            NOT NULL,            -- 거래금액 (만원)
    created_at  TIMESTAMP      NOT NULL DEFAULT NOW()
);

-- 아파트 전월세 실거래가
CREATE TABLE IF NOT EXISTS apt_rent (
    id           BIGSERIAL      PRIMARY KEY,
    complex_id   BIGINT         NOT NULL REFERENCES apt_complex(id),
    rent_type    VARCHAR(5)     NOT NULL CHECK (rent_type IN ('전세', '월세')),
    deal_year    SMALLINT       NOT NULL,
    deal_month   SMALLINT       NOT NULL,
    area         DECIMAL(6, 2)  NOT NULL,           -- 전용면적 (㎡)
    floor        SMALLINT,
    deposit      INT            NOT NULL,           -- 보증금 (만원)
    monthly_rent INT,                               -- 월세 (만원), 전세 시 NULL
    created_at   TIMESTAMP      NOT NULL DEFAULT NOW()
);

-- 공동주택 공시가격
CREATE TABLE IF NOT EXISTS apt_official_price (
    id             BIGSERIAL      PRIMARY KEY,
    complex_id     BIGINT         NOT NULL REFERENCES apt_complex(id),
    year           SMALLINT       NOT NULL,         -- 공시년도
    dong           VARCHAR(10),
    ho             VARCHAR(10),
    area           DECIMAL(6, 2),                  -- 전용면적 (㎡)
    official_price BIGINT         NOT NULL,         -- 공시가격 (원)
    created_at     TIMESTAMP      NOT NULL DEFAULT NOW()
);

-- 청약 분양공고
CREATE TABLE IF NOT EXISTS subscription_announcement (
    id                  BIGSERIAL    PRIMARY KEY,
    announce_no         VARCHAR(20)  NOT NULL UNIQUE,  -- 공고번호
    house_name          VARCHAR(100) NOT NULL,
    house_type          VARCHAR(10)  NOT NULL CHECK (house_type IN ('민영', '국민')),
    supply_addr         VARCHAR(200),
    bjdong_code         CHAR(5),
    lat                 DECIMAL(10, 7),
    lng                 DECIMAL(10, 7),
    total_supply        INT,
    reception_start_dt  DATE,
    reception_end_dt    DATE,
    winner_announce_dt  DATE,
    min_price           INT,                           -- 최저 분양가 (만원)
    max_price           INT,                           -- 최고 분양가 (만원)
    created_at          TIMESTAMP    NOT NULL DEFAULT NOW()
);

-- =============================================================
-- 인덱스
-- =============================================================

CREATE INDEX IF NOT EXISTS idx_apt_complex_bjdong    ON apt_complex (bjdong_code);
CREATE INDEX IF NOT EXISTS idx_apt_trade_complex     ON apt_trade (complex_id);
CREATE INDEX IF NOT EXISTS idx_apt_trade_deal        ON apt_trade (deal_year, deal_month);
CREATE INDEX IF NOT EXISTS idx_apt_rent_complex      ON apt_rent (complex_id);
CREATE INDEX IF NOT EXISTS idx_apt_official_complex  ON apt_official_price (complex_id, year);
CREATE INDEX IF NOT EXISTS idx_subscription_bjdong   ON subscription_announcement (bjdong_code);
CREATE INDEX IF NOT EXISTS idx_subscription_date     ON subscription_announcement (reception_start_dt);
