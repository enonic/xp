describe("api.i18n", () => {

    it("test no translation", () => {
        let message = api.i18n.message('no translation for this', []);
        expect(message).toBe('no translation for this');
    });

    it("test no translation with arguments", () => {
        let message = api.i18n.message('no $1 for $2', ['translation', 'this']);
        expect(message).toBe('no translation for this');
    });

    it("test translation", () => {

        api.i18n.setLocale('no');
        api.i18n.addBundle('no', {
            'translation for this': 'oversetting for dette'
        });

        let message = api.i18n.message('translation for this', []);
        expect(message).toBe('oversetting for dette');
    });

    it("test translation with arguments", () => {

        api.i18n.setLocale('no');
        api.i18n.addBundle('no', {
            '$1 for this': '$1 for dette'
        });

        let message = api.i18n.message('$1 for this', ['oversetting']);
        expect(message).toBe('oversetting for dette');
    });

    it("test merge bundles", () => {

        api.i18n.setLocale('no');

        api.i18n.addBundle('no', {
            'translate this': 'oversett dette'
        });

        api.i18n.addBundle('no', {
            'and translate this': 'og oversett dette'
        });

        let message1 = api.i18n.message('translate this', []);
        expect(message1).toBe('oversett dette');

        let message2 = api.i18n.message('and translate this', []);
        expect(message2).toBe('og oversett dette');
    });

});
