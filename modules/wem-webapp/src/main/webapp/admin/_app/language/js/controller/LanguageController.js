Ext.define('App.controller.LanguageController', {
    extend: 'Ext.app.Controller',

    stores: ['LanguageStore'],
    models: ['LanguageModel'],
    views: ['Toolbar', 'GridPanel'],

    init: function () {
        this.control({
            '*[action=newLanguage]': {
                click: this.newLanguage
            }
        });
    },

    newLanguage: function () {
        var editor = this.getLanguageGrid().getPlugin('cellEditor');
        editor.cancelEdit();
        var r = Ext.ModelManager.create({
            key: '',
            languageCode: '',
            description: '',
            lastModified: new Date()
        }, 'App.model.LanguageModel');
        this.getLanguageStoreStore().insert(0, r);
        editor.startEditByPosition({row: 0, column: 0});
    },

    getLanguageGrid: function () {
        return Ext.ComponentQuery.query('languageGrid')[0];
    }

});
