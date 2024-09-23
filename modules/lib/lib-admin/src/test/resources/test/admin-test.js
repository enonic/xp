const t = require('/lib/xp/testing');

t.mock('/lib/xp/admin', {
    widgetUrl(params) {
        return 'generated_url';
    }
});

exports.testWidgetUrl = function () {
    const adminLib = require('/lib/xp/admin');

    const result = adminLib.widgetUrl({
        application: 'app',
        widget: 'widget',
    });

    t.assertEquals("generated_url", result);
};
