/**
 * Grid related functions.
 *
 * @example
 * var gridLib = require('/lib/xp/grid');
 *
 * @module grid
 */
import {ScriptValue} from '@enonic-types/core';

declare global {
    interface XpLibraries {
        '/lib/xp/grid': typeof import('./grid');
    }
}

export type SharedMapValueType = string | number | boolean | object | null;

export type GridMap = Record<string, SharedMapValueType>;

export type SharedMapModifierFn<Map extends GridMap, Key extends keyof Map> = (value: Map[Key] | null) => ConvertedType;

type ConvertedType = SharedMapValueType | Record<string, unknown> | object[];

interface JavaSharedMap<Map extends GridMap> {
    get<Key extends keyof Map>(key: keyof Map): Map[Key] | null;

    delete(key: keyof Map): void;

    set<Key extends keyof Map>(key: Key, value: ConvertedType, ttlSeconds?: number | null): void;

    modify<Key extends keyof Map>(key: Key, modifier: SharedMapModifierFn<Map, Key>, ttlSeconds?: number | null): Map[Key];
}

interface SharedMapHandler<Map extends GridMap> {
    getMap(mapId: string): JavaSharedMap<Map>;
}

export interface SetParams<Map extends GridMap, Key extends keyof Map> {
    key: Key;
    value: Map[Key] | null;
    ttlSeconds?: number;
}

export interface ModifyParams<Map extends GridMap, Key extends keyof Map> {
    key: Key;
    func: SharedMapModifierFn<Map, Key>;
    ttlSeconds?: number;
}

export interface SharedMap<Map extends GridMap> {
    get<Key extends keyof Map>(key: Key): Map[Key] | null;

    set<Key extends keyof Map>(params: SetParams<Map, Key>): void;

    modify<Key extends keyof Map>(params: ModifyParams<Map, Key>): Map[Key];

    delete(key: keyof Map): void;
}

/**
 * Shared Map is similar to other Map, but its instances are shared across all applications and even cluster nodes.
 *
 *  WARNING: Due to distributed nature of the Shared Map not all types of keys and values can be used.
 *  Strings, numbers, and pure JSON objects are supported. There is no runtime check for type compatibility due to performance reasons.
 *  The developer is also responsible for not modifying shared objects (keys and values) in place.
 *
 * @constructor
 * @hideconstructor
 * @alias SharedMap
 */
class SharedMapImpl<Map extends GridMap>
    implements SharedMap<Map> {

    private map: JavaSharedMap<Map>;

    constructor(mapId: string) {
        const bean: SharedMapHandler<Map> = __.newBean('com.enonic.xp.lib.grid.SharedMapHandler');
        this.map = bean.getMap(mapId);
    }

    /**
     * Returns the value to which the specified key is mapped, or null if this map contains no mapping for the key.
     *
     * @param {string} key - key the key whose associated value is to be returned
     * @returns {string|number|boolean|JSON|null} the value to which the specified key is mapped, or null if this map contains no mapping for the key
     */
    get<Key extends keyof Map>(key: Key): Map[Key] | null {
        return __.toNativeObject(this.map.get(key));
    }

    /**
     * Puts an entry into this map with a given time to live (TTL).
     * If value is null, the existing entry will be removed.
     *
     * @param {object} params JSON with the parameters.
     * @param {string} params.key key of the entry
     * @param {string|number|boolean|JSON|null} params.value value of the entry
     * @param {number} [params.ttlSeconds] maximum time to live in seconds for this entry to stay in the map. (0 means infinite, negative means map config default or infinite if map config is not available)
     */
    set(params: SetParams<Map, keyof Map>): void {
        const key = requireNotNull(params.key, 'key');
        const ttlSeconds = __.nullOrValue(params.ttlSeconds);
        const value = convertValue(params.value);
        if (ttlSeconds === null) {
            this.map.set(key, value);
        } else {
            this.map.set(key, value, ttlSeconds);
        }
    }

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
    modify<Key extends keyof Map>(params: ModifyParams<Map, Key>): Map[Key] {
        const key = requireNotNull(params.key, 'key');
        const func = requireNotNull(params.func, 'func');
        if (typeof func !== 'function') {
            throw 'Parameter "func" is not a function';
        }

        const ttlSeconds = __.nullOrValue(params.ttlSeconds);

        const modifierFn = (oldValue: Map[Key] | null): ConvertedType => {
            return convertValue(func.call(this, __.toNativeObject(oldValue)));
        };

        if (ttlSeconds === null) {
            return __.toNativeObject(this.map.modify(key, modifierFn));
        } else {
            return __.toNativeObject(this.map.modify(key, modifierFn, ttlSeconds));
        }
    }

    /**
     * Removes the mapping for the key from this map if it is present.
     *
     * @param {string} key the key whose associated value is to be removed
     */
    delete(key: keyof Map): void {
        this.map.delete(key);
    }
}

function requireNotNull<T>(value: T, parameterName: string): T {
    if (value == null) {
        throw `Parameter "${parameterName}" is required`;
    }
    return value;
}

function convertValue(value: SharedMapValueType): ConvertedType {
    if (typeof value === 'undefined' || value === null) {
        return null;
    } else if (Array.isArray(value)) {
        const sv: ScriptValue = __.toScriptValue(value);
        return sv.getList();
    } else if (typeof value === 'object') {
        const sv: ScriptValue = __.toScriptValue(value);
        return sv.getMap();
    } else {
        return value;
    }
}

/**
 * Returns an instance of SharedMap by the specified map identifier.
 *
 * @param {string} mapId map identifier
 * @returns {SharedMap} an instance of SharedMap
 */
export function getMap<Map extends GridMap>(mapId: string): SharedMap<Map> {
    return new SharedMapImpl<Map>(requireNotNull(mapId, 'mapId'));
}

