Ext.define( 'Admin.view.liveedit.Toolbar', {
    extend: 'Ext.toolbar.Toolbar',
    alias: 'widget.liveeditToolbar',

    border: false,

    initComponent: function()
    {
        var buttonDefaults = {
            scale: 'medium',
            iconAlign: 'top',
            minWidth: 64
        };

        this.items = [

            {
                xtype: 'buttongroup',
                columns: 1,
                defaults: buttonDefaults,
                items: [
                    {
                        xtype: 'button',
                        itemId: 'saveButton',
                        text: 'Save'
                    }
                ]
            }
        ];

        this.callParent( arguments );
    }

});
