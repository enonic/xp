/**
 * Cache related functions.
 *
 * @example
 * var cacheLib = require('/lib/xp/cache');
 *
 * @module cache
 */

/**
 * Creates a new cache with options.
 *
 * @param {*} native Native cache object.
 * @constructor
 * @private
 */
function Cache(native) {
    this.cache = native;
}

/**
 * Returns value for cache entry if exists, otherwise it's calculated and put into the cache.
 *
 * @param {string} key Cache key to use.
 * @param {function} callback Callback to a function that can calculate the cache value.
 * @returns {*} Cache value for key.
 */
Cache.prototype.get = function (key, callback) {
    var result = this.cache.get(key, callback);
    return __.toNativeObject(result);
};

/**
 * Clears the cache.
 */
Cache.prototype.clear = function () {
    this.cache.clear();
};

/**
 * Returns number of elements in cache.
 *
 * @returns {number} Returns the number of elements that are currently in the cache.
 */
Cache.prototype.getSize = function () {
    return this.cache.getSize();
};

/**
 * Creates a new cache.
 *
 * @example-ref examples/cache/newCache.js
 * @example-ref examples/cache/httpCache.js
 *
 * @param {object} options Cache options as JSON.
 * @param {number} options.size Maximum number of elements in the cache.
 * @param {number} options.expire Expire time (in sec) for cache entries. If not set, it will never expire.
 * @returns {Cache} Returns a new cache instance.
 */
exports.newCache = function (options) {
    var builder = __.newBean('com.enonic.xp.lib.cache.CacheBeanBuilder');

    if (options.size) {
        builder.size = options.size;
    }

    if (options.expire) {
        builder.expire = options.expire;
    }

    var cache = builder.build();
    return new Cache(cache);
};
