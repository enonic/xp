Ext.define('Admin.view.contentManager.NewContentWindow', {
    extend: 'Admin.view.BaseDialogWindow',
    alias: 'widget.newContentWindow',

    requires: [
        'Admin.model.contentStudio.ContentTypeModel',
        'Admin.store.contentStudio.ContentTypeTreeStore'
    ],

    dialogTitle: undefined,
    dialogSubTitle: undefined,
    dialogInfoTpl: undefined,

    width: 800,
    height: 560,

    layout: 'border',
    defaultType: 'container',

    recentCount: 5,
    cookieKey: 'Admin.view.contentManager.NewContentWindow',
    cookieSeparator: '|',

    dataViewItemTemplate: '<tpl for=".">' +
                          '<div class="admin-data-view-row">' +
                          '<div class="admin-data-view-thumbnail">' +
                          '<img src="{iconUrl}?size=32"/>' +
                          '</div>' +
                          '<div class="admin-data-view-description">' +
                          '<h6>{displayName}</h6>' +
                          '<p>{qualifiedName}</p>' +
                          '</div>' +
                          '<div class="x-clear"></div>' +
                          '</div>' +
                          '</tpl>',


    initComponent: function () {
        var me = this;

        var baseDataViewConfig = {
            xtype: 'dataview',
            cls: 'admin-data-view',
            tpl: me.dataViewItemTemplate,
            itemSelector: '.admin-data-view-row',
            trackOver: true,
            overItemCls: 'x-item-over',
            listeners: {
                itemclick: function (dataview, record, item, index, e, opts) {

                    me.updateRecentCookies(record);

                    me.fireEvent('contentTypeSelected', me, record);
                }
            }
        };

        // Recent section
        var recentContentTypesStore = Ext.create('Ext.data.Store', {
            model: 'Admin.model.contentStudio.ContentTypeModel'
        });

        this.updateRecentItems(recentContentTypesStore);

        var recentDataView = Ext.apply({
            store: recentContentTypesStore,
            emptyText: 'No recent content types'
        }, baseDataViewConfig);

        // Recommended section
        var recommendedContentTypesStore = Ext.create('Ext.data.Store', {
            model: 'Admin.model.contentStudio.ContentTypeModel',
            data: [
                { iconUrl: '/enonic/admin/rest/schema/image/ContentType:System:structured', name: 'Advanced Data', qualifiedName: 'path/1' }
            ],
            autoLoad: true
        });

        this.updateRecommendedItems(recommendedContentTypesStore, recentContentTypesStore);

        var recommendedDataView = Ext.apply({
            store: recommendedContentTypesStore,
            emptyText: 'No recommendations yet'
        }, baseDataViewConfig);

        // All section
        var allContentTypesStore = Ext.create('Admin.store.contentStudio.ContentTypeStore', {
            remoteSort: false,
            sorters: [
                {
                    property: 'name',
                    direction: 'ASC'
                }
            ]
        });

        var allDataView = Ext.apply({
            store: allContentTypesStore,
            emptyText: 'No matching content types'
        }, baseDataViewConfig);

        this.items = [
            me.header('Select Content Type', 'parent/of/new/content'),
            {
                region: 'west',
                width: 300,
                margin: '0 20 0 0',
                layout: {
                    type: 'vbox',
                    align: 'stretch'
                },
                defaults: {
                    autoScroll: true,
                    xtype: 'panel',
                    border: false,
                    cls: 'admin-box'
                },
                items: [
                    {
                        title: 'Recommended',
                        autoHeight: true,
                        margin: '0 0 20 0',
                        items: [
                            recommendedDataView
                        ]
                    },
                    {
                        title: 'Recent',
                        flex: 1,
                        items: [
                            recentDataView
                        ]
                    }
                ]
            },
            {
                region: 'center',
                layout: {
                    type: 'vbox',
                    align: 'stretch'
                },
                items: [
                    {
                        xtype: 'textfield',
                        emptyText: 'Content Type Search',
                        margin: '0 0 20 0',
                        enableKeyEvents: true,
                        listeners: {
                            keyup: function (field, event, opts) {
                                var value = field.getValue();
                                if (!Ext.isEmpty(value, false)) {
                                    allContentTypesStore.clearFilter(true);
                                    allContentTypesStore.filter({property: "name", value: value, anyMatch: true});
                                } else {
                                    allContentTypesStore.clearFilter();
                                }
                            }
                        }
                    },
                    {
                        flex: 1,
                        overflowY: 'auto',
                        border: false,
                        items: [
                            allDataView
                        ]
                    }
                ]
            },
            me.buttonRow({
                xtype: 'button',
                text: 'Cancel',
                handler: function (btn, evt) {
                    me.close();
                }
            })
        ];

        this.callParent(arguments);

        this.addEvents('contentTypeSelected')
    },

    updateRecentCookies: function (contentType) {

        var cookies = Ext.util.Cookies.get(this.cookieKey);
        var recentArray = cookies ? cookies.split(this.cookieSeparator) : [];

        var recentItem = this.serializeContentType(contentType);
        if (recentArray.length === 0 || recentArray[0] !== recentItem) {
            recentArray.unshift(recentItem);
        }

        if (recentArray.length > this.recentCount) {
            // constrain recent items quantity to recentCount
            recentArray = recentArray.slice(0, this.recentCount);
        }

        // add chosen item to recent list
        Ext.util.Cookies.set(this.cookieKey, recentArray.join(this.cookieSeparator));
    },

    updateRecentItems: function (recentStore) {

        recentStore.removeAll(true);

        var me = this;
        var cookies = Ext.util.Cookies.get(this.cookieKey);
        if (cookies) {

            var recentRecords = [];

            var recentArray = cookies.split(this.cookieSeparator);
            Ext.Array.each(recentArray, function (item, index, all) {
                recentRecords.push(me.parseContentType(item));
            });

            if (recentRecords.length > 0) {
                recentStore.loadData(recentRecords);
            }
        }
    },

    updateRecommendedItems: function (recommendedStore, recentStore) {

        // recommends the most frequently used content type
        recommendedStore.removeAll(true);

        var recommendedCount = 0;
        var recommendedRecord;
        var qualifiedNames = recentStore.collect('qualifiedName');
        var qualifiedRecords;
        for (var i = 0; i < qualifiedNames.length; i++) {

            qualifiedRecords = recentStore.queryBy(function (index) {
                return function (recentRecord, id) {
                    return recentRecord.get('qualifiedName') === qualifiedNames[index];
                }
            }(i));

            if (qualifiedRecords.getCount() > recommendedCount) {
                recommendedRecord = qualifiedRecords.get(0);
                recommendedCount = qualifiedRecords.getCount();
            }
        }

        if (recommendedRecord) {
            recommendedStore.loadRecords([recommendedRecord]);
        }
    },

    serializeContentType: function (contentType) {
        return Ext.JSON.encode(contentType.data);
    },

    parseContentType: function (string) {
        var json = Ext.JSON.decode(string, true);
        return Ext.create('Admin.model.contentStudio.ContentTypeModel', json);
    }

});

