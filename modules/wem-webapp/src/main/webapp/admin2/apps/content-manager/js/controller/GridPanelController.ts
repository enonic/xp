Ext.define('Admin.controller.GridPanelController', {
    extend: 'Admin.controller.Controller',

    /*      Controller for handling Grid & its Context Menu UI events       */

    stores: [
        'Admin.store.contentManager.ContentStore',
        'Admin.store.contentManager.ContentTreeStore'
    ],
    models: [
        'Admin.model.contentManager.ContentModel'
    ],
    /*    views: [
     'Admin.view.contentManager.FilterPanel',
     'Admin.view.contentManager.ShowPanel',
     'Admin.view.contentManager.ContextMenu'
     ],*/

    init: function () {

        this.control({
            'contentTreeGridPanel treepanel, grid': {
                //This event is called 2-3 times when changing selection. This must be fixed!
                selectionchange: function (panel, selected, opts) {
                    console.log("Fire selection change");
                    this.updateDetailPanel(selected);
                    this.updateToolbarButtons(selected);
                },
                itemcontextmenu: this.popupMenu,
                itemdblclick: function (grid, record, el, index, event, opts) {
                    this.editContent(record);
                }
            },
            'contentShow': {
                afterrender: function () {

                    /* For 18/4 demo */

                    if (document.location.hash.indexOf('/cm/open/') > -1) {
                        Admin.MessageBus.liveEditOpenContent();
                    }

                }
            }
        });
    },

    popupMenu: function (view, rec, node, index, e) {
        e.stopEvent();
        this.getContentManagerContextMenu().showAt(e.getXY());
        return false;
    }

});
