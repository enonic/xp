Ext.define('Admin.view.datadesigner.preview.Toolbar', {
    extend: 'Ext.toolbar.Toolbar',
    alias: 'widget.dataDesignerPreviewToolbar',

    border: false,

    isNewGroup: true,

    initComponent: function () {

        var buttonDefaults = {
            scale: 'medium',
            iconAlign: 'top',
            minWidth: 64
        };

        var leftGrp = {
            xtype: 'buttongroup',
            columns: 3,
            defaults: buttonDefaults,
            items: [
                {
                    text: 'Edit',
                    iconCls: 'icon-edit-generic'
                },
                {
                    text: 'Delete',
                    iconCls: 'icon-delete-user-24'
                }
            ]
        };

        var rightGrp = {
            xtype: 'buttongroup',
            columns: 1,
            defaults: buttonDefaults,
            items: [
                {
                    text: 'Close',
                    action: 'closePreview',
                    iconCls: 'icon-cancel-24'
                }
            ]
        };

        this.items = [ leftGrp, '->', rightGrp ];
        this.callParent(arguments);
    }

});
