Ext.define('Admin.view.contentManager.DeleteContentWindow', {
    extend: 'Admin.view.BaseDialogWindow',
    alias: 'widget.deleteContentWindow',

    dialogTitle: undefined,

    /*items: [
     {
     margin: '10px 0 10px 0px',
     xtype: 'container',
     defaults: {
     xtype: 'button',
     scale: 'medium',
     margin: '0 10 0 0'
     },
     items: [
     {
     text: 'Delete',
     iconCls: 'icon-delete-user-24',
     itemId: 'deleteContentButton',
     action: 'deleteContent'
     }
     ]
     }
     ],*/
    padding: 20,

    initComponent: function () {
        var me = this;
        this.items = [
            {
                region: 'north',
                xtype: 'component',
                tpl: '<h2>{title}</h2>',
                data: {
                    title: 'Delete content(s)'
                },
                margin: '0 0 20 0'
            },
            {
                region: 'center',
                layout: {
                    type: 'vbox',
                    align: 'stretch'
                },
                border: false,
                items: {
                    itemId: 'modalDialog',
                    cls: 'dialog-info',
                    xtype: 'component',
                    border: false,
                    height: 150,
                    styleHtmlContent: true,
                    tpl: me.deleteTemplate
                }
            },
            {
                region: 'south',
                margin: '20 0 0 0',
                border: false,
                layout: {
                    type: 'hbox',
                    pack: 'end'
                },
                items: [
                    {
                        xtype: 'button',
                        text: 'Delete',
                        action: 'deleteContent',
                        margin: '0 10 0 0'
                    },
                    {
                        xtype: 'button',
                        text: 'Cancel',
                        handler: function (btn, evt) {
                            me.close();
                        }
                    }
                ]
            }
        ];

        this.callParent(arguments);
    },

    deleteTemplate: '<div class="delete-container">' +
                    '<tpl for=".">' +
                    '<div class="delete-item">' +
                    '<img class="icon" src="{data.iconUrl}"/>' +
                    '<h4>{data.displayName}</h4>' +
                    '<p>{data.type}</p>' +
                    '</div>' +
                    '</tpl>' +
                    '</div>',

    setModalDialogData: function (model) {
        console.log("Model");
        console.log(model);
        this.modelData = model[0].data;
        if (model) {
            var info = this.down('#modalDialog');
            if (info) {
                info.update(model);
            }

        }
    },

    doShow: function (selection) {
        this.setModalDialogData(selection);
        this.show();
    }
});