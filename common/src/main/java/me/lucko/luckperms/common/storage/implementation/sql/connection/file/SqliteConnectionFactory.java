/*
 * This file is part of RoryPerms, licensed under the MIT License.
 *
 *  Copyright (c) lucko (Luck) <luck@lucko.me>
 *  Copyright (c) contributors
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package me.lucko.luckperms.common.storage.implementation.sql.connection.file;

import me.lucko.luckperms.common.dependencies.Dependency;
import me.lucko.luckperms.common.dependencies.classloader.IsolatedClassLoader;
import me.lucko.luckperms.common.plugin.RoryPermsPlugin;

import java.lang.reflect.Constructor;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.EnumSet;
import java.util.Properties;
import java.util.function.Function;

public class SqliteConnectionFactory extends FlatfileConnectionFactory {
    private Constructor<?> connectionConstructor;

    public SqliteConnectionFactory(Path file) {
        super(file);
    }

    @Override
    public String getImplementationName() {
        return "SQLite";
    }

    @Override
    public void init(RoryPermsPlugin plugin) {
        migrateOldDatabaseFile("luckperms.sqlite");

        IsolatedClassLoader classLoader = plugin.getDependencyManager().obtainClassLoaderWith(EnumSet.of(Dependency.SQLITE_DRIVER));
        try {
            Class<?> connectionClass = classLoader.loadClass("org.sqlite.jdbc4.JDBC4Connection");
            this.connectionConstructor = connectionClass.getConstructor(String.class, String.class, Properties.class);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected Connection createConnection(Path file) throws SQLException {
        try {
            return (Connection) this.connectionConstructor.newInstance("jdbc:sqlite:" + file.toString(), file.toString(), new Properties());
        } catch (ReflectiveOperationException e) {
            if (e.getCause() instanceof SQLException) {
                throw (SQLException) e.getCause();
            }
            throw new RuntimeException(e);
        }
    }

    @Override
    public Function<String, String> getStatementProcessor() {
        return s -> s.replace('\'', '`');
    }
}
