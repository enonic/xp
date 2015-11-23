/**
 * Cache related functions.
 *
 * @example
 * var cacheLib = require('/lib/xp/cache');
 *
 * @module lib/xp/cache
 */

/**
 * Creates a new cache with options.
 *
 * @param {object} options Cache options as JSON.
 * @constructor
 * @private
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
 * @example
 * var value = cache.get('mykey', function() {
 *   return 'myvalue';
 * });
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
 *
 * @example
 * cache.clear();
 *
 */
Cache.prototype.clear = function () {
    this.cache.clear();
};

/**
 * Returns number of elements in cache.
 *
 * @example
 * var size = cache.getSize();
 *
 * @returns {number} Returns number of elements in cache.
 */
Cache.prototype.getSize = function () {
    return this.cache.getSize();
};

/**
 * Creates a new cache.
 *
 * @example
 * var cache = cacheLib.newCache({
 *   size: 100,
 *   expire: 60
 * });
 *
 * var value = cache.get('mykey', function() {
 *   return 'myvalue';
 * });
 *
 * @param {object} options Cache options as JSON.
 * @param {number} options.size Max size of cache.
 * @param {number} options.expire Expire time (in sec) for cache entries. If not set, it will never expire.
 * @returns {Cache} Returns a new cache instance.
 */
exports.newCache = function (options) {
    return new Cache(options);
};
