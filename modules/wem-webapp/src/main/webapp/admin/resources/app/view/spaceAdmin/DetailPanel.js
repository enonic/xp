Ext.define('Admin.view.spaceAdmin.DetailPanel', {
    extend: 'Admin.view.DetailPanel',
    alias: 'widget.spaceDetail',

    requires: [
        'Admin.view.spaceAdmin.DetailToolbar'
    ],

    showToolbar: false,
    isLiveMode: true,


    /*listeners: {
     afterrender: function (detail) {
     detail.el.on('click', function (event, target, opts) {
     var key = target.attributes.getNamedItem('id').nodeValue.split('remove-from-selection-button:')[1];
     detail.fireEvent('deselect', key);
     }, this, {
     delegate: '.deselect'
     });
     detail.el.on('click', function (event, target, opts) {
     detail.fireEvent('clearselection');
     }, this, {
     delegate: '.clearSelection'
     });
     }
     },*/

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
