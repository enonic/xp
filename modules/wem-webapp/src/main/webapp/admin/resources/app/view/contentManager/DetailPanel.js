Ext.define('Admin.view.contentManager.DetailPanel', {
    extend: 'Admin.view.DetailPanel',
    alias: 'widget.contentDetail',

    requires: [
        'Admin.view.contentManager.DetailToolbar',
        'Admin.view.contentManager.LivePreview',
        'Admin.view.account.MembershipsGraphPanel',
        'Ext.ux.toggleslide.ToggleSlide'
    ],

    isLiveMode: false,

    keyField: 'path',

    initComponent: function () {
        var me = this;
        this.activeItem = this.resolveActiveItem(this.data);

        this.singleSelection.tabs = [
            {
                displayName: 'Analytics',
                name: 'analytics',
                items: [
                    {xtype: 'iframe', url: '/dev/detailpanel/analytics.jsp', iFrameCls: (me.isVertical ? 'admin-detail-vertical' : '') }
                ]
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
                livePreview.load('/dev/live-edit/page/bootstrap.jsp', false);
            }
        }, this);

        this.setDataCallback = function (data) {
            if (this.isLiveMode) {
                var livePreview = this.down('#livePreview');
                //TODO update urls when they are ready
                livePreview.load('/dev/live-edit/page/bootstrap.jsp', false);
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

    createToolBar: function () {
        var me = this;
        return Ext.createByAlias('widget.contentDetailToolbar', {
            isLiveMode: me.isLiveMode
        });
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
        this.isLiveMode = !this.isLiveMode;
        this.setData(this.data, false);
    }

});
