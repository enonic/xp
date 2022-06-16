/* global __ */

function requireNotNull(value, parameterName) {
    if (typeof value === 'undefined' || value === null) {
        throw `Parameter "${parameterName}" is required`;
    }
    return value;
}

function convertValue(value) {
    if (Array.isArray(value)) {
        return __.toScriptValue(value).getList();
    } else if (typeof value === 'object') {
        return __.toScriptValue(value).getMap();
    } else {
        return value;
    }
}

/**
 * Creates an instance of SharedMap which are shared across all applications and even cluster nodes.
 *
 * WARNING: Due to distributed nature of the Shared Map not all types of keys and values can be used. Strings, numbers, and pure JSON objects are supported. There is no runtime check for type compatibility due to performance reasons. The developer is also responsible for not modifying shared objects (keys and values) in place.
 *
 * @param mapId - map identifier
 * @constructor
 */
function SharedMap(mapId) {
    var bean = __.newBean('com.enonic.xp.lib.grid.SharedMapHandler');
    this.map = bean.getMap(mapId);
}

/**
 * Returns the value to which the specified key is mapped, or null if this map contains no mapping for the key.
 *
 * @param {string} key - key the key whose associated value is to be returned
 * @returns {string|number|boolean|JSON|null} the value to which the specified key is mapped, or null if this map contains no mapping for the key
 */
SharedMap.prototype.get = function (key) {
    return __.toNativeObject(this.map.get(requireNotNull(key, 'key')));
};

/**
 * Puts an entry into this map with a given time to live (TTL).
 * If value is null, the existing entry will be removed.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.key key of the entry
 * @param {string|number|boolean|JSON|null} params.value value of the entry
 * @param {number} [params.ttlSeconds] maximum time to live in seconds for this entry to stay in the map. (0 means infinite, negative means map config default or infinite if map config is not available)
 */
SharedMap.prototype.set = function (params) {
    var key = requireNotNull(params.key, 'key');
    var ttlSeconds = __.nullOrValue(params.ttlSeconds);
    var value = convertValue(params.value);
    if (ttlSeconds === null) {
        this.map.set(key, value);
    } else {
        this.map.set(key, value, ttlSeconds);
    }
};

/**
 * Removes the mapping for the key from this map if it is present.
 *
 * @param {string} key the key whose associated value is to be removed
 */
SharedMap.prototype.delete = function (key) {
    this.map.delete(requireNotNull(key, 'key'));
};

/**
 * Attempts to compute a mapping for the specified key and its current mapped value.
 *
 * @param {object} params JSON with the parameters.
 * @param {string} params.key  key of the entry
 * @param {function} params.func mapping function that accepts the existing mapped value (or null, if there is no associated mapping).
 *                            The returned value replaces the existing mapped value for the specified key.
 *                            If returned value is null then the value is removed from the map
 * @param {number} [params.ttlSeconds] maximum time to live in seconds for this entry to stay in the map. (0 means infinite, negative means map config default or infinite if map config is not available)
 * @returns the new value to which the specified key is mapped, or null if this map no longer contains mapping for the key
 */
SharedMap.prototype.modify = function (params) {
    var key = requireNotNull(params.key, 'key');
    var func = requireNotNull(params.func, 'func');
    if (typeof func !== 'function') {
        throw 'Parameter "func" is not a function';
    }
    var ttlSeconds = __.nullOrValue(params.ttlSeconds);

    var modifierFn = function (oldValue) {
        return convertValue(func.call(this, __.toNativeObject(oldValue)));
    };

    if (ttlSeconds === null) {
        return __.toNativeObject(this.map.modify(key, modifierFn));
    } else {
        return __.toNativeObject(this.map.modify(key, modifierFn, ttlSeconds));
    }
};

/**
 * Returns an instance of SharedMap by the specified map identifier.
 *
 * @param {string} mapId map identifier
 * @returns {SharedMap} an instance of SharedMap
 */
exports.getMap = function (mapId) {
    return new SharedMap(requireNotNull(mapId, 'mapId'));
};
