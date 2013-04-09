Ext.define('Admin.view.account.DetailPanel', {
    extend: 'Admin.view.DetailPanel',
    alias: 'widget.accountDetail',

    split: true,
    showToolbar: false,

    initComponent: function () {

        this.items = [
            this.createNoSelection(),
            this.createUserPreviewPanel(),
            this.createGroupPreviewPanel(),
            this.createLargeBoxSelection(),
            this.createSmallBoxSelection()
        ];
        this.callParent(arguments);
    },

    showAccountPreview: function (data) {
        if (data) {
            var activeTab;
            switch (data.type) {
            case 'user':
                activeTab = this.down('userPreviewPanel');
                break;
            case 'role':
            case 'group':
                activeTab = this.down('groupPreviewPanel');
                break;
            }
            if (activeTab) {
                activeTab.setData(data);
                this.getLayout().setActiveItem(activeTab);
            }
        }
    },


    showMultipleSelection: function (data, detailed) {
        var activeItem;
        if (detailed) {
            activeItem = this.down('#largeBoxSelection');
            this.getLayout().setActiveItem('largeBoxSelection');
        } else {
            activeItem = this.down('#smallBoxSelection');
            this.getLayout().setActiveItem('smallBoxSelection');
        }

        activeItem.update({users: data});
    },

    showNoneSelection: function (data) {
        var activeItem = this.down('#noSelection');
        this.getLayout().setActiveItem('noSelection');
        activeItem.update(data);
    },

    createUserPreviewPanel: function () {
        return {
            xtype: 'userPreviewPanel',
            showToolbar: false
        };
    },

    createGroupPreviewPanel: function () {
        return {
            xtype: 'groupPreviewPanel',
            showToolbar: false
        };
    },

    largeBoxTemplate: Ext.Template(Templates.account.selectedAccountLarge),

    smallBoxTemplate: Ext.Template(Templates.account.selectedAccountSmall)
});
