Ext.define('Admin.view.contentManager.contextwindow.panel.DeviceSelector', {
    extend: 'Ext.container.Container',
    alias: 'widget.contextWindowDeviceSelector',
    layout: {
        type: 'vbox',
        align: 'stretch'
    },

    DEVICES_URL: '../../admin2/apps/content-manager/js/data/context-window/devices.json',

    topTextCmp: undefined,
    listView: undefined,

    deviceOrientation: 'vertical', // vertical|horizontal

    initComponent: function () {
        this.topTextCmp = this.createTopTextCmp();
        this.listView = this.createListView();
        this.items = [
            this.topTextCmp,
            this.listView
        ];
        this.callParent(arguments);
    },

    /**
     * @returns {Ext.Component}
     */
    createTopTextCmp: function () {
        return new Ext.Component({
            height: 40,
            cls: 'live-edit-device-top-bar',
            html: '<p>Emulate different client\'s physical sizes</p>'
        });
    },

    /**
     * @returns {Ext.view.View}
     */
    createListView: function () {
        var me = this;
        var monitorFullModelData = {
            "name": "Monitor full (default)",
            "device_type": "monitor_full",
            "width": "100%",
            "height": "100%",
            "rotatable": false
        };

        // fixme: formalize model, store 'n stuff

        Ext.define('Admin.ContextWindow.DeviceModel', {
            extend: 'Ext.data.Model',
            fields: [
                { name: 'name', type: 'string' },
                { name: 'device_type', type: 'string' },
                { name: 'width', type: 'auto' },
                { name: 'height', type: 'auto' },
                { name: 'rotatable', type: 'boolean' }
            ]
        });

        Ext.create('Ext.data.Store', {
            id: 'contextWindowDeviceStore',
            model: 'Admin.ContextWindow.DeviceModel',
            proxy: {
                type: 'ajax',
                url: me.DEVICES_URL,
                reader: {
                    type: 'json',
                    root: 'devices'
                }
            },
            listeners: {
                load: function (store, records) {
                    var monitorFullModel = new Admin.ContextWindow.DeviceModel(monitorFullModelData);
                    store.insert(0, monitorFullModel);
                }
            },
            autoLoad: true
        });

        var template = new Ext.XTemplate(
            '<tpl for=".">',
            '   <div class="live-edit-device">',
            '      <div class="live-edit-device-row">',
            '           <div class="live-edit-device-icon {[this.getIconCls(values.device_type)]}"></div>',
            '           <div class="live-edit-device-info">',
            '               <h3>{name}</h3>',
            '               <small>{width} x {height}</small>',
            '           </div>',
            '           <tpl if="rotatable">',
            '               <div class="live-edit-device-rotate-button icon-rotate-right" title="Rotate"></div>',
            '           </tpl>',
            '       </div>',
            '   </div>',
            '</tpl>',
            {
                getIconCls: function (deviceType) {
                    return me.resolveIconCls(deviceType);
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
                itemclick: {
                    fn: me.onItemClick,
                    scope: me
                }
            }
        });
    },

    resizeLiveEditFrame: function (deviceModel) {
        var panelHelper = Admin.view.contentManager.contextwindow.panel.Helper,
            iFrame = Ext.get(panelHelper.getLiveEditIFrameDomEl().id),
            iFrameContainer = panelHelper.getLiveEditIFrameContainerEl(),
            deviceType = deviceModel.data.device_type,
            isRotatable = deviceModel.data.rotatable,
            width = deviceModel.data.width,
            height = deviceModel.data.height,
            useFullWidth = deviceType === 'monitor_full',
            newWidth = useFullWidth ? iFrameContainer.getWidth() : width,
            newHeight = useFullWidth ? iFrameContainer.getWidth() : height;

        if (this.deviceOrientation === 'horizontal' && isRotatable) {
            newWidth = height;
            newHeight = width;
        }
        iFrame.animate({
            duration: 450,
            to: {
                width: newWidth,
                height: newHeight
            },
            listeners: {
                afteranimate: function () {
                    if (useFullWidth) {
                        iFrame.setStyle('width', width);
                    }
                    if (useFullWidth) {
                        iFrame.setStyle('height', height);
                    }
                }
            }
        });
    },

    resolveIconCls: function (deviceType) {
        var iconCls;
        switch (deviceType) {
        case 'monitor':
            iconCls = 'icon-desktop';
            break;
        case 'monitor_full':
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
    },

    onItemClick: function (view, record, item, index, event) {
        var me = this;
        var targetIsRotateButton = Ext.fly(event.target).hasCls('live-edit-device-rotate-button');
        if (targetIsRotateButton) {
            me.deviceOrientation = me.deviceOrientation === 'vertical' ? 'horizontal' : 'vertical';
            me.rotateRotateButton(Ext.fly(event.target));
        } else {
            me.deviceOrientation = 'vertical';
            var rotateButtonDom = Ext.get(item).down('.live-edit-device-rotate-button');
            if (rotateButtonDom) {
                rotateButtonDom.removeCls('live-edit-device-rotate-button-horizontal');
            }
        }

        me.resizeLiveEditFrame(record);
    },

    rotateRotateButton: function (buttonEl) {
        if (this.deviceOrientation === 'horizontal') {
            buttonEl.addCls('live-edit-device-rotate-button-horizontal');
        } else {
            buttonEl.removeCls('live-edit-device-rotate-button-horizontal');
        }
    }

});