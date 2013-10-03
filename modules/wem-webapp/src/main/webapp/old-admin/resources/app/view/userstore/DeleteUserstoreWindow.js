Ext.define('Admin.view.userstore.DeleteUserstoreWindow', {
    extend: 'Admin.view.BaseDialogWindow',
    alias: 'widget.deleteUserstoreWindow',

    dialogTitle: 'Delete Userstore(s)',

    items: [
        {
            margin: '10px 0 10px 0px',
            xtype: 'container',
            defaults: {
                xtype: 'button',
                scale: 'medium',
                margin: '0 10 0 0'
            },
            items: [
                {
                    text: 'Delete',
                    iconCls: 'icon-delete-user-24',
                    itemId: 'deleteUserstoreButton',
                    action: 'deleteUserstore'
                }
            ]
        }
    ],

    initComponent: function () {
        this.callParent(arguments);
    },

    doShow: function (selection) {
        var Templates_userstore_userstoreInfo =
    		'<div>' +
    		    '<div class="admin-user-info clearfix">' +
    		        '<div class="admin-user-photo west admin-left">' +
    		            '<div class="photo-placeholder">' +
    		                '<img src="resources/images/icons/128x128/userstore.png" alt="{name}"/>' +
    		            '</div>' +
    		        '</div>' +
    		        '<div class="admin-left" style="line-height: 32px">' +
    		            '<h2>{name}</h2>' +
    		        '</div>' +
    		    '</div>' +
    		'</div>';

        if (selection) {
            this.setDialogInfoTpl(Templates_userstore_userstoreInfo);
            this.callParent([selection[0]]);
        }
    }
});