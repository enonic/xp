Ext.define('Admin.view.contentManager.DetailPanel', {
    extend: 'Admin.view.DetailPanel',
    alias: 'widget.contentDetail',

    requires: [
        'Admin.view.contentManager.DetailToolbar',
        'Admin.view.contentManager.LivePreview',
        'Admin.view.account.MembershipsGraphPanel',
        'Ext.ux.toggleslide.ToggleSlide'
    ],

    isLiveMode: true,

    initComponent: function () {
        this.activeItem = this.resolveActiveItem(this.data);

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


    /*
     * Toolbar
     */

    createToolBar: function () {
        return Ext.createByAlias('widget.contentDetailToolbar', {
            isLiveMode: this.isLiveMode
        });
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
