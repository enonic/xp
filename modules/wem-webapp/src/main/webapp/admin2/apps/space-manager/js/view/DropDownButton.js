Ext.define('Admin.view.DropDownButton', {
    extend: 'Ext.button.Button',
    alias: 'widget.dropDownButton',

    cls: 'admin-dropdown-button',
    width: 120,
    padding: 5,
    menuItems: [],

    initComponent: function () {

        this.menu = this.createMenu();

        this.callParent(arguments);
    },

    createMenu: function () {
        var me = this;
        return Ext.create('Admin.view.BaseContextMenu', {
            width: 120,
            items: this.menuItems
        });
    }
});