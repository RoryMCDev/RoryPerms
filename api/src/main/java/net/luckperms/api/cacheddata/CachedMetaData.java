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

package net.luckperms.api.cacheddata;

import net.luckperms.api.metastacking.MetaStackDefinition;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.SortedMap;
import java.util.function.Function;

/**
 * Holds cached meta lookup data for a specific set of contexts.
 */
public interface CachedMetaData extends CachedData {

    /**
     * Gets a value for the given meta key.
     *
     * @param key the key
     * @return the value
     */
    @Nullable String getMetaValue(@NonNull String key);

    /**
     * Gets a value for the given meta key, and runs it through the given {@code transformer}.
     *
     * <p>If no such meta value exists, an {@link Optional#empty() empty optional} is returned.
     * (the transformer will never be passed a null argument)</p>
     *
     * <p>The transformer is allowed to throw {@link IllegalArgumentException} or return null. This
     * will also result in an {@link Optional#empty() empty optional} being returned.</p>
     *
     * <p>For example, to parse and return an integer meta value, use:</p>
     * <p><blockquote><pre>
     *     getMetaValue("my-int-val", Integer::parseInt).orElse(0);
     * </pre></blockquote>
     *
     * @param key the key
     * @param valueTransformer the transformer used to transform the value
     * @param <T> the type of the transformed result
     * @return the meta value
     * @since 5.3
     */
    default <T> @NonNull Optional<T> getMetaValue(@NonNull String key, @NonNull Function<String, ? extends T> valueTransformer) {
        return Optional.ofNullable(getMetaValue(key)).map(value -> {
            try {
                return valueTransformer.apply(value);
            } catch (IllegalArgumentException e) {
                return null;
            }
        });
    }

    /**
     * Gets the holder's highest priority prefix, or null if the holder has no prefixes
     *
     * @return a prefix string, or null
     */
    @Nullable String getPrefix();

    /**
     * Gets the holder's highest priority suffix, or null if the holder has no suffixes
     *
     * @return a suffix string, or null
     */
    @Nullable String getSuffix();

    /**
     * Gets an immutable copy of the meta this holder has.
     *
     * @return an immutable map of meta
     */
    @NonNull @Unmodifiable Map<String, List<String>> getMeta();

    /**
     * Gets an immutable sorted map of all of the prefixes the holder has, whereby the first
     * value is the highest priority prefix.
     *
     * @return a sorted map of prefixes
     */
    @NonNull @Unmodifiable SortedMap<Integer, String> getPrefixes();

    /**
     * Gets an immutable sorted map of all of the suffixes the holder has, whereby the first
     * value is the highest priority suffix.
     *
     * @return a sorted map of suffixes
     */
    @NonNull @Unmodifiable SortedMap<Integer, String> getSuffixes();

    /**
     * Gets the name of the holders primary group.
     *
     * <p>Will return {@code null} for Group holder types.</p>
     *
     * @return the name of the primary group
     * @since 5.1
     */
    @Nullable String getPrimaryGroup();

    /**
     * Gets the definition used for the prefix stack
     *
     * @return the definition used for the prefix stack
     */
    @NonNull MetaStackDefinition getPrefixStackDefinition();

    /**
     * Gets the definition used for the suffix stack
     *
     * @return the definition used for the suffix stack
     */
    @NonNull MetaStackDefinition getSuffixStackDefinition();

}
