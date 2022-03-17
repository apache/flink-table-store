/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.flink.table.store.connector.utils;

import org.apache.flink.configuration.Configuration;
import org.apache.flink.configuration.ReadableConfig;
import org.apache.flink.table.api.TableConfig;

import java.lang.reflect.Field;

/** Utils for {@link TableConfig}. */
public class TableConfigUtils {

    public static Configuration extractConfiguration(ReadableConfig readableConfig) {
        return extractConfiguration(readableConfig, new Configuration());
    }

    private static Configuration extractConfiguration(ReadableConfig from, Configuration to) {
        if (from instanceof Configuration) {
            return (Configuration) from;
        }

        if (!(from instanceof TableConfig)) {
            throw new RuntimeException("Unknown readableConfig type: " + from.getClass());
        }

        TableConfig tableConfig = (TableConfig) from;
        try {
            Field rootField = TableConfig.class.getDeclaredField("rootConfiguration");
            rootField.setAccessible(true);
            ReadableConfig rootConfig = (ReadableConfig) rootField.get(tableConfig);
            extractConfiguration(rootConfig, to);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        to.addAll(tableConfig.getConfiguration());
        return to;
    }
}
