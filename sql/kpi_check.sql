-- ============================================================
-- 看板 KPI 抽查脚本
-- 用法：双击 kpi-check.bat，或在本目录执行
--       mysql -uroot -p --default-character-set=utf8mb4 < kpi_check.sql
-- 目的：用 SQL 直接算一遍，和页面上的数字对照
-- ============================================================
USE movie_dashboard;

-- ---------- 1. 数据总量（老师必看） ----------
SELECT '数据总量' AS 检查项,
       (SELECT COUNT(*) FROM dim_movie)   AS 影片数,
       (SELECT COUNT(*) FROM fact_rating) AS 评分数,
       (SELECT COUNT(*) FROM fact_tag)    AS 标签数,
       (SELECT COUNT(DISTINCT user_id) FROM fact_rating) AS 用户数;

-- ---------- 2. 看板默认筛选条件下的 6 个 KPI ----------
-- 默认条件：评分时间 2015-01-01 ~ 2018-09-24，上映年份 1902 ~ 2018
-- 对照页面顶部 6 张卡片：9729 / 27918 / 6.96 / 155 / 100 / 46.5
SELECT
    (SELECT COUNT(*) FROM dim_movie m
      WHERE m.release_year BETWEEN 1902 AND 2018) AS 影片总数,
    (SELECT COUNT(*) FROM fact_rating r
       JOIN dim_movie m ON m.movie_id = r.movie_id
      WHERE r.rated_date BETWEEN '2015-01-01' AND '2018-09-24'
        AND m.release_year BETWEEN 1902 AND 2018) AS 评分总数,
    (SELECT ROUND(AVG(r.score), 2) FROM fact_rating r
       JOIN dim_movie m ON m.movie_id = r.movie_id
      WHERE r.rated_date BETWEEN '2015-01-01' AND '2018-09-24'
        AND m.release_year BETWEEN 1902 AND 2018) AS 平均评分,
    (SELECT COUNT(DISTINCT r.user_id) FROM fact_rating r
       JOIN dim_movie m ON m.movie_id = r.movie_id
      WHERE r.rated_date BETWEEN '2015-01-01' AND '2018-09-24'
        AND m.release_year BETWEEN 1902 AND 2018) AS 参与用户,
    (SELECT MAX(c) FROM (
        SELECT COUNT(*) c FROM fact_rating r
          JOIN dim_movie m ON m.movie_id = r.movie_id
         WHERE r.rated_date BETWEEN '2015-01-01' AND '2018-09-24'
           AND m.release_year BETWEEN 1902 AND 2018
         GROUP BY r.movie_id) t) AS 单片最高评分数,
    (SELECT ROUND(100.0 * SUM(r.score >= 8) / COUNT(*), 1) FROM fact_rating r
       JOIN dim_movie m ON m.movie_id = r.movie_id
      WHERE r.rated_date BETWEEN '2015-01-01' AND '2018-09-24'
        AND m.release_year BETWEEN 1902 AND 2018) AS 高分占比;

-- ---------- 3. 类型对比 TOP5（对照"类型对比"柱状图） ----------
-- 口径与后端一致：先算每部影片的平均分，再对影片平均分求平均（不是对单条评分求平均）
WITH movie_agg AS (
    SELECT m.movie_id,
           COUNT(r.rating_id) AS rating_count,
           AVG(r.score)       AS avg_score
    FROM dim_movie m
    LEFT JOIN fact_rating r
           ON r.movie_id = m.movie_id
          AND r.rated_date BETWEEN '2015-01-01' AND '2018-09-24'
    WHERE m.release_year BETWEEN 1902 AND 2018
    GROUP BY m.movie_id
)
SELECT g.genre_name                AS 类型,
       COUNT(DISTINCT ma.movie_id) AS 影片数,
       COALESCE(SUM(ma.rating_count), 0) AS 评分数,
       ROUND(AVG(ma.avg_score), 2) AS 平均评分
FROM movie_agg ma
JOIN rel_movie_genre rg ON rg.movie_id = ma.movie_id
JOIN dim_genre g ON g.genre_id = rg.genre_id
GROUP BY g.genre_name
ORDER BY 影片数 DESC
LIMIT 5;
