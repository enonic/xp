Ext.define('Admin.view.contentStudio.DetailPanel', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.contentTypeDetailPanel',
    layout: 'card',

    header: false,

    cls: 'admin-preview-panel',
    overflowX: 'hidden',
    overflowY: 'auto',

    typeField: 'qualifiedName',
    iconClasses: {
        "system:content": 'icon-icomoon-content-128',
        "system:folder": 'icon-icomoon-folder-128',
        "system:file": 'icon-icomoon-file-128',
        "system:page": 'icon-icomoon-page-128',
        "system:space": 'icon-icomoon-space-128',
        "system:shortcut": 'icon-icomoon-shortcut-128',
        "system:structured": 'icon-icomoon-structured-128',
        "system:unstructured": 'icon-icomoon-unstructured-128'
    },

    initComponent: function () {

        this.activeItem = this.resolveActiveItem(this.data);

        var noneSelectedCmp = this.createNoSelection();
        var previewCt = this.createSingleSelection(this.data);

        this.items = [
            noneSelectedCmp,
            previewCt
        ];

        this.callParent(arguments);
    },

    resolveIconClass: function (node) {
        var iconCls = '';
        var nodeType = node[this.typeField];
        var typeCls = nodeType && this.iconClasses && this.iconClasses[nodeType.toLowerCase()];
        if (typeCls) {
            iconCls = typeCls;
        }
        return iconCls;
    },

    createSingleSelection: function (data) {

        var singleData;
        if (Ext.isArray(data) && data.length > 0) {
            singleData = data[0];
        } else {
            singleData = data;
        }
        if (singleData) {
            if (Ext.isEmpty(singleData.iconCls)) {
                singleData.iconCls = this.resolveIconClass(singleData);
            }
            singleData = singleData.data || singleData;
        }

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
                    itemId: 'previewIcon',
                    tpl: Templates.contentStudio.previewIcon,
                    data: singleData,
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
                            tpl: Templates.contentStudio.previewHeader,
                            data: singleData
                        },
                        {
                            flex: 1,
                            cls: 'center',
                            xtype: 'tabpanel',
                            items: [
                                {
                                    title: "Configuration",
                                    itemId: 'configurationTab',
                                    layout: 'anchor',
                                    items: [
                                        {
                                            xtype: 'textarea',
                                            cls: 'config-container',
                                            grow: true,
                                            readOnly: true,
                                            anchor: '100%',
                                            itemId: 'configurationArea',
                                            value: singleData ? singleData.configXml : undefined
                                        }
                                    ]
                                }
                            ]
                        }
                    ]
                },
                {
                    xtype: 'component',
                    width: 300,
                    margin: 5,
                    itemId: 'previewInfo',
                    cls: 'east',
                    tpl: Templates.contentStudio.previewCommonInfo,
                    data: singleData
                }
            ]
        };
    },

    createNoSelection: function () {
        var tpl = new Ext.XTemplate(Templates.contentStudio.noContentTypeSelected);

        return {
            xtype: 'component',
            itemId: 'noSelection',
            styleHtmlContent: true,
            border: true,
            padding: 5,
            tpl: tpl,
            data: {}
        };
    },

    setData: function (data) {

        if (!data) {
            return;
        }

        this.data = data;

        if (Ext.isEmpty(this.data)) {

            this.getLayout().setActiveItem('noSelection');

        } else if (Ext.isObject(this.data) || this.data.length === 1) {

            var singleData;
            if (Ext.isArray(this.data)) {
                singleData = this.data[0];
            } else {
                singleData = this.data;
            }
            if (singleData) {
                if (Ext.isEmpty(singleData.iconCls)) {
                    singleData.set('iconCls', this.resolveIconClass(singleData));
                }
                singleData = singleData.data || singleData;
            }

            var previewHeader = this.down('#previewHeader');
            previewHeader.update(singleData);

            var previewPhoto = this.down('#previewIcon');
            previewPhoto.update(singleData);

            var previewInfo = this.down('#previewInfo');
            previewInfo.update(singleData);

            var configurationArea = this.down('#configurationArea');
            configurationArea.setValue(singleData.configXml);

            this.getLayout().setActiveItem('singleSelection');

        } //else if (this.data.length > 1 && this.data.length <= 10) {
        //TODO: Do we need multiple selection here?
        //var largeBox = this.down('#largeBoxSelection');
        //largeBox.update(this.data);

        //this.getLayout().setActiveItem(largeBox);

        //} else {

        //var smallBox = this.down('#smallBoxSelection');
        //smallBox.update(this.data);

        //this.getLayout().setActiveItem(smallBox);

        //}
    },

    getData: function () {
        return this.data;
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
    }

});
