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

package me.lucko.luckperms.common.cacheddata;

import me.lucko.luckperms.common.model.Group;
import me.lucko.luckperms.common.model.HolderType;
import me.lucko.luckperms.common.verbose.VerboseCheckTarget;

import net.luckperms.api.cacheddata.CachedDataManager;
import net.luckperms.api.query.QueryOptions;

/**
 * Holds an easily accessible cache of a groups's data in a number of contexts
 */
public class GroupCachedDataManager extends HolderCachedDataManager<Group> implements CachedDataManager {

    public GroupCachedDataManager(Group holder) {
        super(holder);
    }

    @Override
    protected CacheMetadata getMetadataForQueryOptions(QueryOptions queryOptions) {
        return new CacheMetadata(HolderType.GROUP, VerboseCheckTarget.group(this.holder), queryOptions);
    }

}
