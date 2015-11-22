/**
 * Cache related functions.
 *
 * @example
 * var httpLib = require('/lib/xp/cache');
 *
 * @module lib/xp/cache
 */

/**
 * Creates a new cache with options.
 *
 * @param {object} options Cache options as JSON.
 * @param {number} options.size Max size of cache.
 * @param {number} options.expire Expire time (in sec) for cache entries. If not set, it will never expire.
 * @constructor
 */
function Cache(options) {
    var builder = __.newBean('com.enonic.xp.lib.cache.CacheBeanBuilder');

    if (options.size) {
        builder.size = options.size;
    }

    if (options.expire) {
        builder.expire = options.expire;
    }

    this.cache = builder.build();
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
 * @returns {number} Returns number of elements in cache.
 */
Cache.prototype.size = function () {
    return this.cache.getSize();
};

/**
 * Creates a new cache.
 *
 * @param {object} options Cache options as JSON.
 * @param {number} options.size Max size of cache.
 * @param {number} options.expire Expire time (in sec) for cache entries. If not set, it will never expire.
 * @returns {Cache} Returns a new cache instance.
 */
exports.newCache = function (options) {
    return new Cache(options);
};
