# MySQL Import Guide | MySQL 导入说明

## 文件说明 | File
- `devflow_full_init.sql`: standalone SQL for schema, indexes, and demo data.
- `devflow_full_init.sql`：可独立导入的全量 SQL（表结构、索引、演示数据）。

## 导入方式 | Import Command
```bash
mysql -h 127.0.0.1 -P 3306 -u root -p < deploy/mysql/devflow_full_init.sql
```

or / 或者

```bash
mysql -h 127.0.0.1 -P 3306 -u devflow -pdevflow < deploy/mysql/devflow_full_init.sql
```

## 备注 | Notes
- SQL includes bilingual comments on every table and column.
- All table and column comments are Chinese/English bilingual.
- The script is designed for MySQL 8+.
