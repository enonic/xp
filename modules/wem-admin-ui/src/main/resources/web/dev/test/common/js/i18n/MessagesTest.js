describe("api.i18nTest", function () {

    it("test no translation", function () {
        var message = api.i18n.message('no translation for this', []);
        expect(message).toBe('no translation for this');
    });

    it("test no translation with arguments", function () {
        var message = api.i18n.message('no $1 for $2', ['translation', 'this']);
        expect(message).toBe('no translation for this');
    });

    it("test translation", function () {

        api.i18n.setLocale('no');
        api.i18n.addBundle('no', {
            'translation for this': 'oversetting for dette'
        });

        var message = api.i18n.message('translation for this', []);
        expect(message).toBe('oversetting for dette');
    });

    it("test translation with arguments", function () {

        api.i18n.setLocale('no');
        api.i18n.addBundle('no', {
            '$1 for this': '$1 for dette'
        });

        var message = api.i18n.message('$1 for this', ['oversetting']);
        expect(message).toBe('oversetting for dette');
    });

    it("test merge bundles", function () {

        api.i18n.setLocale('no');

        api.i18n.addBundle('no', {
            'translate this': 'oversett dette'
        });

        api.i18n.addBundle('no', {
            'and translate this': 'og oversett dette'
        });

        var message1 = api.i18n.message('translate this');
        expect(message1).toBe('oversett dette');

        var message2 = api.i18n.message('and translate this');
        expect(message2).toBe('og oversett dette');
    });

});
