Ext.define('Admin.lib.inputtypes.Relation', {
    extend: 'Ext.form.field.ComboBox',
    alias: 'widget.input.Relation',
    label: 'Relation',
    hideTrigger: true,
    width: 580,
    forceSelection: true,
    minChars: 1,
    queryMode: 'remote',

    // Hardcoded store for now.
    displayField: 'title',
    valueField: 'key',
    store: Ext.create('Ext.data.Store', {
        fields: ['key', 'title'],
        proxy: {
            type: 'ajax',
            url: 'related-articles.json',
            reader: {
                type: 'json',
                root: 'articles'
            }
        }
    })
});