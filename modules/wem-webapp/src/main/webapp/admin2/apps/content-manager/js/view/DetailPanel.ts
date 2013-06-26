Ext.define('Admin.view.contentManager.DetailPanel', {
    extend: 'Admin.view.BaseDetailPanel',
    alias: 'widget.contentDetail',

    /*    requires: [
     'Admin.view.contentManager.DetailToolbar',
     'Admin.view.contentManager.LivePreview',
     'Admin.view.account.MembershipsGraphPanel',
     'Ext.ux.toggleslide.ToggleSlide'
     ],*/

    isLiveMode: false,

    keyField: 'path',

    initComponent: function () {
        var me = this;
        this.activeItem = this.resolveActiveItem(this.data);

        this.singleSelection.getTabs = function () {
            var iFrameCls = me.isVertical ? 'admin-detail-vertical' : '';
            var analytics = new admin.ui.IframeContainer('/dev/detailpanel/analytics.html ', iFrameCls);

            return <any[]> [
                {
                    displayName: 'Analytics',
                    name: 'analytics',
                    items: [analytics.ext]
                },
                {
                    displayName: 'Sales',
                    name: 'sales',
                    items: [
                        {xtype: 'component', html: '<h1>Sales</h1>'}
                    ]
                },
                {
                    displayName: 'Scorecard',
                    name: 'scorecard',
                    items: [
                        {xtype: 'component', html: '<h1>Scorecard</h1>'}
                    ]
                },
                {
                    displayName: 'History',
                    name: 'history',
                    items: [
                        {xtype: 'component', html: '<h1>History</h1>'}
                    ]
                }
            ];
        };


        //Handlers for this items put in the Admin.controller.contentManager.Controller
        this.actionButtonItems = [
            {
                text: ' New',
                icon: undefined,
                action: 'newContent',
                disableOnMultipleSelection: true
            },
            {
                text: 'Edit',
                icon: undefined,
                action: 'editContent',
                disableOnMultipleSelection: false
            },
            {
                text: 'Open',
                icon: undefined,
                action: 'viewContent',
                disableOnMultipleSelection: false
            },
            {
                text: 'Delete',
                icon: undefined,
                action: 'deleteContent'
            },
            {
                text: 'Duplicate',
                icon: undefined,
                action: 'duplicateContent'
            },
            {
                text: 'Move',
                icon: undefined,
                disabled: true,
                action: 'moveContent'
            }
        ];

        this.on('afterrender', function () {
            if (this.isLiveMode) {
                var livePreview = this.down('#livePreview');
                //TODO update urls when they are ready
                livePreview.load(this.getLiveUrl(this.data), false);
            }
        }, this);

        this.setDataCallback = function (data) {
            if (this.isLiveMode) {
                var livePreview = this.down('#livePreview');
                //TODO update urls when they are ready
                livePreview.load(this.getLiveUrl(data), false);
            }
        };

        this.items = [
            this.createNoSelection(),
            this.createSingleSelection(this.data),
            this.createLargeBoxSelection(this.data),
            this.createSmallBoxSelection(this.data),
            this.createLivePreview(this.data)
        ];

        this.callParent(arguments);
    },

    getLiveUrl: function (data) {
        if (data) {
            if (data.length > 0) {
                return data[0].data.displayName.match(/frogger/gi) !== null ? '/dev/live-edit-page/frogger.jsp'
                    : '/dev/live-edit-page/bootstrap.jsp';
            } else if (data.data) {
                return data.data.displayName.match(/frogger/gi) !== null ? '/dev/live-edit-page/frogger.jsp'
                    : '/dev/live-edit-page/bootstrap.jsp';
            }
        }
        return '/dev/live-edit-page/bootstrap.jsp';
    },

    createToolBar: function () {
        return new app.DetailToolbar(this.isLiveMode).ext;
    },

    createLivePreview: function (data) {
        var me = this;
        return {
            itemId: 'livePreview',
            xtype: 'contentLive',
            actionButton: (me.isFullPage ? undefined : me.getActionButton())
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
        this.isLiveMode ? this.showDetails() : this.showPreview();
    },

    showPreview: function () {
        this.isLiveMode = true;
        this.setData(this.data, false);
    },

    showDetails: function () {
        this.isLiveMode = false;
        this.setData(this.data, false);
    }

});
