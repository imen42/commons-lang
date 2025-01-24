/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package benchmark;
import org.apache.commons.lang3.StringUtils;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.Throughput) // Measures operations per unit of time
@OutputTimeUnit(TimeUnit.MILLISECONDS) // Outputs results in milliseconds
@State(Scope.Thread) // Each thread gets its own state instance
public class ApacheCommonsBenchmark {

    private String testString;

    @Setup(Level.Iteration) // Initialize before each iteration
    public void setup() {
        testString = "Apache Commons Langs JMH Benchmarking Example";
    }

    @Benchmark
    public boolean testIsNotEmpty() {
        return StringUtils.isNotEmpty(testString);
    }

    @Benchmark
    public String testReverse() {
        return StringUtils.reverse(testString);
    }
}
