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

package org.apache.flink.table.store.file.catalog;

import org.apache.flink.configuration.ConfigOption;
import org.apache.flink.configuration.ConfigOptions;
import org.apache.flink.configuration.ReadableConfig;
import org.apache.flink.core.fs.Path;
import org.apache.flink.util.Preconditions;

/** Factory to create {@link FileSystemCatalog}. */
public class FileSystemCatalogFactory implements CatalogFactory {

    private static final String IDENTIFIER = "filesystem";

    private static final ConfigOption<String> WAREHOUSE =
            ConfigOptions.key("warehouse")
                    .stringType()
                    .noDefaultValue()
                    .withDescription("The warehouse root path of catalog.");

    @Override
    public String identifier() {
        return IDENTIFIER;
    }

    @Override
    public Catalog create(ReadableConfig options) {
        String warehouse =
                Preconditions.checkNotNull(
                        options.get(WAREHOUSE),
                        WAREHOUSE.key()
                                + " must be set for table store "
                                + IDENTIFIER
                                + " catalog");
        return new FileSystemCatalog(new Path(warehouse));
    }
}
