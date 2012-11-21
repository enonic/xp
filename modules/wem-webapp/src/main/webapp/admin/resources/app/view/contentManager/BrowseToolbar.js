Ext.define('Admin.view.contentManager.BrowseToolbar', {
    extend: 'Ext.toolbar.Toolbar',
    alias: 'widget.browseToolbar',

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
                xtype: 'splitbutton',
                text: ' New',
                action: 'newContent',
                cls: 'x-btn-as-arrow',
                menu: Ext.create('Admin.view.MegaMenu', {
                    recentCount: 4,
                    cookieKey: 'admin.contentmanager.megamenu',
                    url: 'resources/data/contentManagerMenu.json',
                    loadMenuItems: this.generateContentTypesMenu
                })
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
                action: 'moveContent'
            }
        ];

        this.callParent(arguments);
    },

    generateContentTypesMenu: function () {
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
                    "iconCls": "icon-content-24",
                    "qualifiedContentType": contentType.qualifiedName
                }));
            }
            menuSection = menu.createMenuSection('Content Types', sectionItems);
            menuSection.minWidth = 160;
            menuItems.push(menuSection);

            menu.add({
                xtype: 'container',
                itemId: 'itemSection',
                layout: {
                    type: 'table',
                    columns: 4,
                    tdAttrs: {
                        style: {
                            'vertical-align': 'top'
                        }
                    }
                },
                items: menuItems
            });
            menu.doLayout();
        });
    }

});
