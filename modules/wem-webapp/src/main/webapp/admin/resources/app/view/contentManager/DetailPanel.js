Ext.define('Admin.view.contentManager.DetailPanel', {
    extend: 'Admin.view.DetailPanel',
    alias: 'widget.contentDetail',

    requires: [
        'Admin.view.contentManager.DetailToolbar',
        'Admin.view.contentManager.LivePreview',
        'Admin.view.account.MembershipsGraphPanel'
    ],

    showToolbar: true,
    isLiveMode: false,

    /*listeners: {
     afterrender: function () {
     if (this.isLiveMode) {
     var livePreview = this.down('#livePreview');
     //TODO update urls when they are ready
     livePreview.load('/dev/live-edit/page/page.jsp');
     }
     if (!this.showToolbar) {
     var toggleBtn = this.down('#toggleBtn');
     var a = toggleBtn.el.down('a');
     a.on('click', function () {
     this.toggleLive();
     if (this.isLiveMode) {
     a.setHTML('Switch to Info View');
     } else {
     a.setHTML('Switch to Live View');
     }
     }, this);
     }
     }
     },*/

    initComponent: function () {
        this.resolveActiveData(this.data);

        this.setDataCallback = function (data) {
            if (this.isLiveMode) {

                var livePreview = this.down('#livePreview');
                this.getLayout().setActiveItem(livePreview);

                //TODO update urls when they are ready
                livePreview.load('/dev/live-edit/page/page.jsp');

            }
        };

        this.toolBarConfig({
            updateTitleCallback: function (data, tbar, count) {
                var toggleBtn = tbar.down('#toggleBtn');
                if (toggleBtn) {
                    if (count === 1) {
                        toggleBtn.show();
                    } else {
                        toggleBtn.hide();
                    }
                }
            }
        });

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

        if (this.showToolbar) {
            this.tbar = Ext.createByAlias('widget.contentDetailToolbar', {
                isLiveMode: this.isLiveMode
            });
        } else {
            this.tbar = this.toolBar(['->', {
                xtype: 'tbtext',
                itemId: 'toggleBtn',
                hidden: true,
                text: '<a href="javascript:;">Switch to Info View</a>'
            }]);
        }


        this.callParent(arguments);
        this.addEvents('deselectrecord');

    },

    /*
     createSmallBoxSelection: function (data) {
     var tpl = Ext.Template(Templates.contentManager.previewSelectionSmall);

     var panel = {
     xtype: 'panel',
     itemId: 'smallBoxSelection',
     styleHtmlContent: true,
     listeners: {
     click: {
     element: 'body',
     fn: this.deselectItem,
     scope: this
     }
     },
     autoScroll: true,
     padding: 10,
     border: 0,
     tpl: tpl,
     data: data
     };

     return panel;
     },
     */

    createLivePreview: function (data) {
        return {
            itemId: 'livePreview',
            xtype: 'contentLive'
        };
    },


    deselectItem: function (event, target) {
        var className = target.className;
        if (className && className === 'remove-selection') {
            var key = target.attributes.getNamedItem('id').nodeValue.split('remove-from-selection-button-')[1];
            if (!Ext.isEmpty(key)) {
                this.fireEvent('deselectrecord', key);
            }
        }
    },

    toggleLive: function () {
        this.isLiveMode = !this.isLiveMode;
        this.setData(this.data, false);
    }

});
