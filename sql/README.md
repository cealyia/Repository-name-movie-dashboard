# 数据库脚本说明

## 文件顺序

| 文件 | 内容 | 行数 |
| --- | --- | --- |
| `01_schema.sql` | 建库、删表、建 5 张表、写入类型维度 | 20 |
| `02_dim_movie.sql` | 影片维度 + 影片-类型关联 | 9,742 + 22,084 |
| `03_fact_rating.sql` | 评分事实表 | 100,836 |
| `04_fact_tag.sql` | 标签事实表 | 3,683 |

必须先执行 `01`，再执行后面三个（有外键依赖）。

## 方式一：一键脚本（推荐）

双击 `import.bat`。脚本里的 `MYSQL_BIN` / `DB_USER` / `DB_PASS` 按本机情况修改。

## 方式二：命令行手动执行

```bat
cd /d 项目路径\movie-dashboard\sql
mysql -uroot -p --default-character-set=utf8mb4 < 01_schema.sql
mysql -uroot -p --default-character-set=utf8mb4 < 02_dim_movie.sql
mysql -uroot -p --default-character-set=utf8mb4 < 03_fact_rating.sql
mysql -uroot -p --default-character-set=utf8mb4 < 04_fact_tag.sql
```

## 方式三：Navicat / MySQL Workbench

1. 新建连接，打开 `01_schema.sql`，点“执行”（会创建 `movie_dashboard` 库和表）。
2. 依次打开 `02`、`03`、`04` 并执行。
3. `03_fact_rating.sql` 约 7.6 MB，执行需要 10–30 秒，属正常现象。

## 校验

```sql
USE movie_dashboard;
SELECT COUNT(*) FROM fact_rating;   -- 100836
SELECT COUNT(*) FROM dim_movie;      -- 9742
SELECT COUNT(*) FROM fact_tag;       -- 3683
```

## 常用脚本（双击即可）

| 文件 | 作用 |
| --- | --- |
| `import.bat` | 建库 + 导入全部数据（首次运行用） |
| `mysql-console.bat` | 打开 `mysql>` 命令行，直接敲 SQL，输 `exit` 退出 |
| `kpi-check.bat` | 自动执行 `kpi_check.sql`，打印数据总量、6 个 KPI、类型 TOP5，用来和看板对数字 |

三个脚本里的 `MYSQL_BIN` / `DB_USER` / `DB_PASS` 按本机情况修改。

`kpi_check.sql` 里的口径与后端接口完全一致（类型平均分是"先算影片平均分、再对影片求平均"），
所以执行结果应该和页面上的数字一模一样；对不上说明数据没导全或筛选条件不一致。

## 重建

`01_schema.sql` 开头有 `DROP TABLE IF EXISTS`，重复执行会清空并重建所有表。
