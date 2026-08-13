const t = require('/lib/xp/testing');
const adminLib = require('/lib/xp/admin');

exports.testWidgetUrl = function () {
    const result = adminLib.widgetUrl({
        application: 'myapp',
        widget: 'mywidget',
        params: {
            k1: 'v1',
            k2: ['v21', 'v22'],
        }
    });

    t.assertEquals("generated_url", result);
};

exports.testExtensionUrl = function () {
    const result = adminLib.extensionUrl({
        application: 'myapp',
        extension: 'myExtension',
        params: {
            k1: 'v1',
            k2: ['v21', 'v22'],
        }
    });

    t.assertEquals("generated_url", result);
};

exports.testWidgetUrlWithoutParams = function () {
    const result = adminLib.widgetUrl({
        application: 'myapp',
        widget: 'mywidget',
    });

    t.assertEquals("generated_url", result);
};

exports.testGetToolUrl = function () {
    const result = adminLib.getToolUrl('myapp', 'mytool');
    t.assertEquals("generated_url", result);
};

exports.getHomeToolUrl = function () {
    const result = adminLib.getHomeToolUrl({
        type: 'absolute',
    });
    t.assertEquals("generated_url", result);
};

exports.createTopic = function () {
    const result = adminLib.createTopic({
        name: 'myTopic',
        allow: ['role:system.admin.login']
    });

    t.assertEquals('myapplication:myTopic', result);
};

exports.createTopicWithoutAllow = function () {
    adminLib.createTopic({
        name: 'myTopic'
    });
};

exports.sendToTopic = function () {
    adminLib.sendToTopic('myTopic', {
        count: 42
    });
};

exports.sendToTopicWithoutMessage = function () {
    adminLib.sendToTopic('myTopic');
};
