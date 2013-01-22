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
                text: 'Publish',
                action: 'publishContent'
            },

            {
                text: 'Edit',
                action: 'editContent'
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
                iconCls: 'icon-lightbulb-on-24',
                enableToggle: true,
                pressed: true
            }
        ];

        this.callParent(arguments);
    }

});
