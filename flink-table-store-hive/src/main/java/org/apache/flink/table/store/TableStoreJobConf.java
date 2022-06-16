/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.flink.table.store;

import org.apache.flink.configuration.ConfigOption;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.table.store.file.FileStoreOptions;
import org.apache.flink.util.Preconditions;

import org.apache.hadoop.hive.metastore.api.hive_metastoreConstants;
import org.apache.hadoop.mapred.JobConf;

import java.util.Arrays;
import java.util.Map;
import java.util.Properties;

/**
 * Utility class to convert Hive table property keys and get file store specific configurations from
 * {@link JobConf}.
 */
public class TableStoreJobConf {

    private static final String TBLPROPERTIES_PREFIX = "table-store.";
    private static final String INTERNAL_TBLPROPERTIES_PREFIX =
            "table-store.internal.tblproperties.";

    private static final String INTERNAL_DB_NAME = "table-store.internal.db-name";
    private static final String INTERNAL_TABLE_NAME = "table-store.internal.table-name";
    private static final String INTERNAL_LOCATION = "table-store.internal.location";

    private static final String INTERNAL_FILE_STORE_USER = "table-store.internal.file-store.user";

    private final JobConf jobConf;

    public TableStoreJobConf(JobConf jobConf) {
        this.jobConf = jobConf;
    }

    public static void configureInputJobProperties(Properties properties, Map<String, String> map) {
        String tableNameString = properties.getProperty(hive_metastoreConstants.META_TABLE_NAME);
        String[] tableName = tableNameString.split("\\.");
        Preconditions.checkState(
                tableName.length >= 2,
                "There is no dot in META_TABLE_NAME " + tableNameString + ". This is unexpected.");

        map.put(
                INTERNAL_DB_NAME,
                String.join(".", Arrays.copyOfRange(tableName, 0, tableName.length - 1)));

        map.put(INTERNAL_TABLE_NAME, tableName[tableName.length - 1]);

        map.put(
                INTERNAL_LOCATION,
                properties.getProperty(hive_metastoreConstants.META_TABLE_LOCATION));

        for (ConfigOption<?> option : FileStoreOptions.allOptions()) {
            if (properties.containsKey(TBLPROPERTIES_PREFIX + option.key())) {
                map.put(
                        INTERNAL_TBLPROPERTIES_PREFIX + option.key(),
                        properties.getProperty(TBLPROPERTIES_PREFIX + option.key()));
            }
        }
    }

    public String getDbName() {
        return jobConf.get(INTERNAL_DB_NAME);
    }

    public String getTableName() {
        return jobConf.get(INTERNAL_TABLE_NAME);
    }

    public String getLocation() {
        return jobConf.get(INTERNAL_LOCATION);
    }

    public void updateFileStoreOptions(Configuration fileStoreOptions) {
        fileStoreOptions.set(FileStoreOptions.PATH, getLocation());
        for (Map.Entry<String, String> entry :
                jobConf.getPropsWithPrefix(INTERNAL_TBLPROPERTIES_PREFIX).entrySet()) {
            fileStoreOptions.setString(entry.getKey(), entry.getValue());
        }
    }

    public String getFileStoreUser() {
        return jobConf.get(INTERNAL_FILE_STORE_USER);
    }

    public void setFileStoreUser(String user) {
        jobConf.set(INTERNAL_FILE_STORE_USER, user);
    }
}
