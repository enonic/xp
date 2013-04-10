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

    largeBoxTemplate: '<tpl for="users">' +
                      '<div class="admin-selected-item-box large clearfix" id="selected-item-box:{key}">' +
                      '<div class="left">' +
                      '<img alt={displayName} src="{image_url}"/>' +
                      '</div>' +
                      '<div class="center">' +
                      '<h6>{displayName}</h6>' +
                      '<p>{userStore}\\\\{name}</p>' +
                      '</div>' +
                      '<div class="right">' +
                      '<a href="javascript:;" class="deselect" id="remove-from-selection-button:{key}"></a>' +
                      '</div>' +
                      '</div>' +
                      '</tpl>',

    smallBoxTemplate: '<tpl for="users">' +
                      '<div id="selected-item-box:{key}" class="admin-selected-item-box small clearfix">' +
                      '<div class="left">' +
                      '<img src="{image_url}" alt="{displayName}"/>' +
                      '</div>' +
                      '<div class="center">{displayName}</div>' +
                      '<div class="right">' +
                      '<a href="javascript:;" class="deselect" id="remove-from-selection-button:{key}"></a>' +
                      '</div>' +
                      '</div>' +
                      '</tpl>'
});
