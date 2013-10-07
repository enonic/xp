Ext.define('Admin.view.account.TabMenu', {
    extend: 'Ext.menu.Menu',
    alias: 'widget.userTabMenu',

    items: [
        {
            text: 'Delete Address Tab',
            action: 'deleteAddressTab'
        },
        {
            text: 'New Address Tab',
            action: 'newAddressTab'
        }
    ]
});
