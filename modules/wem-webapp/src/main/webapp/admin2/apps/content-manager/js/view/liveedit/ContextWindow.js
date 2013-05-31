Ext.define('Admin.view.contentManager.liveedit.ContextWindow', {
    extend: 'Ext.container.Container',
    alias: 'widget.liveEditContextWindow',

    border: false,
    floating: true,
    shadow: false,
    width: 500,
    modal: true,
    autoHeight: true,
    maxHeight: 600,
    cls: 'admin-window',
    closeAction: 'hide',
    padding: 20,

    initComponent: function () {

        var me = this;


        if (!this.items) {
            this.items = [];
        }

        this.callParent(arguments);
    },


    doShow: function (model) {
        this.show();
    },

    doHide: function () {
        this.hide();
    },

    close: function () {
        this.destroy();
    }

});