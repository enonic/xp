Ext.define('Admin.view.contentManager.contextwindow.DeviceSelector', {
    extend: 'Ext.container.Container',
    alias: 'widget.contextWindowDeviceSelector',
    layout: {
        type: 'vbox',
        align: 'stretch'
    },

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
            height: 70,
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
                { name: 'width', type: 'string' },
                { name: 'height', type: 'string' }
            ]
        });

        Ext.create('Ext.data.Store', {
            id: 'contextWindowDeviceStore',
            model: 'ContextWindow.Devices',
            proxy: {
                type: 'ajax',
                url: '../../admin2/apps/content-manager/js/data/device-list.json',
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
            '           <div class="live-edit-device-icon"></div>',
            '           <div>',
            '               <h3>{name}</h3>',
            '           </div>',
            '       </div>',
            '   </div>',
            '</tpl>'
        );

        return new Ext.view.View({
            flex: 1,
            store: Ext.data.StoreManager.lookup('contextWindowDeviceStore'),
            tpl: template,
            cls: 'live-edit-component-list',
            itemSelector: 'div.live-edit-device',
            emptyText: 'No devices available'
        });
    },

    getContextWindow: function () {
        return this.up('contextWindow');
    }


});