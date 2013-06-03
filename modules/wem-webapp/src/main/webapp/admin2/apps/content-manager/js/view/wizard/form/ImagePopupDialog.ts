Ext.define('Admin.view.contentManager.wizard.form.ImagePopupDialog', {
    extend: 'Ext.Container',
    alias: 'widget.imagePopupDialog',

    layout: {
        type: 'vbox',
        align: 'stretch'
    },

    height: 150,

    cls: 'admin-inputimage-dlg',

    tpl: "<div style='text-align: center'><h1>{displayName}</h1><p>{path}</p></div>",

    defaultType: 'container',

    padding: '20 0 0 0',

    removeHandler: Ext.emptyFn(),

    editHandler: Ext.emptyFn(),

    initComponent: function () {

        this.items = <any[]>[
            {
                tpl: this.tpl,
                itemId: 'messageBox',
                data: this.buildTemplateData(this.data)
            },
            {
                layout: {
                    type: 'hbox',
                    pack: 'center'
                },
                items: [
                    {
                        xtype: 'button',
                        text: 'Edit',
                        cls: 'icon-button',
                        scale: 'medium',
                        width: 150,
                        margin: '5 5',
                        listeners: {
                            click: this.editHandler
                        }
                    },
                    {
                        xtype: 'button',
                        text: 'Remove',
                        cls: 'icon-button',
                        scale: 'medium',
                        width: 150,
                        margin: '5 5',
                        style: {
                            borderColor: '#7A7A7A',
                            backgroundColor: '#7A7A7A'
                        },
                        listeners: {
                            click: this.removeHandler
                        }
                    }
                ]
            }
        ];
        this.callParent(arguments);
    },

    buildTemplateData: function (data) {
        return {
            displayName: Ext.String.ellipsis(data.displayName, 25),
            path: Ext.String.ellipsis(data.path, 50)
        };
    },

    updateTpl: function (data) {
        this.down('#messageBox').update(this.buildTemplateData(data));
    }

});