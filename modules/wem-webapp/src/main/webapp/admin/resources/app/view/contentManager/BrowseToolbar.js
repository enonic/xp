Ext.define('Admin.view.contentManager.BrowseToolbar', {
    extend: 'Ext.toolbar.Toolbar',
    alias: 'widget.browseToolbar',

    requires: [
        'Ext.ux.toggleslide.ToggleSlide'
    ],

    cls: 'admin-toolbar',
    border: true,

    defaults: {
        scale: 'medium',
        iconAlign: 'top',
        minWidth: 64
    },

    initComponent: function () {
        this.items = [

            {
                text: ' New',
                disabled: true,
                action: 'newContent'
            },
            {
                text: 'Edit',
                action: 'editContent'
            },
            {
                text: 'Open',
                action: 'viewContent'
            },
            {
                text: 'Delete',
                action: 'deleteContent'
            },
            {
                text: 'Duplicate',
                action: 'duplicateContent'
            },
            {
                text: 'Move',
                disabled: true,
                action: 'moveContent'
            },
            '->',
            {
                xtype: 'toggleslide',
                onText: 'Preview',
                offText: 'Details',
                action: 'toggleLive',
                state: this.isLiveMode
            },
            {
                iconCls: 'icon-toolbar-settings',
                action: 'showToolbarMenu',
                minWidth: 42,
                padding: '6 8 6 12'
            }
        ];

        this.callParent(arguments);
    },

    loadContentTypesMenu: function () {
        var menu = this;
        Admin.lib.RemoteService.contentType_list({}, function (rpcResponse) {
            var menuItems = [], contentTypes, menuSection;
            if (!rpcResponse || !rpcResponse.success) {
                return;
            }
            contentTypes = rpcResponse.contentTypes;

            var i;
            var sectionItems = [];
            for (i = 0; i < contentTypes.length; i++) {
                var contentType = contentTypes[i];
                sectionItems.push(menu.createMenuItem({
                    "text": contentType.name,
                    "action": "newContent",
                    "icon": contentType.iconUrl,
                    "qualifiedContentType": contentType.qualifiedName
                }));
            }
            menuSection = menu.createMenuSection('Content Types', sectionItems);
            menuSection.minWidth = 160;
            menuItems.push(menuSection);

            var itemSection = menu.down('#itemSection');
            if (menuItems.length > 0 && itemSection) {
                itemSection.layout.columns = 1;
                itemSection.removeAll(true);
                itemSection.add(menuItems);
            }
        });
    }

});
