var appLib = require('/lib/xp/app');
var assert = require('/lib/xp/testing');

/* global testInstance */

exports.createWhenMissing = function () {
    var result = appLib.createOrUpdate({
        key: 'new_app',
        editor: function (descriptor) {
            assert.assertJsonEquals({key: 'new_app', description: '', config: {}}, descriptor);
            descriptor.title = 'Fresh';
            return descriptor;
        }
    });

    assert.assertJsonEquals({key: 'new_app', description: '', title: 'Fresh', config: {}}, result);
};

exports.keepUntouched = function () {
    var result = appLib.createOrUpdate({
        key: 'my_app',
        editor: function (descriptor) {
            assert.assertEquals('Old title', descriptor.title);
            assert.assertEquals('image/png', descriptor.icon.mimeType);
            descriptor.title = 'New title';
            return descriptor;
        }
    });

    assert.assertEquals('New title', result.title);
    assert.assertEquals('app.title', result.titleI18nKey);
    assert.assertEquals('Old description', result.description);
    assert.assertEquals('image/png', result.icon.mimeType);
};

exports.clearWithNull = function () {
    appLib.createOrUpdate({
        key: 'my_app',
        editor: function (descriptor) {
            descriptor.title = null;
            descriptor.titleI18nKey = null;
            descriptor.vendorUrl = null;
            descriptor.config = null;
            return descriptor;
        }
    });
};

exports.setConfig = function () {
    appLib.createOrUpdate({
        key: 'my_app',
        editor: function (descriptor) {
            descriptor.config = {
                text: 'value',
                count: 7,
                ratio: 1.5,
                flag: false,
                nothing: null,
                nested: {inner: 'x', skipped: null},
                list: ['a', 2]
            };
            return descriptor;
        }
    });
};

exports.rejectNonObjectConfig = function () {
    assert.assertThrows(function () {
        appLib.createOrUpdate({
            key: 'my_app',
            editor: function (descriptor) {
                descriptor.config = 'not an object';
                return descriptor;
            }
        });
    });
};

exports.removeIcon = function () {
    var result = appLib.createOrUpdate({
        key: 'my_app',
        editor: function (descriptor) {
            descriptor.icon = null;
            return descriptor;
        }
    });

    assert.assertNull(result.icon);
};

exports.replaceIcon = function () {
    var result = appLib.createOrUpdate({
        key: 'my_app',
        editor: function (descriptor) {
            descriptor.icon = {
                data: testInstance.createByteSource('<svg>new</svg>'),
                mimeType: 'image/svg+xml'
            };
            return descriptor;
        }
    });

    assert.assertEquals('image/svg+xml', result.icon.mimeType);
};

exports.changeIconMimeType = function () {
    appLib.createOrUpdate({
        key: 'my_app',
        editor: function (descriptor) {
            descriptor.icon.mimeType = 'image/svg+xml';
            return descriptor;
        }
    });
};

exports.rejectIconWithoutData = function () {
    assert.assertThrows(function () {
        appLib.createOrUpdate({
            key: 'my_app',
            editor: function (descriptor) {
                descriptor.icon = {mimeType: 'image/svg+xml'};
                return descriptor;
            }
        });
    });
};

exports.requireKeyAndEditor = function () {
    assert.assertThrows(function () {
        appLib.createOrUpdate({editor: function (d) { return d; }});
    });
    assert.assertThrows(function () {
        appLib.createOrUpdate({key: 'my_app'});
    });
};