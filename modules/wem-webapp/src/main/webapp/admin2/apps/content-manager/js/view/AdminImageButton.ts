Ext.define('Admin.view.AdminImageButton', {
    extend: 'Ext.button.Button',
    alias: 'widget.adminImageButton',

    cls: 'admin-image-button',
    scale: 'large',

    popupTpl: undefined,
    popupData: undefined,

    listeners: {
        click: function (item) {
            if (!item.popupPanel) {
                item.popupPanel = <any> Ext.create("Ext.panel.Panel", {
                    floating: true,
                    cls: 'admin-toolbar-popup',
                    border: false,
                    tpl: item.popupTpl,
                    data: item.popupData,
                    styleHtmlContent: true,
                    renderTo: Ext.getBody(),
                    listeners: {
                        afterrender: function (cont) {
                            cont.show();
                            cont.setPagePosition(cont.el.getAlignToXY(item.el, "tr-br?"));
                        }
                    }

                });
            } else {
                if (item.popupPanel.isHidden()) {
                    item.popupPanel.show();
                } else {
                    item.popupPanel.hide();
                }

            }
        }
    }

});