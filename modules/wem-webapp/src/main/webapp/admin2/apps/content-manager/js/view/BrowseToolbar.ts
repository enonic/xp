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
        //Handlers for this items put in the Admin.controller.contentManager.Controller
        this.items = <any[]>[

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
    }

});
