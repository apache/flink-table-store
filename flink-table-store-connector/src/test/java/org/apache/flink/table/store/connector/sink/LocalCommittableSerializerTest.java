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

package org.apache.flink.table.store.connector.sink;

import org.apache.flink.table.store.file.mergetree.Increment;
import org.apache.flink.table.types.logical.IntType;
import org.apache.flink.table.types.logical.RowType;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.apache.flink.table.store.file.manifest.ManifestCommittableSerializerTest.randomIncrement;
import static org.apache.flink.table.store.file.mergetree.compact.CompactManagerTest.row;
import static org.assertj.core.api.Assertions.assertThat;

/** Test for {@link LocalCommittableSerializer}. */
public class LocalCommittableSerializerTest {

    @Test
    public void test() throws IOException {
        LocalCommittableSerializer serializer =
                new LocalCommittableSerializer(
                        RowType.of(new IntType()),
                        RowType.of(new IntType()),
                        RowType.of(new IntType()));
        Increment increment = randomIncrement();
        LocalCommittable committable = new LocalCommittable(row(0), 1, increment);
        LocalCommittable newCommittable =
                serializer.deserialize(1, serializer.serialize(committable));
        assertThat(newCommittable).isEqualTo(committable);
    }
}
