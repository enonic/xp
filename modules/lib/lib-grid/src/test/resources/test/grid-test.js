var gridLib = require('/lib/xp/grid');
var assert = require('/lib/xp/testing');

exports.testGetWithoutKey = function () {
    var memoryGrid = gridLib.getMap('mapId');
    try {
        memoryGrid.get();
    } catch (e) {
        assert.assertEquals('Parameter "key" is required', e);
    }
};

exports.testGet = function () {
    var memoryGrid = gridLib.getMap('mapId');
    assert.assertEquals('value', memoryGrid.get('key'));
};

exports.testDeleteWithoutKey = function () {
    var memoryGrid = gridLib.getMap('mapId');
    try {
        memoryGrid.delete();
    } catch (e) {
        assert.assertEquals('Parameter "key" is required', e);
    }
};

exports.testDelete = function () {
    var memoryGrid = gridLib.getMap('mapId');
    memoryGrid.delete('key');
};

exports.testSetWithoutKey = function () {
    var memoryGrid = gridLib.getMap('mapId');
    try {
        memoryGrid.set({});
    } catch (e) {
        assert.assertEquals('Parameter "key" is required', e);
    }
};

exports.testSetWithoutTtlSeconds = function () {
    var memoryGrid = gridLib.getMap('mapId');

    memoryGrid.set({
        key: 'key',
        value: 'value'
    });
};

exports.testSet = function () {
    var memoryGrid = gridLib.getMap('mapId');

    memoryGrid.set({
        key: 'key',
        value: 'value',
        ttlSeconds: 2 * 60 * 1000
    });
};

exports.testModifyWithoutKey = function () {
    var memoryGrid = gridLib.getMap('mapId');
    try {
        memoryGrid.modify({});
    } catch (e) {
        assert.assertEquals('Parameter "key" is required', e);
    }
};

exports.testModifyWithoutFunc = function () {
    var memoryGrid = gridLib.getMap('mapId');
    try {
        memoryGrid.modify({
            key: 'key'
        });
    } catch (e) {
        assert.assertEquals('Parameter "func" is required', e);
    }
};

exports.testModifyWithWrongArgumentFunc = function () {
    var memoryGrid = gridLib.getMap('mapId');
    try {
        memoryGrid.modify({
            key: 'key',
            func: 1
        });
    } catch (e) {
        assert.assertEquals('Parameter "func" is not a function', e);
    }
};
