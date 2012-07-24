Ext.define('Admin.view.BaseDialogWindow', {
    extend: 'Ext.window.Window',
    alias: 'widget.baseDialogWindow',

    border: false,
    padding: 1,

    draggable: false,
    closable: false,
    width: 500,
    modal: true,
    modelData: undefined,

    autoHeight: true,
    maxHeight: 350,
    autoScroll: true,

    cls: 'admin-dialog-window',
    closeAction: 'hide',
    bodyPadding: 10,
    bodyStyle: 'background: #fff;',

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

        if (!me.dockedItems) {
            me.dockedItems = [];
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

        if (!me.items) {
            me.items = [];
        }

        if (me.dialogTitle || me.dialogSubTitle) {
            var dialogHtml = '<h3>' + me.dialogTitle + '</h3>';
            dialogHtml += (Ext.isEmpty(me.dialogSubTitle)) ? '' : '<h4>' + me.dialogSubTitle + '</h4>';
            me.createDialogHeader(dialogHtml);
        }

        if (me.dialogInfoTpl) {
            me.createDialogInfo(me.dialogInfoTpl);
        }

        this.callParent(arguments);
    },

    createDialogHeader: function (title) {
        Ext.Array.insert(this.items, 0, [
            {
                itemId: 'dialogHeader',
                xtype: 'component',
                cls: 'dialog-header',
                styleHtmlContent: true,
                html: title
            }

        ]);
    },

    createDialogInfo: function (tpl) {
        Ext.Array.insert(this.items, 1, [
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

    setDialogHeader: function (title) {
        var dialogHeader = this.down('#dialogHeader');
        if (dialogHeader) {
            dialogHeader.update(title);
        } else {
            this.createDialogHeader(title);
        }
    },

    setDialogInfoTpl: function (tpl) {
        var dialogInfo = this.down('#dialogInfo');
        if (dialogInfo) {
            dialogInfo.tpl = new Ext.XTemplate(tpl);
        } else {
            this.createDialogInfo(tpl);
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
    }

});

