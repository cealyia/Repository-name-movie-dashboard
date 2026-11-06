-- ============================================================
-- 电影数据分析看板 - 建表脚本（MySQL 8.0）
-- 数据集：MovieLens ml-latest-small
-- 执行顺序：01_schema.sql -> 02_dim_movie.sql -> 03_fact_rating.sql -> 04_fact_tag.sql
-- ============================================================
CREATE DATABASE IF NOT EXISTS movie_dashboard
    DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE movie_dashboard;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

DROP TABLE IF EXISTS fact_tag;
DROP TABLE IF EXISTS fact_rating;
DROP TABLE IF EXISTS rel_movie_genre;
DROP TABLE IF EXISTS dim_movie;
DROP TABLE IF EXISTS dim_genre;

CREATE TABLE dim_genre (
    genre_id      INT         NOT NULL COMMENT '类型ID',
    genre_name    VARCHAR(20) NOT NULL COMMENT '类型中文名',
    genre_name_en VARCHAR(30) NOT NULL COMMENT '类型英文名（数据原始值）',
    sort_no       INT         NOT NULL DEFAULT 0 COMMENT '展示顺序',
    PRIMARY KEY (genre_id),
    UNIQUE KEY uk_genre_name (genre_name)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '类型维度表';

CREATE TABLE dim_movie (
    movie_id     INT          NOT NULL COMMENT '影片ID（MovieLens movieId）',
    title        VARCHAR(300) NOT NULL COMMENT '片名，含上映年份',
    release_year SMALLINT     NULL COMMENT '上映年份，无法从片名解析时为 NULL',
    PRIMARY KEY (movie_id),
    KEY idx_movie_year (release_year)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '影片维度表';

CREATE TABLE rel_movie_genre (
    movie_id INT NOT NULL COMMENT '影片ID',
    genre_id INT NOT NULL COMMENT '类型ID',
    PRIMARY KEY (movie_id, genre_id),
    KEY idx_rel_genre (genre_id),
    CONSTRAINT fk_rel_movie FOREIGN KEY (movie_id) REFERENCES dim_movie (movie_id),
    CONSTRAINT fk_rel_genre FOREIGN KEY (genre_id) REFERENCES dim_genre (genre_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '影片-类型关联表';

CREATE TABLE fact_rating (
    rating_id   BIGINT       NOT NULL COMMENT '评分记录ID',
    user_id     INT          NOT NULL COMMENT '用户ID',
    movie_id    INT          NOT NULL COMMENT '影片ID',
    score       DECIMAL(3, 1) NOT NULL COMMENT '评分，10分制（原始5星制×2）',
    rated_at    DATETIME     NOT NULL COMMENT '评分时间',
    rated_date  DATE         NOT NULL COMMENT '评分日期（冗余，便于按天聚合）',
    rated_month CHAR(7)      NOT NULL COMMENT '评分月份 yyyy-MM（冗余，便于按月聚合）',
    rated_year  SMALLINT     NOT NULL COMMENT '评分年份（冗余）',
    PRIMARY KEY (rating_id),
    KEY idx_rating_movie (movie_id, rated_date),
    KEY idx_rating_date (rated_date),
    KEY idx_rating_month (rated_month),
    KEY idx_rating_user (user_id),
    CONSTRAINT fk_rating_movie FOREIGN KEY (movie_id) REFERENCES dim_movie (movie_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '评分事实表（10万+行）';

CREATE TABLE fact_tag (
    tag_id      BIGINT       NOT NULL COMMENT '标签记录ID',
    user_id     INT          NOT NULL COMMENT '用户ID',
    movie_id    INT          NOT NULL COMMENT '影片ID',
    tag         VARCHAR(100) NOT NULL COMMENT '标签文本',
    tagged_at   DATETIME     NOT NULL COMMENT '打标签时间',
    tagged_date DATE         NOT NULL COMMENT '打标签日期',
    PRIMARY KEY (tag_id),
    KEY idx_tag_movie (movie_id),
    KEY idx_tag_name (tag),
    CONSTRAINT fk_tag_movie FOREIGN KEY (movie_id) REFERENCES dim_movie (movie_id)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COMMENT = '标签事实表';

SET FOREIGN_KEY_CHECKS = 1;

-- 类型维度数据：MovieLens 官方 19 个类型 + 1 个“未知”
-- ------------------------------------------------
INSERT INTO dim_genre (genre_id, genre_name, genre_name_en, sort_no) VALUES (1, '动作', 'Action', 1);
INSERT INTO dim_genre (genre_id, genre_name, genre_name_en, sort_no) VALUES (2, '冒险', 'Adventure', 2);
INSERT INTO dim_genre (genre_id, genre_name, genre_name_en, sort_no) VALUES (3, '动画', 'Animation', 3);
INSERT INTO dim_genre (genre_id, genre_name, genre_name_en, sort_no) VALUES (4, '儿童', 'Children', 4);
INSERT INTO dim_genre (genre_id, genre_name, genre_name_en, sort_no) VALUES (5, '喜剧', 'Comedy', 5);
INSERT INTO dim_genre (genre_id, genre_name, genre_name_en, sort_no) VALUES (6, '犯罪', 'Crime', 6);
INSERT INTO dim_genre (genre_id, genre_name, genre_name_en, sort_no) VALUES (7, '纪录片', 'Documentary', 7);
INSERT INTO dim_genre (genre_id, genre_name, genre_name_en, sort_no) VALUES (8, '剧情', 'Drama', 8);
INSERT INTO dim_genre (genre_id, genre_name, genre_name_en, sort_no) VALUES (9, '奇幻', 'Fantasy', 9);
INSERT INTO dim_genre (genre_id, genre_name, genre_name_en, sort_no) VALUES (10, '黑色电影', 'Film-Noir', 10);
INSERT INTO dim_genre (genre_id, genre_name, genre_name_en, sort_no) VALUES (11, '恐怖', 'Horror', 11);
INSERT INTO dim_genre (genre_id, genre_name, genre_name_en, sort_no) VALUES (12, 'IMAX', 'IMAX', 12);
INSERT INTO dim_genre (genre_id, genre_name, genre_name_en, sort_no) VALUES (13, '歌舞', 'Musical', 13);
INSERT INTO dim_genre (genre_id, genre_name, genre_name_en, sort_no) VALUES (14, '悬疑', 'Mystery', 14);
INSERT INTO dim_genre (genre_id, genre_name, genre_name_en, sort_no) VALUES (15, '爱情', 'Romance', 15);
INSERT INTO dim_genre (genre_id, genre_name, genre_name_en, sort_no) VALUES (16, '科幻', 'Sci-Fi', 16);
INSERT INTO dim_genre (genre_id, genre_name, genre_name_en, sort_no) VALUES (17, '惊悚', 'Thriller', 17);
INSERT INTO dim_genre (genre_id, genre_name, genre_name_en, sort_no) VALUES (18, '战争', 'War', 18);
INSERT INTO dim_genre (genre_id, genre_name, genre_name_en, sort_no) VALUES (19, '西部', 'Western', 19);
INSERT INTO dim_genre (genre_id, genre_name, genre_name_en, sort_no) VALUES (20, '未知', '(no genres listed)', 20);
