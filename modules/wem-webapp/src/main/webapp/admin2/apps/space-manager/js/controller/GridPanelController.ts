Ext.define('Admin.controller.GridPanelController', {
    extend: 'Admin.controller.Controller',

    stores: [],

    models: [],

    views: [],

    contextMenu: null,

    init: function () {

        this.control({
            '#spaceTreeGrid gridpanel, #spaceTreeGrid treepanel': {
                selectionchange: this.onGridSelectionChange,
                itemcontextmenu: this.showContextMenu,
                itemdblclick: function (grid, record) {
                    this.editSpace(record);
                }
            },
            '#spaceContextMenu *[action=editSpace]': {
                click: function (el, e) {
                    this.editSpace();
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
            this.contextMenu = new app_ui.ContextMenu();
        }
        return this.contextMenu;
    }

});