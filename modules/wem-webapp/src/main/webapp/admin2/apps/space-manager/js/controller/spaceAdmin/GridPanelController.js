Ext.define('Admin.controller.spaceAdmin.GridPanelController', {
    extend: 'Admin.controller.spaceAdmin.Controller',

    stores: [],

    models: [],

    views: [
        'Admin.view.spaceAdmin.TreeGridPanel',
        'Admin.view.spaceAdmin.ContextMenu'
    ],

    init: function () {

        this.control({
            'spaceTreeGrid gridpanel, spaceTreeGrid treepanel': {
                selectionchange: this.onGridSelectionChange,
                itemcontextmenu: this.showContextMenu,
                itemdblclick: function (grid, record) {
                    this.editSpace(record);
                }
            },
            'spaceContextMenu *[action=deleteSpace]': {
                click: function (el, e) {
                    this.deleteSpace();
                }
            },
            'spaceContextMenu *[action=editSpace]': {
                click: function (el, e) {
                    this.editSpace();
                }
            },
            'spaceContextMenu *[action=viewSpace]': {
                click: function (el, e) {
                    this.viewSpace();
                }
            }
        });
    },

    onGridSelectionChange: function (selModel, selected, opts) {
        this.updateDetailPanel(selected);
        this.updateToolbarButtons(selected);
    },

    showContextMenu: function (view, rec, node, index, e) {
        e.stopEvent();
        this.getContextMenu().showAt(e.getXY());
        return false;
    },

    getContextMenu: function () {
        var menu = Ext.ComponentQuery.query('spaceContextMenu')[0];
        if (!menu) {
            menu = Ext.create('widget.spaceContextMenu');
        }
        return menu;
    }

});