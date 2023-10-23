/*
 * MIT License
 *
 * Copyright (c) 2020 Mark
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.crabcode.factory.data;


import com.crabcode.factory.reflect.exception.UncheckedClassNotFoundException;
import com.crabcode.factory.util.Logger;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

/**
 * @since September 21, 2019
 * @author Andavin
 */
abstract class SQLDataManager implements DataManager {

    static final String TABLE_NAME = "`custom_images`";
    private final String url;
    private final Properties properties = new Properties();

    SQLDataManager(String url) {
        this.url = checkNotNull(url, "url");
    }

    SQLDataManager(String url, String user, String password) {
        this(url);
        this.properties.put("user", checkNotNull(user, "user"));
        this.properties.put("password", checkNotNull(password, "password"));
    }

    /**
     * Create a new {@link Connection} to the SQL database
     * with the given properties and credentials specified
     * at creation of this data manager.
     *
     * @return The newly created connection.
     * @throws SQLException If something goes wrong while creating
     *                      the connection.
     */
    Connection getConnection() throws SQLException {
        return DriverManager.getConnection(this.url, this.properties);
    }


}
