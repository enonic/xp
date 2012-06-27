Ext.define('App.view.DetailPanel', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.systemCacheDetail',

    requires: [
        'App.view.GaugePanel'
    ],

    title: 'Cache Details',
    split: true,
    autoScroll: true,
    defaults: {
        border: false
    },
    bodyPadding: 10,

    cache: {
        data: {
            timeToLive: '-',
            objectCount: 0,
            diskCapacity: '-',
            cacheHits: 0,
            cacheMisses: 0
        }
    },

    tbar: {
        border: false,
        padding: 5,
        items: [
            {
                text: 'Clear Cache',
                iconCls: 'icon-delete',
                action: 'clearCache',
                disabled: true
            },
            {
                text: 'Reset Statistics',
                iconCls: 'icon-delete',
                action: 'clearStats',
                disabled: true
            },
            '->',
            {
                text: 'Refresh',
                itemId: 'toggleRefresh',
                iconCls: 'icon-refresh',
                pressed: true,
                enableToggle: true,
                action: 'refreshCache'
            }
        ]
    },

    initComponent: function () {

        this.items = [
            {
                xtype: 'panel',
                itemId: 'cacheDetailHeader',
                height: 40,
                anchor: '100%',
                styleHtmlContent: true,
                tpl: new Ext.XTemplate(Templates.cache.detailPanelHeader)
            },
            {
                xtype: 'form',
                itemId: 'cacheDetailFieldset',
                anchor: '100%',
                items: [
                    {
                        xtype: 'fieldset',
                        title: 'Info',
                        items: [
                            {
                                xtype: 'displayfield',
                                name: 'timeToLive',
                                fieldLabel: 'Time To Live'
                            },
                            {
                                xtype: 'displayfield',
                                name: 'memoryCapacity',
                                fieldLabel: 'Size'
                            }
                        ]
                    }
                ]
            },
            {
                xtype: 'container',
                height: 300,
                itemId: 'cacheDetailGauges',
                layout: {
                    type: 'hbox',
                    align: 'stretch'
                },

                items: [
                    {
                        xtype: 'systemCacheGauge',
                        itemId: 'usageGauge',
                        title: 'Total Usage',
                        flex: 1,
                        margins: '0 5 0 0'
                    },
                    {
                        xtype: 'systemCacheGauge',
                        itemId: 'hitGauge',
                        title: 'Hit Rate',
                        flex: 1,
                        margins: '0 0 0 5',
                        gaugeColor: '#70be00'
                    }
                ]
            }
        ];

        this.callParent(arguments);

        if (this.cache) {
            this.updateDetail(this.cache);
        }

    },

    setCache: function (cache) {
        this.cache = cache;
    },

    getCache: function () {
        return this.cache;
    },

    clearDetail: function () {
        this.updateDetail(this.__proto__.cache);
    },

    updateToolbar: function () {
        var clearStats = this.down('button[action=clearStats]');
        var clearCache = this.down('button[action=clearCache]');
        var flag = Ext.isEmpty(this.cache.data.name);
        clearStats.setDisabled(flag);
        clearCache.setDisabled(flag);
    },

    updateDetail: function (cache) {
        if (cache) {
            // sets the new record as current cache
            this.setCache(cache);
            this.updateToolbar();

            var data = this.cache ? this.cache.data : {};
            this.getComponent('cacheDetailHeader').update(data);
            this.getComponent('cacheDetailFieldset').getForm().setValues(data);

            var count = data.objectCount || 0;
            var size = data.memoryCapacity || 0;
            var hits = data.cacheHits || 0;
            var total = hits + ( data.cacheMisses || 0 );

            var gauges = this.getComponent('cacheDetailGauges');
            gauges.getComponent('usageGauge').updateData([
                {
                    name: 'usageGauge',
                    data: ( size > 0 ? Math.floor(count * 100 / size) : 0 )
                }
            ]);
            gauges.getComponent('hitGauge').updateData([
                {
                    name: 'hitGauge',
                    data: ( total > 0 ? Math.floor(hits * 100 / total) : 0 )
                }
            ]);

        } else if (this.cache.data.name) {
            // loads the current cache updates from server
            var me = this;
            Ext.Ajax.request({
                url: 'data/system/cache/info',
                method: 'GET',
                params: { name: this.cache.data.name },
                success: function (response, opts) {
                    var cache = Ext.decode(response.responseText);
                    me.updateDetail({ data: cache });
                },
                failure: function (response, opts) {
                    Ext.Msg.alert('Warning', 'Cache wasn\'t updated.');
                }
            });
        }
    }

});
