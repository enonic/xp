Ext.define('Admin.view.DetailPanel', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.detailPanel',
    layout: 'card',
    autoScroll: true,
    cls: 'admin-preview-panel admin-detail',
    border: false,

    listeners: {
        afterrender: function (detail) {
            detail.el.on('click', function (event, target, opts) {
                var key = target.attributes.getNamedItem('id').nodeValue.split('remove-from-selection-button:')[1];
                detail.fireEvent('deselect', key);
            }, this, {
                delegate: '.deselect'
            });
            detail.el.on('click', function (event, target, opts) {
                detail.fireEvent('clearselection');
            }, this, {
                delegate: '.clearSelection'
            });
        }
    },

    initComponent: function () {
        this.callParent(arguments);
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
    /*
     * Single selection
     * */
    singleTemplate: {

        photo: '<img src="{data.iconUrl}?size=100" style="width: 100px;" alt="{name}"/>',

        header: '<h1>{data.displayName}</h1><span>{data.description}</span>',

        info: '<div class="container"><table><thead>' +
              '<tr><th colspan="2">General</th></tr>' +
              '</thead><tbody>' +
              '<tr><td class="label">Created:</td><td>{data.createdTime}</td></tr>' +
              '<tr><td class="label">Modified:</td><td>{data.modifiedTime}</td></tr>' +
              '</tbody></table></div>'
    },

    singleSelection: {
        tabs: []
    },

    createSingleSelection: function (data) {
        var me = this;
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
                    tpl: me.singleTemplate.photo,
                    data: data,
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
                            tpl: me.singleTemplate.header,
                            data: data
                        },
                        {
                            flex: 1,
                            cls: 'center',
                            xtype: 'tabpanel',
                            items: me.singleSelection.tabs
                        }
                    ]
                },
                {
                    xtype: 'component',
                    width: 300,
                    margin: 5,
                    itemId: 'previewInfo',
                    cls: 'east',
                    tpl: me.singleTemplate.info,
                    data: data
                }
            ]
        };
    },

    largeBoxTemplate: '<tpl for=".">' +
                      '<div id="selected-item-box-{data.key}" class="admin-selected-item-box large clearfix">' +
                      '<div class="left"><img src="{data.iconUrl}?size=32" alt="{data.name}"/></div>' +
                      '<div class="center"><h6>{data.displayName}</h6><p>{data.description}</p></div>' +
                      '<div class="right">' +
                      '<a id="remove-from-selection-button:{internalId}" class="deselect" href="javascript:;"></a>' +
                      '</div>' +
                      '</div>' +
                      '</tpl>',

    createLargeBoxSelection: function (data) {
        return {
            itemId: 'largeBoxSelection',
            xtype: 'component',
            styleHtmlContent: true,
            padding: 10,
            bodyStyle: {
                border: 'none'
            },
            listeners: {
                click: {
                    element: 'body',
                    fn: this.deselectItem,
                    scope: this
                }
            },
            tpl: this.largeBoxTemplate,
            data: data
        };
    },

    smallBoxTemplate: '<tpl for=".">' +
                      '<div id="selected-item-box-{data.key}" class="admin-selected-item-box small clearfix">' +
                      '<div class="left"><img src="{data.iconUrl}?size=20" alt="{data.name}"/></div>' +
                      '<div class="center">{data.displayName}</div>' +
                      '<div class="right">' +
                      '<a id="remove-from-selection-button:{data.key}" class="deselect" href="javascript:;"></a>' +
                      '</div>' +
                      '</div>' +
                      '</tpl>',

    createSmallBoxSelection: function (data) {
        return {
            itemId: 'smallBoxSelection',
            xtype: 'component',
            styleHtmlContent: true,
            padding: 10,
            bodyStyle: {
                border: 'none'
            },
            tpl: this.smallBoxTemplate,
            data: data
        };
    },

    /*
     * Data
     * */
    setDataCallback: function (data) {
    },

    resolveActiveItem: function (data) {
        var activeItem;
        if (Ext.isEmpty(this.data)) {
            activeItem = 'noSelection';
        } else if (Ext.isObject(this.data) || this.data.length === 1) {
            activeItem = 'singleSelection';
        } else if (this.data.length > 1 && this.data.length <= 10) {
            activeItem = 'largeBoxSelection';
        } else {
            activeItem = 'smallBoxSelection';
        }
        return activeItem;
    },

    resolveActiveData: function (data) {
        var activeData;
        if (Ext.isArray(data) && data.length === 1) {
            activeData = data[0];
        } else {
            activeData = data;
        }
        return activeData;
    },

    updateActiveItem: function (data, item) {
        item = item || this.getLayout().getActiveItem();
        if ('singleSelection' === item.itemId) {
            var previewHeader = item.down('#previewHeader');
            previewHeader.update(data);

            var previewPhoto = item.down('#previewPhoto');
            previewPhoto.update(data);

            var previewInfo = item.down('#previewInfo');
            previewInfo.update(data);
        } else if ('largeBoxSelection' === item.itemId || 'smallBoxSelection' === item.itemId) {
            item.update(data);
        }
    },

    setData: function (data) {
        this.data = data;
        var toActivate = this.resolveActiveItem(data);
        var active = this.getLayout().getActiveItem();
        if (active.itemId !== toActivate) {
            active = this.getLayout().setActiveItem(toActivate);
        }
        if (active) {
            var activeData = this.resolveActiveData(data);
            this.updateActiveItem(activeData, active);
            /*            this.updateTitle(activeData);*/
        }
        this.setDataCallback(data);
    },

    getData: function () {
        return this.data;
    }
});
