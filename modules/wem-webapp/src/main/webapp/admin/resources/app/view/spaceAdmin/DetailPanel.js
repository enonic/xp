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

        this.singleSelection.tabs = [
            {
                displayName: 'Traffic',
                tab: 'traffic'
            },
            {
                displayName: 'Graph',
                tab: 'graph'
            },
            {
                displayName: 'Meta',
                tab: 'meta'
            }
        ];

        this.singleSelection.tabData = {
            traffic: {
                html: '<h1>Traffic</h1>'
            },
            meta: {
                html: '<h1>Meta</h1>'
            },
            graph: {
                html: '<h1>Graph</h1>'
            }
        };

        this.actionButtonItems = [
            {
                text: 'Open',
                action: 'viewSpace'
            },
            {
                text: 'Edit',
                action: 'editSpace'
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
