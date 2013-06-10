Ext.define('Admin.controller.Controller', {
    extend: 'Ext.app.Controller',

    stores: [
    ],

    models: [
        'Admin.model.SpaceModel'
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

//        var tabItem = {
//            id: 'new-space',
//            xtype: 'spaceAdminWizardPanel',
//            editing: true,
//            title: 'New Space'
//        };
        var spaceWizardPanel = new app_ui_wizard.SpaceWizardPanel('new-space', 'New Space', true);

        tabs.addTab(spaceWizardPanel.ext);
    },

    viewSpace: function (space:app_model.SpaceModel) {
        space = this.validateSpace(space);

        var me = this;
        var tabs = this.getCmsTabPanel();

        // focus edit tab if present or create new one
        var activeTab = tabs.setActiveTab(me.generateTabId(space, true));

        if (!activeTab) {
            var id = this.generateTabId(space, false);
            var tabItem = new app_ui.SpaceDetailPanel(undefined, id, space).ext;
            /*    {
             id: me.generateTabId(space, false),
             xtype: 'spaceDetail',
             showToolbar: false,
             data: space,
             title: space.get('displayName'),
             isFullPage: true
             };*/
            tabs.addTab(tabItem);
        }
    },

    editSpace: function (space:app_model.SpaceModel) {
        space = this.validateSpace(space);

        var me = this;
        var tabs = this.getCmsTabPanel();

        tabs.el.mask();
        Admin.lib.RemoteService.space_get({
            "spaceName": [space.get('name')]
        }, function (r) {
            tabs.el.unmask();
            if (r) {
                var id = me.generateTabId(space, true);
                var editing = true;
                var title = <string> space.get('displayName');
                var data = space;
                var spaceWizardPanel = new app_ui_wizard.SpaceWizardPanel(id, title, editing, data);

                //check if preview tab is open and close it
                var index = tabs.items.indexOfKey(me.generateTabId(space, false));
                if (index >= 0) {
                    tabs.remove(index);
                }
                tabs.addTab(spaceWizardPanel.ext, index >= 0 ? index : undefined, undefined);
            } else {
                console.error("Error", r ? r.error : "Unable to retrieve space.");
            }
        });
    },

    validateSpace: function (space:app_model.SpaceModel) {
        if (!space) {
            var showPanel = this.getSpaceTreeGridPanel();
            return showPanel.getSelection()[0];
        }
        return space;
    },

    updateDetailPanel: function (selected) {
        //this.getSpaceDetailPanel().setData(selected);
    },

    updateToolbarButtons: function (selected) {
        var enable = selected && selected.length > 0;
        var toolbar = this.getSpaceBrowseToolbar();

        var buttons = Ext.ComponentQuery.query('button[action=viewSpace], ' + 'button[action=editSpace] ', toolbar);

        Ext.Array.each(buttons, function (button:Ext_button_Button, index, all) {
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
        return components.gridPanel;
    },

    getSpaceDetailPanel: function () {
        return components.detailPanel;
    },

    /*  Getters */

    getCmsTabPanel: function () {
        return components.tabPanel;
    },

    getTopBar: function () {
        return this.getCmsTabPanel().tabBar;
    },

    getMainViewport: function () {
        var parent = window.parent || window;
        return parent['Ext'].ComponentQuery.query('#mainViewport')[0];
    }



});
