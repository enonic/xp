Ext.define('App.view.GridPanel', {
    extend: 'Ext.grid.Panel',
    alias : 'widget.languageGrid',

    requires: [
        'App.view.Toolbar',
        'Admin.plugin.PageSizePlugin'
    ],
    layout: 'fit',
    loadMask: true,
    columnLines: true,
    frame: false,
    store: 'LanguageStore',

    initComponent: function() {

        this.columns = [
            {
                text: 'Language Code',
                dataIndex: 'languageCode',
                sortable: true,
                width: 100,
                align: 'right',
                field: {
                    allowBlank: false
                }
            },
            {
                text: 'Description',
                dataIndex: 'description',
                sortable: true,
                flex: 1,
                field: {
                    allowBlank: false
                }
            }
        ];

        this.bbar = {
            xtype: 'pagingtoolbar',
            store: this.store,
            displayInfo: true,
            displayMsg: 'Displaying languages {0} - {1} of {2}',
            emptyMsg: 'No languages to display',
            plugins: ['pageSize']
        };

        this.tbar = {
            xtype: 'languageToolbar'
        };

        this.viewConfig = {
            trackOver : true,
            stripeRows: true
        };

        var cellEditing = Ext.create('Ext.grid.plugin.CellEditing', {
            pluginId: 'cellEditor',
            clicksToEdit: 1
        });

        this.plugins = [cellEditing];

        this.callParent(arguments);
    }

});
