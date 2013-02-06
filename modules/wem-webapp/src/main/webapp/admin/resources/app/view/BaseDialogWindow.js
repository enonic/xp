Ext.define('Admin.view.BaseDialogWindow', {
    extend: 'Ext.container.Container',
    alias: 'widget.baseDialogWindow',

    border: false,
    floating: true,
    shadow: false,
    width: 500,
    modal: true,
    autoHeight: true,
    maxHeight: 600,
    cls: 'admin-window',
    closeAction: 'hide',

    modelData: undefined,
    dialogTitle: 'Base dialog',
    dialogSubTitle: '',
    dialogInfoTpl: Templates.common.userInfo,

    listeners: {
        show: function (cmp) {
            var form = cmp.down('form');
            if (form) {
                form.getForm().reset();
                form.doLayout();
                var firstField = form.down('field');
                if (firstField) {
                    firstField.focus();
                }
            }
        },
        resize: function (window) {
            // Support maxHeight which is not actually supported for Window with autoHeight set to true.
            if (this.getHeight() > this.maxHeight) {
                this.setHeight(this.maxHeight);
            }

            this.center();
        }
    },

    initComponent: function () {

        var me = this;

        if (!this.dockedItems) {
            this.dockedItems = [];
        }
        Ext.Array.insert(this.dockedItems, 0, [
            {
                xtype: 'toolbar',
                dock: 'right',
                autoHeight: true,
                items: [
                    {
                        itemId: 'closeButton',
                        scale: 'medium',
                        iconAlign: 'top',
                        text: 'Close',
                        action: 'close',
                        iconCls: 'icon-close',
                        listeners: {
                            click: function (btn, evt) {
                                me.close();
                            }
                        }
                    }
                ]
            }
        ]);

        if (!this.items) {
            this.items = [];
        }

        if (this.dialogTitle) {
            this.setDialogHeader(this.dialogTitle);
        }
        if (this.dialogSubTitle) {
            this.setDialogSubHeader(this.dialogSubTitle);
        }
        if (this.dialogInfoTpl) {
            this.setDialogInfo(this.dialogInfoTpl);
        }

        this.callParent(arguments);
    },


    filterItem: function (id) {
        return Ext.Array.filter(this.items, function (item) {
            return item.itemId !== id;
        });
    },

    setDialogHeader: function (title) {

        var headerItems = [];

        headerItems.push(this.createTitle(title));
        Ext.Array.each(this.buttons, function (b, i) {
            headerItems.push(this.buttons[i]);
        });
        headerItems.push(this.createCloseButton());

        this.items = this.filterItem('dialogTitle');

        Ext.Array.insert(this.items, 0, [
            {
                xtype: 'container',
                itemId: 'dialogTitle',
                cls: 'admin-window-header',
                padding: '5 0 5 5',
                layout: {
                    type: 'hbox',
                    align: 'stretch'
                },
                defaults: {
                    margin: '0 5 0 0'
                },
                items: headerItems
            }
        ]);
        this.doLayout();
    },

    setDialogSubHeader: function (title) {
        var i = this.dialogTitle ? 1 : 0;

        this.items = this.filterItem('dialogSubTitle');

        Ext.Array.insert(this.items, i, [
            {
                xtype: 'component',
                itemId: 'dialogSubTitle',
                cls: 'admin-window-subheader',
                html: title
            }
        ]);
        this.doLayout();
    },

    setDialogInfo: function (tpl) {
        var i = 0;
        if (this.dialogTitle) {
            i++;
        }
        if (this.dialogSubTitle) {
            i++;
        }

        this.items = this.filterItem('dialogInfo');

        Ext.Array.insert(this.items, i, [
            {
                itemId: 'dialogInfo',
                cls: 'dialog-info',
                xtype: 'component',
                border: false,
                height: 80,
                styleHtmlContent: true,
                tpl: new Ext.XTemplate(tpl)
            }
        ]);
    },

    setDialogInfoTpl: function (tpl) {
        var dialogInfo = this.down('#dialogInfo');
        if (dialogInfo) {
            dialogInfo.tpl = new Ext.XTemplate(tpl);
        } else {
            this.setDialogInfo(tpl);
        }
    },

    setDialogInfoData: function (model) {
        if (model) {
            this.modelData = model.data;
            var info = this.down('#dialogInfo');
            if (info) {
                info.update(this.modelData);
            }

        }
    },


    doShow: function (model) {
        this.setDialogInfoData(model);
        this.show();
    },

    doHide: function () {
        this.x = -this.width;
        this.hide();
    },

    close: function () {
        this.destroy();
    },

    createTitle: function (title) {

        return {
            xtype: 'component',
            flex: 1,
            cls: this.iconCls,
            autoEl: {
                tag: 'h1',
                html: title
            }
        };
    },

    createCloseButton: function () {
        var me = this;
        return {
            xtype: 'button',
            ui: 'grey',
            text: 'Close',
            handler: function (btn) {
                me.close();
            }
        };
    }

});

