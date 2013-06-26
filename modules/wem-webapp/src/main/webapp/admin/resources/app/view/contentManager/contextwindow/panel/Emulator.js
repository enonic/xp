Ext.define('Admin.view.contentManager.contextwindow.panel.Emulator', {
    extend: 'Ext.container.Container',
    alias: 'widget.contextWindowEmulator',
    layout: {
        type: 'vbox',
        align: 'stretch'
    },

    DEVICES_URL: '../admin2/apps/content-manager/js/data/context-window/devices.json',

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
            cls: 'admin-emulator-top-bar',
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
                    me.listView.getSelectionModel().select(0);
                }
            },
            autoLoad: true
        });

        var template = new Ext.XTemplate(
            '<tpl for=".">',
            '   <div class="admin-device-item">',
            '      <div class="admin-device-item-row">',
            '           <div class="admin-device-item-icon {[this.getIconCls(values.device_type)]}"></div>',
            '           <div class="admin-device-item-info">',
            '               <h3>{name}</h3>',
            '               <small>{width} x {height}</small>',
            '           </div>',
            '           <tpl if="rotatable">',
            '               <div class="admin-device-item-rotate-button icon-rotate-right" title="Rotate"></div>',
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
            store: Ext.getStore('contextWindowDeviceStore'),
            tpl: template,
            cls: 'admin-device-list',
            itemSelector: 'div.admin-device-item',
            emptyText: 'No devices available',
            selectedItemCls: 'admin-device-item-selected',
            listeners: {
                itemclick: {
                    fn: me.onItemClick,
                    scope: me
                }
            }
        });
    },

    resizeLiveEditFrame: function (deviceModel) {
        var me = this,
            iFrameEl = Ext.get(me.getContextWindow().getLiveEditIFrameDom().id),
            iFrameContainer = iFrameEl.parent(),
            deviceType = deviceModel.data.device_type,
            deviceIsRotatable = deviceModel.data.rotatable,
            width = deviceModel.data.width,
            height = deviceModel.data.height,
            useFullWidth = deviceType === 'monitor_full',
            newWidth = useFullWidth ? iFrameContainer.getWidth() : width,
            newHeight = useFullWidth ? iFrameContainer.getHeight() : height;

        if (this.deviceOrientation === 'horizontal' && deviceIsRotatable) {
            newWidth = height;
            newHeight = width;
        }
        iFrameEl.animate({
            duration: 200,
            easing: 'linear',
            to: {
                width: newWidth,
                height: newHeight
            },
            listeners: {
                afteranimate: function () {
                    if (useFullWidth) {
                        iFrameEl.setStyle('width', width);
                    }
                    if (useFullWidth) {
                        iFrameEl.setStyle('height', height);
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
        var targetIsRotateButton = Ext.fly(event.target).hasCls('admin-device-item-rotate-button');
        if (targetIsRotateButton) {
            me.deviceOrientation = me.deviceOrientation === 'vertical' ? 'horizontal' : 'vertical';
            me.rotateRotateButton(Ext.fly(event.target));
        } else {
            me.deviceOrientation = 'vertical';
            var rotateButtonDom = Ext.get(item).down('.admin-device-item-rotate-button');
            if (rotateButtonDom) {
                rotateButtonDom.removeCls('admin-device-item-rotate-button-horizontal');
            }
        }

        me.resizeLiveEditFrame(record);
    },

    rotateRotateButton: function (buttonEl) {
        if (this.deviceOrientation === 'horizontal') {
            buttonEl.addCls('admin-device-item-rotate-button-horizontal');
        } else {
            buttonEl.removeCls('admin-device-item-rotate-button-horizontal');
        }
    },

    getContextWindow: function () {
        return this.up('contextWindow');
    }

});