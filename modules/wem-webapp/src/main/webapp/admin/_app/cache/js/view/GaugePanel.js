Ext.define('App.view.GaugePanel', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.systemCacheGauge',

    title: 'Gauge Panel',
    layout: 'fit',
    bodyPadding: '20 0 0',

    gaugeColor: '#4e8cc6',
    gaugeStore: null,

    initComponent: function () {

        this.gaugeStore = Ext.create('Ext.data.JsonStore', {
            fields: ['name', 'data']
        });

        this.items = {
            xtype: 'chart',
            insetPadding: 30,
            animate: {
                easing: 'elasticIn',
                duration: 1000
            },
            store: this.gaugeStore,
            axes: [
                {
                    type: 'gauge',
                    position: 'gauge',
                    minimum: 0,
                    maximum: 100,
                    steps: 10
                }
            ],
            series: [
                {
                    type: 'gauge',
                    field: 'data',
                    donut: 40,
                    colorSet: [this.gaugeColor, '#ddd']
                }
            ]
        };
        var me = this;
        this.tools = [
            {
                type: 'refresh',
                tooltip: 'Refresh form Data',
                // hidden:true,
                handler: function (event, toolEl, panel) {
                    me.updateData();
                }
            }
        ];
        this.callParent(arguments);

    },

    updateData: function (data) {
        if (data) {
            this.gaugeStore.loadData(data);
        }
    }

});