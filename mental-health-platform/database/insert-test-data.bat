@echo off
echo 正在插入心理测试数据...
mysql -uroot -p123456 mental_health < 07-insert-test-data.sql
if %errorlevel% == 0 (
    echo 心理测试数据插入成功！
) else (
    echo 心理测试数据插入失败，请检查MySQL连接
)
pause
