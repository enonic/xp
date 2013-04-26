Ext.define('Admin.controller.Controller', {
    extend: 'Ext.app.Controller',

    stores: [
        'Admin.store.SpaceStore'
    ],

    models: [
        'Admin.model.SpaceModel'
    ],

    views: [
        'Admin.view.DetailPanel',
        'Admin.view.DeleteSpaceWindow'
    ],

    requires: [
        'Admin.lib.RemoteService'
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
            editing: true,
            title: 'New Space'
        };

        tabs.addTab(tabItem);
    },

    viewSpace: function (space) {
        space = this.validateSpace(space);

        var me = this;
        var tabs = this.getCmsTabPanel();

        // focus edit tab if present or create new one
        var activeTab = tabs.setActiveTab(me.generateTabId(space, true));

        if (!activeTab) {
            var tabItem = {
                id: me.generateTabId(space, false),
                xtype: 'spaceDetail',
                showToolbar: false,
                data: space,
                title: space.get('displayName'),
                isFullPage: true
            };
            tabs.addTab(tabItem);
        }
    },

    editSpace: function (space) {
        space = this.validateSpace(space);

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
                    title: space.get('displayName')
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

    deleteSpace: function (space) {
        space = this.validateSpace(space);
        this.showDeleteSpaceWindow([].concat(space));
    },

    showDeleteSpaceWindow: function (spaceArray) {
        var win = this.getDeleteSpaceWindow();
        win.doShow(spaceArray);
    },

    validateSpace: function (space) {
        if (!space) {
            var showPanel = this.getSpaceTreeGridPanel();
            return showPanel.getSelection()[0];
        }
        return space;
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
    },

    /*  Getters */

    getCmsTabPanel: function () {
        return Ext.ComponentQuery.query('cmsTabPanel')[0];
    },

    getTopBar: function () {
        return Ext.ComponentQuery.query('topBar')[0];
    },

    getMainViewport: function () {
        var parent = window.parent || window;
        return parent['Ext'].ComponentQuery.query('#mainViewport')[0];
    }



});