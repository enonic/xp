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

exports.testGetTools = function () {
    const result = adminLib.getTools();
    t.assertNotNull(result);
    t.assertTrue(Array.isArray(result));
};

exports.testGetToolsWithLocales = function () {
    const result = adminLib.getTools({
        locales: ['en', 'no']
    });
    t.assertNotNull(result);
    t.assertTrue(Array.isArray(result));
};
