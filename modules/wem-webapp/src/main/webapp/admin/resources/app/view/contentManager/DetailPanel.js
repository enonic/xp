Ext.define('Admin.view.contentManager.DetailPanel', {
    extend: 'Admin.view.DetailPanel',
    alias: 'widget.contentDetail',

    requires: [
        'Admin.view.contentManager.DetailToolbar',
        'Admin.view.contentManager.LivePreview',
        'Admin.view.account.MembershipsGraphPanel',
        'Ext.ux.toggleslide.ToggleSlide'
    ],

    showToolbar: true,
    isLiveMode: true,

    initComponent: function () {
        this.activeItem = this.resolveActiveItem(this.data);

        this.on('afterrender', function () {
            if (this.isLiveMode) {
                var livePreview = this.down('#livePreview');
                //TODO update urls when they are ready
                livePreview.load('/dev/live-edit/page/page.jsp');
            }
        }, this);

        this.setDataCallback = function (data) {

            if (this.isLiveMode) {
                var livePreview = this.down('#livePreview');

                //TODO update urls when they are ready
                livePreview.load('/dev/live-edit/page/page.jsp');
            }
        };

        this.singleSelection.tabs = [
            {
                title: "Content",
                itemId: 'contentTab',
                html: ' Content'
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
            },
            {
                title: "Relations",
                itemId: 'relationsTab',
                items: [
                    {
                        tpl: Templates.account.userPreviewMemberships
                    },
                    {
                        xtype: 'membershipsGraphPanel',
                        extraCls: 'admin-memberships-graph',
                        listeners: {
                            afterrender: function (cmp) {
                                var data = this.data ? this.data.graph : undefined;
                                if (data) {
                                    cmp.setGraphData(data);
                                }
                            }
                        }
                    }
                ]
            }
        ];

        this.items = [
            this.createNoSelection(),
            this.createSingleSelection(this.data),
            this.createLargeBoxSelection(this.data),
            this.createSmallBoxSelection(this.data),
            this.createLivePreview(this.data)
        ];

        this.callParent(arguments);
        this.addEvents('deselectrecord');
    },


    createLivePreview: function (data) {
        return {
            itemId: 'livePreview',
            xtype: 'contentLive'
        };
    },

    resolveActiveItem: function (data) {
        var activeItem;
        if (Ext.isEmpty(this.data)) {
            activeItem = 'noSelection';
        } else if (Ext.isObject(this.data) || this.data.length === 1) {
            if (this.isLiveMode) {
                activeItem = 'livePreview';
            } else {
                activeItem = 'singleSelection';
            }

        } else if (this.data.length > 1 && this.data.length <= 10) {
            activeItem = 'largeBoxSelection';
        } else {
            activeItem = 'smallBoxSelection';
        }
        return activeItem;
    },


    toggleLive: function () {
        this.isLiveMode = !this.isLiveMode;
        this.setData(this.data, false);
    }

});
