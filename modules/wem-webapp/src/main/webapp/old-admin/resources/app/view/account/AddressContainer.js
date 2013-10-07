Ext.define('Admin.view.account.AddressContainer', {
    extend: 'Ext.form.FieldSet',
    alias: 'widget.addressContainer',

    width: '100%',
    title: 'Address',
    padding: 10,
    cls: 'addresses',

    dropConfig: undefined,

    requires: [
        "Admin.view.account.AddressDropTarget",
        "Admin.view.account.AddressColumn"
    ],

    initComponent: function () {
        var button = {
            xtype: 'button',
            text: 'Add New Address',
            action: 'addNewAddress',
            currentUser: this.currentUser
        };
        var addresses = this.items;
        this.items = [
            {
                xtype: 'addressColumn',
                items: addresses
            },
            button
        ];
        this.callParent(arguments);
        this.addEvents({
            validatedrop: true,
            beforedragover: true,
            dragover: true,
            beforedrop: true,
            drop: true
        });
        this.on("drop", this.doLayout, this);
    },

    initEvents: function () {
        this.callParent();
        this.dd = Ext.create("Admin.view.account.AddressDropTarget", this, this.dropConfig);
    },

    beforeDestroy: function () {
        if (this.dd) {
            this.dd.unreg();
        }
        this.callParent();
    },

    getItems: function () {
        return this.query('addressPanel');
    }

});