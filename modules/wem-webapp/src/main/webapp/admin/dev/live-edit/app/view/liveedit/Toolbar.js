Ext.define('Admin.view.liveedit.Toolbar', {
    extend: 'Ext.toolbar.Toolbar',
    alias: 'widget.liveeditToolbar',

    border: false,

    initComponent: function () {
        var buttonDefaults = {
            scale: 'medium',
            iconAlign: 'top',
            minWidth: 64
        };

        // Dummy store for the combo
        var regions = Ext.create('Ext.data.Store', {
            fields: ['name', 'selector'],
            data: [
                {"name": "North", "selector": "#north"},
                {"name": "West", "selector": "#west"},
                {"name": "Center", "selector": "#center"},
                {"name": "East", "selector": "#east"},
                {"name": "South", "selector": "#south"}
            ]
        });


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

        this.callParent(arguments);
    }

});
