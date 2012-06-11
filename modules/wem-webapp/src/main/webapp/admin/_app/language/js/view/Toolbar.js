Ext.define('App.view.Toolbar', {
    extend: 'Ext.toolbar.Toolbar',
    alias: 'widget.languageToolbar',

    items: [
        {
            text: 'New',
            iconCls: 'icon-new',
            action: 'newLanguage'
        }
    ],

    initComponent: function() {
        this.callParent(arguments);
    }
});

