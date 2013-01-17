Ext.define('Admin.view.contentManager.DetailPanel', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.contentDetail',

    requires: [
        'Admin.view.contentManager.DetailToolbar',
        'Admin.view.contentManager.LivePreview',
        'Admin.view.account.MembershipsGraphPanel'
    ],

    autoScroll: true,
    border: false,
    layout: 'card',
    cls: 'admin-detail',

    showToolbar: true,
    isLiveMode: false,

    initComponent: function () {
        if (Ext.isEmpty(this.data)) {

            this.activeItem = 'noSelection';

        } else if (Ext.isObject(this.data) || this.data.length === 1) {

            if (this.isLiveMode) {
                this.activeItem = 'livePreview';
            } else {
                this.activeItem = 'singleSelection';
            }

        } else if (this.data.length > 1 && this.data.length <= 10) {

            this.activeItem = 'largeBoxSelection';

        } else {

            this.activeItem = 'smallBoxSelection';

        }

        this.items = [
            this.createNoSelection(),
            this.createSingleSelection(this.data),
            this.createLargeBoxSelection(this.data),
            this.createSmallBoxSelection(this.data),
            this.createLivePreview(this.data)
        ];

        if (this.showToolbar) {
            this.tbar = Ext.createByAlias('widget.contentDetailToolbar');
        } else {
            this.tbar = {
                xtype: 'toolbar',
                cls: 'admin-white-toolbar',
                items: [
                    {
                        xtype: 'tbtext',
                        text: 'No items selected - Choose from list above - <a href="javascript:;">Clear selection</a>'
                    }
                ]
            };
        }

        this.callParent(arguments);
        this.addEvents('deselectrecord');
    },


    createNoSelection: function () {
        return {
            itemId: 'noSelection',
            xtype: 'panel',
            styleHtmlContent: true,
            padding: 10,
            bodyStyle: {
                border: 'none'
            },
            html: '<div>Nothing selected</div>'
        };
    },

    createSingleSelection: function (data) {
        var info;
        if (Ext.isArray(data) && data.length > 0) {
            info = data[0].data;
        } else if (!Ext.isEmpty(data)) {
            info = data.data;
        }

        return {
            xtype: 'container',
            itemId: 'singleSelection',
            layout: {
                type: 'column',
                columns: 3
            },
            defaults: {
                border: 0
            },
            items: [
                {
                    xtype: 'component',
                    width: 100,
                    cls: 'west',
                    itemId: 'previewPhoto',
                    tpl: Templates.contentManager.previewPhoto,
                    data: info,
                    margin: 5
                },
                {
                    xtype: 'container',
                    columnWidth: 1,
                    margin: '5 0',
                    defaults: {
                        border: 0
                    },
                    items: [
                        {
                            xtype: 'component',
                            cls: 'north',
                            itemId: 'previewHeader',
                            padding: '5 5 15',
                            tpl: Templates.contentManager.previewHeader,
                            data: info
                        },
                        {
                            flex: 1,
                            cls: 'center',
                            xtype: 'tabpanel',
                            items: [
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
                            ]
                        }
                    ]
                },
                {
                    xtype: 'component',
                    width: 300,
                    margin: 5,
                    itemId: 'previewInfo',
                    cls: 'east',
                    tpl: Templates.contentManager.previewCommonInfo,
                    data: info
                }
            ]
        };
    },

    createLargeBoxSelection: function (data) {
        var tpl = Ext.Template(Templates.contentManager.previewSelectionLarge);

        var panel = {
            xtype: 'panel',
            itemId: 'largeBoxSelection',
            styleHtmlContent: true,
            autoScroll: true,
            listeners: {
                click: {
                    element: 'body',
                    fn: this.deselectItem,
                    scope: this
                }
            },
            padding: 10,
            border: 0,
            tpl: tpl,
            data: data
        };

        return panel;
    },

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

    setData: function (data, updateTitle) {
        if (!data) {
            return;
        }

        this.data = data;

        if (Ext.isEmpty(this.data)) {

            this.getLayout().setActiveItem('noSelection');

        } else if (Ext.isObject(this.data) || this.data.length === 1) {

            var singleData;
            if (Ext.isArray(this.data)) {
                singleData = this.data[0] && this.data[0].data ? this.data[0].data : this.data[0];
            } else {
                singleData = this.data.data || this.data;
            }

            if (this.isLiveMode) {

                var livePreview = this.down('#livePreview');
                this.getLayout().setActiveItem(livePreview);

                //TODO update urls when they are ready
                livePreview.load('/dev/live-edit/page/page.jsp');

            } else {

                var previewHeader = this.down('#previewHeader');
                previewHeader.update(singleData);

                var previewPhoto = this.down('#previewPhoto');
                previewPhoto.update(singleData);

                var previewInfo = this.down('#previewInfo');
                previewInfo.update(singleData);

                this.getLayout().setActiveItem('singleSelection');
            }

        } else if (this.data.length > 1 && this.data.length <= 10) {

            var largeBox = this.down('#largeBoxSelection');
            largeBox.update(this.data);

            this.getLayout().setActiveItem(largeBox);

        } else {

            var smallBox = this.down('#smallBoxSelection');
            smallBox.update(this.data);

            this.getLayout().setActiveItem(smallBox);

        }
        if (updateTitle !== false) {
            this.updateTitle(data);
        }
    },

    getData: function () {
        return this.data;
    },

    updateTitle: function (data) {
        var count = Ext.isObject(data) ? 1 : data.length;
        var header = count + " item(s) selected";
        if (count > 0) {
            header += " (<a href='javascript:;' class='clearSelection'>Clear selection</a>)";
        }

        var tbar = this.dockedItems.get(0);
        if (tbar) {
            var tbtext = tbar.down('tbtext');
            if (tbtext) {
                tbtext.update(header);
                if (count > 0) {
                    var clearSel = tbtext.el.down('a.clearSelection');
                    if (clearSel) {
                        clearSel.on("click", function () {
                            this.fireEvent('deselectrecord', -1);
                        }, this);
                    }
                }
            }
        }

    },

    toggleLive: function () {
        this.isLiveMode = !this.isLiveMode;
        this.setData(this.data, false);
    }

});
