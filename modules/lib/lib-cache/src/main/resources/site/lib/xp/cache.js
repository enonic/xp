/**
 * Cache related functions.
 *
 * @example
 * var httpLib = require('/lib/xp/cache');
 *
 * @module lib/xp/cache
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

Cache.prototype.get = function (key, callback) {
    var result = this.cache.get(key, callback);
    return __.toNativeObject(result);
};

Cache.prototype.clear = function () {
    this.cache.clear();
};

Cache.prototype.size = function () {
    return this.cache.getSize();
};

/**
 * Creates a new cache.
 */
exports.newCache = function (options) {
    return new Cache(options);
};
