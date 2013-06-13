Ext.define('Admin.view.contentManager.contextwindow.DeviceSelector', {
    extend: 'Ext.container.Container',
    alias: 'widget.contextWindowDeviceSelector',
    layout: {
        type: 'vbox',
        align: 'stretch'
    },

    DEVICE_STORE_URL: '../../admin2/apps/content-manager/js/data/context-window/devices.json',

    topText: undefined,
    listView: undefined,

    initComponent: function () {
        this.topText = this.createTopText();
        this.listView = this.createListView();
        this.items = [
            this.topText,
            this.listView
        ];
        this.callParent(arguments);
    },

    createTopText: function () {
        return new Ext.Component({
            height: 40,
            cls: 'live-edit-device-top-bar',
            html: '<p>Emulate different client types</p>'
        });
    },

    /**
     * @returns {Ext.view.View}
     */
    createListView: function () {
        var me = this;

        // fixme: formalize model, store 'n stuff

        Ext.define('ContextWindow.Devices', {
            extend: 'Ext.data.Model',
            fields: [
                { name: 'name', type: 'string' },
                { name: 'type', type: 'string' },
                { name: 'width', type: 'string' },
                { name: 'height', type: 'string' }
            ]
        });

        Ext.create('Ext.data.Store', {
            id: 'contextWindowDeviceStore',
            model: 'ContextWindow.Devices',
            proxy: {
                type: 'ajax',
                url: me.DEVICE_STORE_URL,
                reader: {
                    type: 'json',
                    root: 'devices'
                }
            },
            autoLoad: true
        });

        var template = new Ext.XTemplate(
            '<tpl for=".">',
            '   <div class="live-edit-device">',
            '      <div class="live-edit-device-row">',
            '           <div class="live-edit-device-icon {[this.resolveIconCls(values.type)]}"></div>',
            '           <div class="live-edit-device-info">',
            '               <h3>{name}</h3>',
            '               <small>{width} x {height}</small>',
            '           </div>',
            '           <div class="live-edit-device-rotate-button icon-rotate-right"></div>',
            '       </div>',
            '   </div>',
            '</tpl>',
            {
                resolveIconCls: function (deviceType) {
                    var iconCls;
                    switch (deviceType) {
                    case 'pc':
                        iconCls = 'icon-desktop';
                        break;
                    case 'mobile':
                        iconCls = 'icon-mobile-phone';
                        break;
                    case 'tablet':
                        iconCls = 'icon-tablet';
                        break;
                    default:
                        iconCls = '';
                    }
                    return iconCls;
                }
            }
        );

        return new Ext.view.View({
            flex: 1,
            store: Ext.data.StoreManager.lookup('contextWindowDeviceStore'),
            tpl: template,
            cls: 'live-edit-device-list',
            itemSelector: 'div.live-edit-device',
            emptyText: 'No devices available',
            selectedItemCls: 'live-edit-device-selected',
            listeners: {
                itemclick: function (view, record, item, index, event) {
                    if (Ext.fly(event.target).hasCls('live-edit-device-rotate-button')) {
                        me.resizeIFrame(record, true);
                    } else {
                        me.resizeIFrame(record, false);
                    }
                }
            }
        });
    },

    resizeIFrame: function (deviceModel, rotate) {

        // fixme: rotate

        var iFrame = Ext.get(this.getContextWindow().getLiveEditIFrame().id),
            iFrameContainer = Ext.get('live-edit-iframe-container'),
            width = deviceModel.data.width,
            height = deviceModel.data.height,
            widthHasPercentUnit = width.indexOf('%') > -1,
            heightHasPercentUnit = height.indexOf('%') > -1,
            // Ext animate does not work on percent units so we have to use the iFrame container's current dimensions
            newWidth = widthHasPercentUnit ? iFrameContainer.getWidth() : width,
            newHeight = heightHasPercentUnit ? iFrameContainer.getWidth() : height;

        iFrame.animate({
            duration: 300,
            to: {
                width: newWidth,
                height: newHeight
            },
            listeners: {
                afteranimate: function () {
                    if (widthHasPercentUnit) {
                        iFrame.setStyle('width', width);
                    }
                    if (heightHasPercentUnit) {
                        iFrame.setStyle('height', height);
                    }
                }
            }
        });
    },

    getContextWindow: function () {
        return this.up('contextWindow');
    }

});