Ext.define( 'Admin.view.account.GroupItemField', {
    extend: 'Ext.form.FieldContainer',
    alias: 'widget.groupItemField',

    layout: {
        type: 'hbox'
    },

    width: 400,

    initComponent: function()
    {

        this.items = [
            {
                xtype: 'hidden',
                name: 'membership',
                value: this.groupId
            },
            {
                xtype: 'label',
                text: this.title,
                cls: 'group-item',
                flex: 1
            },
            {
                xtype: 'button',
                iconCls: 'icon-delete',
                action: 'deleteGroup'
            }
        ];
        this.callParent( arguments );
    }

} );