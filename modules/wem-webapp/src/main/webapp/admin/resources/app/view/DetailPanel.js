Ext.define('Admin.view.DetailPanel', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.detailPanel',
    requires: [
        'Admin.view.DropDownButton',
        'Admin.view.BaseContextMenu'
    ],

    layout: 'card',
    cls: 'admin-preview-panel admin-detail',
    border: false,

    showToolbar: true,

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

            if (this.isFullPage) {
                var actionsButton = this.down('dropDownButton');
                if (actionsButton) {
                    actionsButton.setVisible(false);
                }
            }
            if (this.singleSelection.tabs.length > 0) {
                this.changeTab(this.singleSelection.tabs[0].tab);
            }
        }
    },

    isFullPage: false,

    initComponent: function () {
        if (this.showToolbar) {
            this.tbar = this.createToolBar();
        }

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
     * Actions button
     * */
    actionButtonItems: [],

    getActionItems: function () {
        return this.actionButtonItems;
    },

    getActionButton: function () {
        var me = this;
        if (this.actionButtonItems.length < 1) {
            return {};
        }
        return {
            xtype: 'dropDownButton',
            text: 'Actions',
            height: 30,
            itemId: 'dropdown',
            width: 120,
            tdAttrs: {
                width: 120,
                valign: 'top',
                style: {
                    padding: '0 20px 0 0'
                }
            },
            menuItems: me.getActionItems()

        };

    },

    /*
     * Single selection
     * */
    singleTemplate: {

        photo: '<img src="{data.iconUrl}?size=80" style="width: 80px;" alt="{name}"/>',

        header: '<h1>{data.displayName}</h1><span>{data.key}{data.path}</span>'
    },

    singleSelection: {
        /* Example k - should be set in app*/
        tabs: [
        ],
        /* Example tabData - should be set in app*/
        tabData: {
        }
    },

    createSingleSelection: function (data) {
        var me = this;
        return {
            xtype: 'container',
            itemId: 'singleSelection',
            layout: 'border',
            defaults: {
                border: 0
            },
            overflowX: 'hidden',
            overflowY: 'hidden',
            // Attempt to get livePreview to re-render when scaling the detail window. Currently not working
            /*listeners: {
             resize: function (component) {

             var livePreview = component.down('#center').down('#livePreview');
             if (livePreview) {
             livePreview.doComponentLayout();
             }
             }
             },*/
            items: [
                {
                    xtype: 'container',
                    region: 'north',
                    cls: 'north',
                    margin: '5 0',
                    height: 100,
                    layout: {
                        type: 'table',
                        tableAttrs: {
                            style: {
                                width: '100%'
                            }
                        },
                        columns: 3
                    },
                    defaults: {
                        height: 100,
                        border: 0
                    },
                    items: [
                        {
                            xtype: 'component',
                            width: 80,
                            itemId: 'previewPhoto',
                            tpl: me.singleTemplate.photo,
                            data: data,
                            margin: '0 5 0 5',
                            tdAttrs: {
                                width: 100
                            }
                        },
                        {
                            xtype: 'component',
                            itemId: 'previewHeader',
                            tpl: me.singleTemplate.header,
                            data: data
                        },
                        me.getActionButton()
                    ]
                },
                {
                    xtype: 'container',
                    region: 'west',
                    cls: 'west',
                    width: 200,
                    tpl: Ext.create('Ext.XTemplate', '<ul class="admin-detail-nav">' +
                                                     '<tpl for=".">' +
                                                     '<li data-tab="{tab}">{displayName}</li>' +
                                                     '</tpl>' +
                                                     '</ul>'),
                    data: me.singleSelection.tabs,
                    listeners: {
                        click: {
                            element: 'el', //bind to the underlying el property on the panel
                            fn: function (evt, element) {
                                var tab = element.attributes['data-tab'].value;
                                me.changeTab(tab);
                            }
                        }
                    }
                },
                {
                    region: 'center',
                    cls: 'center',
                    xtype: 'container',
                    itemId: 'center'
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
            autoScroll: true,
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
            autoScroll: true,
            bodyStyle: {
                border: 'none'
            },
            tpl: this.smallBoxTemplate,
            data: data
        };
    },

    /*
     * Toolbar
     */

    createToolBar: function () {
        return {
            xtype: 'toolbar',
            itemId: 'defaultToolbar',
            cls: 'admin-white-toolbar',
            items: [
                {
                    xtype: 'tbtext',
                    itemId: 'selectionTxt',
                    text: 'Stub text'
                }
            ]
        };
    },

    /*
     * Data
     */
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
        }
        this.setDataCallback(data);
    },

    getData: function () {
        return this.data;
    },

    /*--------*/
    changeTab: function (selectedTab) {
        var currentTab = this.singleSelection.tabData[selectedTab];
        var target = this.down('#center');
        // This clears the center everytime we click. This might not be the fastest solution.
        target.remove(target.child());
        target.update('');
        if (currentTab.html) {
            target.update(currentTab.html);
        } else if (currentTab.item) {
            target.add(currentTab.item);
            currentTab.callback(target);
        }

        var element = Ext.dom.Query.select('*[data-tab=' + selectedTab + ']')[0];
        var children = element.parentElement.children;
        for (var i = 0; i < children.length; i++) {
            children[i].className = '';
        }
        element.className = 'active';

    }
});
