var appLib = require('/lib/xp/app');
var assert = require('/lib/xp/testing');

exports.getWithoutIcon = function () {
    var result = appLib.getDescriptor({
        key: '123456',
    });

    assert.assertJsonEquals({
        'key': '123456',
        'description': 'my app description',
        'title': 'Title',
        'titleI18nKey': 'app.title.key',
        'vendorName': 'Vendor Name',
        'vendorUrl': 'vendorUrl'
    }, result);
};

exports.getMissing = function () {
    var result = appLib.getDescriptor({
        key: 'missing',
    });

    assert.assertNull(result);
}
