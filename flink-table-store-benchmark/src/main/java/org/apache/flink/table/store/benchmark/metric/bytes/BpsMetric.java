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

package org.apache.flink.table.store.benchmark.metric.bytes;

import org.apache.flink.table.store.benchmark.utils.BenchmarkUtils;

import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonCreator;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonIgnore;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonInclude;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.JsonNode;

import javax.annotation.Nullable;

import java.util.Objects;

import static java.util.Objects.requireNonNull;

/**
 * Response type for BPS aggregated metrics. Contains the metric name and optionally the sum,
 * average, minimum and maximum.
 */
public class BpsMetric {

    private static final String FIELD_NAME_ID = "id";

    private static final String FIELD_NAME_MIN = "min";

    private static final String FIELD_NAME_MAX = "max";

    private static final String FIELD_NAME_AVG = "avg";

    private static final String FIELD_NAME_SUM = "sum";

    @JsonProperty(value = FIELD_NAME_ID, required = true)
    private final String id;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty(FIELD_NAME_MIN)
    private final Double min;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty(FIELD_NAME_MAX)
    private final Double max;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty(FIELD_NAME_AVG)
    private final Double avg;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty(FIELD_NAME_SUM)
    private final Double sum;

    @JsonCreator
    public BpsMetric(
            final @JsonProperty(value = FIELD_NAME_ID, required = true) String id,
            final @Nullable @JsonProperty(FIELD_NAME_MIN) Double min,
            final @Nullable @JsonProperty(FIELD_NAME_MAX) Double max,
            final @Nullable @JsonProperty(FIELD_NAME_AVG) Double avg,
            final @Nullable @JsonProperty(FIELD_NAME_SUM) Double sum) {

        this.id = requireNonNull(id, "id must not be null");
        this.min = min;
        this.max = max;
        this.avg = avg;
        this.sum = sum;
    }

    public BpsMetric(final @JsonProperty(value = FIELD_NAME_ID, required = true) String id) {
        this(id, null, null, null, null);
    }

    @JsonIgnore
    public String getId() {
        return id;
    }

    @JsonIgnore
    public Double getMin() {
        return min;
    }

    @JsonIgnore
    public Double getMax() {
        return max;
    }

    @JsonIgnore
    public Double getSum() {
        return sum;
    }

    @JsonIgnore
    public Double getAvg() {
        return avg;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        BpsMetric that = (BpsMetric) o;
        return Objects.equals(id, that.id)
                && Objects.equals(min, that.min)
                && Objects.equals(max, that.max)
                && Objects.equals(avg, that.avg)
                && Objects.equals(sum, that.sum);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, min, max, avg, sum);
    }

    @Override
    public String toString() {
        return "BpsMetric{"
                + "id='"
                + id
                + '\''
                + ", mim='"
                + min
                + '\''
                + ", max='"
                + max
                + '\''
                + ", avg='"
                + avg
                + '\''
                + ", sum='"
                + sum
                + '\''
                + '}';
    }

    public static BpsMetric fromJson(String json) {
        try {
            JsonNode jsonNode = BenchmarkUtils.JSON_MAPPER.readTree(json);
            return BenchmarkUtils.JSON_MAPPER.convertValue(jsonNode.get(0), BpsMetric.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
