Ext.define('Admin.view.contentManager.wizard.form.ImagePopupDialog', {
    extend: 'Ext.Container',
    alias: 'widget.imagePopupDialog',

    layout: {
        type: 'vbox',
        align: 'stretch'
    },

    height: 150,
    //width: 500,
    //modal: false,

    style: {
        backgroundColor: 'grey',
        clear: 'both'
    },

    tpl: "<div style='text-align: center'><h1>{displayName}</h1><p>{path}</p></div>",

    defaultType: 'container',

    initComponent: function () {

        this.items = [

            {
                tpl: this.tpl,
                itemId: 'messageBox',
                data: this.data
            },
            {
                items: [
                    {
                        xtype: 'button',
                        text: 'Edit'
                    },
                    {
                        xtype: 'button',
                        text: 'Remove',
                        ui: 'grey'
                    }
                ]
            }
        ];
        this.callParent(arguments);
    },

    updateTpl: function (data) {
        this.down('#messageBox').update(data);
    }

});