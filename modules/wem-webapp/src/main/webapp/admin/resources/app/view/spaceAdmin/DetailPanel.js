Ext.define('Admin.view.spaceAdmin.DetailPanel', {
    extend: 'Admin.view.DetailPanel',
    alias: 'widget.spaceDetail',

    requires: [
        'Admin.view.spaceAdmin.DetailToolbar'
    ],

    showToolbar: false,

    initComponent: function () {
        var data = this.resolveActiveData(this.data);

        this.activeItem = this.resolveActiveItem(data);

        if (this.showToolbar) {
            this.tbar = Ext.createByAlias('widget.spaceDetailToolbar');
        } else {
            this.tbar = this.toolBar([
                '->',
                {
                    xtype: 'tbtext',
                    itemId: 'toggleBtn',
                    hidden: true,
                    text: '<a href="javascript:;">Switch to Info View</a>'
                }
            ]);
        }

        this.singleSelection.tabs = [
            {
                title: "Content",
                itemId: 'contentTab',
                html: 'Content'
            },
            {
                title: "Tree",
                itemId: 'treeTab',
                html: 'Tree'
            },
            {
                title: "Page",
                itemId: 'pageTab',
                html: 'Page'
            },
            {
                title: "Security",
                itemId: 'securityTab',
                html: 'Security'
            }
        ];

        this.items = [
            this.createNoSelection(),
            this.createSingleSelection(data),
            this.createSmallBoxSelection(data),
            this.createLargeBoxSelection(data)
        ];

        this.callParent(arguments);
        this.addEvents('deselect', 'clearselection');

    }

});
