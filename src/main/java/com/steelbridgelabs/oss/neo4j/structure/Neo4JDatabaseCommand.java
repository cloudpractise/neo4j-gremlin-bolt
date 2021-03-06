/*
 *  Copyright 2016 SteelBridge Laboratories, LLC.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 *  For more information: http://steelbridgelabs.com
 */

package com.steelbridgelabs.oss.neo4j.structure;

import org.neo4j.driver.Result;

import java.util.Map;
import java.util.function.Consumer;

/**
 * @author Rogelio J. Baucells
 */
class Neo4JDatabaseCommand {

    private static final Consumer<Result> noop = result -> {
    };

    private final String statement;
    private final Map<String, Object> parameters;
    private final Consumer<Result> callback;

    public Neo4JDatabaseCommand(String statement, Map<String, Object> parameters) {
        this.statement = statement;
        this.parameters = parameters;
        this.callback = noop;
    }

    public Neo4JDatabaseCommand(String statement, Map<String, Object> parameters, Consumer<Result> callback) {
        this.statement = statement;
        this.parameters = parameters;
        this.callback = callback;
    }

    public String getStatement() {
        return statement;
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }

    public Consumer<Result> getCallback() {
        return callback;
    }
}
