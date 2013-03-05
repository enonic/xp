Ext.define('Admin.controller.spaceAdmin.Controller', {
    extend: 'Admin.controller.Controller',

    stores: [
        'Admin.store.spaceAdmin.SpaceStore',
        'Admin.store.spaceAdmin.SpaceTreeStore'
    ],

    models: [
        'Admin.model.spaceAdmin.SpaceModel'
    ],

    views: [
        'Admin.view.spaceAdmin.DetailPanel',
        'Admin.view.spaceAdmin.DeleteSpaceWindow'
    ],


    init: function () {
        var me = this;

        me.control({});
    },

    generateTabId: function (space, isEdit) {
        return 'tab-' + ( isEdit ? 'edit-' : 'preview-') + space.get('name');
    },


    showNewSpaceWindow: function () {
        var tabs = this.getCmsTabPanel();

        var tabItem = {
            id: 'new-space',
            xtype: 'spaceAdminWizardPanel',
            title: 'New Space'
        };

        tabs.addTab(tabItem);
    },

    viewSpace: function (space) {
        if (space) {
            var me = this;
            var tabs = this.getCmsTabPanel();

            // focus edit tab if present or create new one
            var activeTab = tabs.setActiveTab(me.generateTabId(space, true));

            if (!activeTab) {
                var tabItem = {
                    id: me.generateTabId(space, false),
                    xtype: 'spaceDetail',
                    showToolbar: true,
                    data: space,
                    title: space.data.displayName
                };
                tabs.addTab(tabItem);
            }
        }
    },

    editSpace: function (space) {
        if (!space) {
            return;
        }

        var me = this;
        var tabs = this.getCmsTabPanel();

        tabs.el.mask();
        Admin.lib.RemoteService.space_get({
            "spaceName": [space.get('name')]
        }, function (r) {
            tabs.el.unmask();
            if (r) {
                var tabItem = {
                    id: me.generateTabId(space, true),
                    editing: true,
                    xtype: 'spaceAdminWizardPanel',
                    data: space,
                    title: space.get('displayName'),
                    modelData: space.raw
                };

                //check if preview tab is open and close it
                var index = tabs.items.indexOfKey(me.generateTabId(space, false));
                if (index >= 0) {
                    tabs.remove(index);
                }
                tabs.addTab(tabItem, index >= 0 ? index : undefined, undefined);
            } else {
                Ext.Msg.alert("Error", r ? r.error : "Unable to retrieve space.");
            }
        });
    },

    showDeleteSpaceWindow: function (space) {
        var win = this.getDeleteSpaceWindow();
        win.doShow(space);
    },


    updateDetailPanel: function (selected) {
        this.getSpaceDetailPanel().setData(selected);
    },

    updateToolbarButtons: function (selected) {
        var enable = selected && selected.length > 0;
        var toolbar = this.getSpaceBrowseToolbar();

        var buttons = Ext.ComponentQuery.query('button[action=viewSpace], ' + 'button[action=editSpace], ' +
                                               'button[action=deleteSpace]', toolbar);

        Ext.Array.each(buttons, function (button) {
            button.setDisabled(!enable);
        });
    },


    /*  Getters     */

    getSpaceFilterPanel: function () {
        return Ext.ComponentQuery.query('spaceFilter')[0];
    },

    getSpaceBrowseToolbar: function () {
        return Ext.ComponentQuery.query('spaceBrowseToolbar')[0];
    },

    getSpaceTreeGridPanel: function () {
        return Ext.ComponentQuery.query('spaceTreeGrid')[0];
    },

    getSpaceDetailPanel: function () {
        return Ext.ComponentQuery.query('spaceDetail')[0];
    },

    getDeleteSpaceWindow: function () {
        var win = Ext.ComponentQuery.query('deleteSpaceWindow')[0];
        if (!win) {
            win = Ext.create('widget.deleteSpaceWindow');
        }
        return win;
    }

});