Ext.define( 'Admin.view.datadesigner.FilterPanel', {
    extend: 'Ext.form.Panel',
    alias: 'widget.filterPanel',
    layout: 'form',
    frame: true,
    bodyPadding: 5,
    title: 'Filter',

    items: [
        {
            xtype: 'textfield',
            itemId: 'searchTextField',
            emptyText: 'Search',
            enableKeyEvents: true,
            flex: 1
        },
        {
            xtype: 'checkbox',
            boxLabel: 'Show base types only',
            inputValue: '1',
            itemId: 'showBaseTypesOnlyCheckbox'
        }
    ],

    initComponent: function()
    {
        this.callParent( arguments );
    }

});
