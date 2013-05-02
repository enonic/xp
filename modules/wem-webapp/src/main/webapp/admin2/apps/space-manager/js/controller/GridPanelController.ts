Ext.define('Admin.controller.GridPanelController', {
    extend: 'Admin.controller.Controller',

    stores: [],

    models: [],

    views: [
        'Admin.view.TreeGridPanel'
    ],

    contextMenu: null,

    init: function () {

        this.control({
            'spaceTreeGrid gridpanel, spaceTreeGrid treepanel': {
                selectionchange: this.onGridSelectionChange,
                itemcontextmenu: this.showContextMenu,
                itemdblclick: function (grid, record) {
                    this.editSpace(record);
                }
            },
            '#spaceContextMenu *[action=deleteSpace]': {
                click: function (el, e) {
                    this.deleteSpace();
                }
            },
            '#spaceContextMenu *[action=editSpace]': {
                click: function (el, e) {
                    this.editSpace();
                }
            },
            '#spaceContextMenu *[action=viewSpace]': {
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
        var xy = e.getXY();
        this.getContextMenu().showAt(xy[0], xy[1]);
        return false;
    },

    getContextMenu: function () {
        if (!this.contextMenu) {
            this.contextMenu = new admin.ui.ContextMenu();
        }
        return this.contextMenu;
    }

});