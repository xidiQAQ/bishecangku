@echo off
chcp 65001
mysql -u root -proot --default-character-set=utf8mb4 mental_health < 13-insert-articles-simple.sql
echo 文章数据插入完成！
pause
