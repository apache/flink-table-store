---
title: "Spark"
weight: 3
type: docs
aliases:
- /engines/spark.html
---
<!--
Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.
-->

# Spark

Table Store supports reading table store tables through Spark.

## Version

Table Store supports Spark 3+. It is highly recommended to use Spark 3+ version with many improvements.

## Install

{{< stable >}}
Download [flink-table-store-spark-{{< version >}}.jar](https://repo.maven.apache.org/maven2/org/apache/flink/flink-table-store-spark/{{< version >}}/flink-table-store-spark-{{< version >}}.jar).
{{< /stable >}}
{{< unstable >}}
You are using an unreleased version of Table Store, you need to manually [Build Spark Bundled Jar]({{< ref "docs/engines/build" >}}) from the source code.
{{< /unstable >}}

Use `--jars` in spark-sql:
```bash
spark-sql ... --jars flink-table-store-spark-{{< version >}}.jar
```

Alternatively, you can copy `flink-table-store-spark-{{< version >}}.jar` under `spark/jars` in your Spark installation.

## Catalog

The following command registers the Table Store's Spark catalog with the name `table_store`:

```bash
spark-sql ... \
    --conf spark.sql.catalog.table_store=org.apache.flink.table.store.spark.SparkCatalog \
    --conf spark.sql.catalog.table_store.warehouse=file:/tmp/warehouse
```

Some extra configurations are needed if your Table Store Catalog uses the Hive
Metastore (No extra configuration is required for read-only).

```bash
spark-sql ... \
    --conf spark.sql.catalog.table_store=org.apache.flink.table.store.spark.SparkCatalog \
    --conf spark.sql.catalog.table_store.warehouse=file:/tmp/warehouse \
    --conf spark.sql.catalog.table_store.metastore=hive \
    --conf spark.sql.catalog.table_store.uri=thrift://...
```

## Create Temporary View

Use the `CREATE TEMPORARY VIEW` command to create a Spark mapping table on top of
an existing Table Store table if you don't want to use Table Store Catalog.

```sql
CREATE TEMPORARY VIEW myTable
USING tablestore
OPTIONS (
  path "file:/tmp/warehouse/default.db/myTable"
)
```

## Query Table

```sql
SELECT * FROM table_store.default.myTable;
```

## DDL Statements

### Create Table
```sql
CREATE TABLE [IF NOT EXISTS] table_identifier 
[ ( col_name1[:] col_type1 [ COMMENT col_comment1 ], ... ) ]
[ USING table_store ]    
[ COMMENT table_comment ]
[ PARTITIONED BY ( col_name1, col_name2, ... ) ]
[ TBLPROPERTIES ( key1=val1, key2=val2, ... ) ]       
```
For example, create an order table with `order_id` as primary key and partitioned by `dt, hh`.
```sql
CREATE TABLE table_store.default.OrderTable (
    order_id BIGINT NOT NULL comment 'biz order id',
    buyer_id BIGINT NOT NULL COMMENT 'buyer id',
    coupon_info ARRAY<STRING> NOT NULL COMMENT 'coupon info',
    order_amount DOUBLE NOT NULL COMMENT 'order amount',
    dt STRING NOT NULL COMMENT 'yyyy-MM-dd',
    hh STRING NOT NULL COMMENT 'HH'
) COMMENT 'my table'
PARTITIONED BY (dt, hh)
TBLPROPERTIES ('foo' = 'bar', 'primary-key' = 'order_id,dt,hh')
```
{{< hint info >}}
__Note:__
- Primary key feature is supported via table properties, and composite primary key is delimited with comma.
- Partition fields should be predefined, complex partition such like `PARTITIONED BY ( col_name2[:] col_type2 [ COMMENT col_comment2 ], ... )` is not supported.
- For Spark 3.0, `CREATE TABLE USING table_store` is required.
{{< /hint >}}

### Alter Table
```sql
ALTER TABLE table_identifier   
SET TBLPROPERTIES ( key1=val1 ) 
    | RESET TBLPROPERTIES (key2)
    | ADD COLUMNS ( col_name col_type [ , ... ] )
    | { ALTER | CHNAGE } COLUMN col_name { DROP NOT NULL | COMMENT 'new_comment'}
```

- Change/add table properties
```sql
ALTER TABLE table_store.default.OrderTable SET TBLPROPERTIES (
    'write-buffer-size'='256 MB'
)
```

- Remove a table property
```sql
ALTER TABLE table_store.default.OrderTable UNSET TBLPROPERTIES ('write-buffer-size')
```

- Add a new column
```sql
ALTER TABLE table_store.default.OrderTable ADD COLUMNS (buy_count INT)
```

- Change column nullability
```sql
ALTER TABLE table_store.default.OrderTable ALTER COLUMN coupon_info DROP NOT NULL
```

- Change column comment
```sql
ALTER TABLE table_store.default.OrderTable ALTER COLUMN buy_count COMMENT 'buy count'
```

{{< hint info >}}
__Note:__
- Spark does not support changing nullable column to nonnull column.
{{< /hint >}}

### Drop Table

```sql
DROP TABLE table_store.default.OrderTable
```
{{< hint warning >}}
__Attention__: Drop a table will delete both metadata and files on the disk.
{{< /hint >}}

### Create Namespace

```sql
CREATE NAMESPACE [IF NOT EXISTS] table_store.bar
```

### Drop Namespace

```sql
DROP NAMESPACE table_store.bar
```

{{< hint warning >}}
__Attention__: Drop a namespace will delete all table's metadata and files under this namespace on the disk.
{{< /hint >}}
