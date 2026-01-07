var gridLib = require('/lib/xp/grid');
var assert = require('/lib/xp/testing');

exports.testGetWithoutKey = function () {
    var sharedMap = gridLib.getMap('mapId');
    try {
        sharedMap.get();
    } catch (e) {
        assert.assertEquals('Parameter "key" is required', e.message);
    }
};

exports.testGet = function () {
    var sharedMap = gridLib.getMap('mapId');
    assert.assertEquals('value', sharedMap.get('key'));
};

exports.testDeleteWithoutKey = function () {
    var sharedMap = gridLib.getMap('mapId');
    try {
        sharedMap.delete();
    } catch (e) {
        assert.assertEquals('Parameter "key" is required', e.message);
    }
};

exports.testDelete = function () {
    var sharedMap = gridLib.getMap('mapId');
    sharedMap.delete('key');
};

exports.testSetWithoutKey = function () {
    var sharedMap = gridLib.getMap('mapId');
    try {
        sharedMap.set({});
    } catch (e) {
        assert.assertEquals('Parameter "key" is required', e.message);
    }
};

exports.testSetWithoutTtlSeconds = function () {
    var sharedMap = gridLib.getMap('mapId');

    sharedMap.set({
        key: 'key',
        value: 'value'
    });
};

exports.testSet = function () {
    var sharedMap = gridLib.getMap('mapId');

    sharedMap.set({
        key: 'key',
        value: 'value',
        ttlSeconds: 2 * 60 * 1000
    });
};

exports.testSetWithNullValue = function () {
    var sharedMap = gridLib.getMap('mapId');

    sharedMap.set({
        key: 'key',
        value: null,
        ttlSeconds: -1
    });
};

exports.testModifyWithoutKey = function () {
    var sharedMap = gridLib.getMap('mapId');
    try {
        sharedMap.modify({});
    } catch (e) {
        assert.assertEquals('Parameter "key" is required', e.message);
    }
};

exports.testModifyWithoutFunc = function () {
    var sharedMap = gridLib.getMap('mapId');
    try {
        sharedMap.modify({
            key: 'key'
        });
    } catch (e) {
        assert.assertEquals('Parameter "func" is required', e.message);
    }
};

exports.testModifyWithWrongArgumentFunc = function () {
    var sharedMap = gridLib.getMap('mapId');
    try {
        sharedMap.modify({
            key: 'key',
            func: 1
        });
    } catch (e) {
        assert.assertEquals('Parameter "func" is not a function', e.message);
    }
};
