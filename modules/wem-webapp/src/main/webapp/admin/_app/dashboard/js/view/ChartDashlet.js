Ext.define('App.view.ChartDashlet', {
    extend: 'App.view.Dashlet',
    alias: 'widget.chartDashlet',

    requires: [
        'App.view.Dashlet',
        "Ext.data.JsonStore",
        "Ext.chart.theme.Base",
        "Ext.chart.series.Series",
        "Ext.chart.series.Line",
        "Ext.chart.axis.Numeric"
    ],

    height: 300,
    autoScroll: false,
    bodyPadding: 0,
    title: 'Chart Dashlet',
    html: 'Chart dashlet text',
    listeners: {
        afterrender: function () {
            this.doLayout();
        }
    },

    generateData: function (n, floor) {
        var data = [],
            i;

        floor = (!floor && floor !== 0) ? 20 : floor;

        for (i = 0; i < (n || 12); i++) {
            data.push({
                name: i + 1,
                data1: Math.floor(Math.max((Math.random() * 100), floor)),
                data2: Math.floor(Math.max((Math.random() * 100), floor)),
                data3: Math.floor(Math.max((Math.random() * 100), floor))
            });
        }
        return data;
    },

    initComponent: function () {
        this.items = {
            xtype: 'chart',
            style: 'background:#fff',
            animate: true,
            store: Ext.create("Ext.data.JsonStore", {
                fields: ["name", "data1", "data2", "data3"],
                data: this.generateData()
            }),
            shadow: true,
            theme: 'Category1',
            legend: {
                position: 'bottom'
            },
            axes: [
                {
                    type: 'Numeric',
                    minimum: 0,
                    position: 'left',
                    fields: ['data1', 'data2', 'data3'],
                    title: 'Number of Hits',
                    minorTickSteps: 1,
                    grid: {
                        odd: {
                            opacity: 1,
                            fill: '#ddd',
                            stroke: '#bbb',
                            'stroke-width': 0.5
                        }
                    }
                },
                {
                    type: 'Category',
                    position: 'bottom',
                    fields: ['name'],
                    title: 'Month of the Year'
                }
            ],
            series: [
                {
                    type: 'line',
                    highlight: {
                        size: 7,
                        radius: 7
                    },
                    axis: 'left',
                    xField: 'name',
                    yField: 'data1',
                    markerConfig: {
                        type: 'cross',
                        size: 4,
                        radius: 4,
                        'stroke-width': 0
                    }
                },
                {
                    type: 'line',
                    highlight: {
                        size: 7,
                        radius: 7
                    },
                    axis: 'left',
                    smooth: true,
                    xField: 'name',
                    yField: 'data2',
                    markerConfig: {
                        type: 'circle',
                        size: 4,
                        radius: 4,
                        'stroke-width': 0
                    }
                },
                {
                    type: 'line',
                    highlight: {
                        size: 7,
                        radius: 7
                    },
                    axis: 'left',
                    smooth: true,
                    fill: true,
                    xField: 'name',
                    yField: 'data3',
                    markerConfig: {
                        type: 'triangle',
                        size: 4,
                        radius: 4,
                        'stroke-width': 0
                    }
                }
            ]
        };
        this.callParent(arguments)
    }

});
