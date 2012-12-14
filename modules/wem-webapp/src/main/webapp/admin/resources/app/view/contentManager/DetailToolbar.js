Ext.define('Admin.view.contentManager.DetailToolbar', {
    extend: 'Ext.toolbar.Toolbar',
    alias: 'widget.contentDetailToolbar',

    cls: 'admin-toolbar',

    defaults: {
        scale: 'medium'
    },

    initComponent: function () {

        this.items = [

            {
                text: 'Publish'
            },

            {
                text: 'Edit'
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
                text: 'Move'
            },

            {
                text: 'Relations'
            },
            {
                text: 'History'
            },

            {
                text: 'View'
            },

            {
                text: 'Export'
            },
            '->',
            {
                text: 'Live Mode',
                action: 'toggleLive',
                enableToggle: true
            }
        ];

        this.callParent(arguments);
    }

});
