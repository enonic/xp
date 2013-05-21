Ext.define('Admin.view.userstore.preview.UserstorePreviewPanel', {
    extend: 'Ext.panel.Panel',
    alias: 'widget.userstorePreviewPanel',

    autoScroll: true,
    layout: 'card',
    cls: 'admin-preview-panel',

    collapsible: true,

    showToolbar: true,

    initComponent: function () {
        this.items = [
            this.createNoneSelection(),
            this.createUserstoreSelection()
        ];

        this.callParent(arguments);
    },

    createUserstoreSelection: function () {
        var Templates_userstore_previewCommonInfo =
        		'<div class="container">' +
        		    '<table>' +
        		        '<thead>' +
        		        '<tr>' +
        		            '<th colspan="2">General</th>' +
        		        '</tr>' +
        		        '</thead>' +
        		        '<tbody>' +
        		        '<tr>' +
        		            '<td class="label">Created:</td>' +
        		            '<td>{created}</td>' +
        		        '</tr>' +
        		        '<tr>' +
        		            '<td class="label">Modified:</td>' +
        		            '<td>{lastModified}</td>' +
        		        '</tr>' +
        		        '</tbody>' +
        		    '</table>' +
        		'</div>' +
        		'<div class="container">' +
        		    '<table>' +
        		        '<thead>' +
        		        '<tr>' +
        		            '<th colspan="2">Statistics</th>' +
        		        '</tr>' +
        		        '</thead>' +
        		        '<tbody>' +
        		        '<tr>' +
        		            '<td class="label">User count:</td>' +
        		            '<td>{userCount}</td>' +
        		        '</tr>' +
        		        '<tr>' +
        		            '<td class="label">Group count:</td>' +
        		            '<td>{groupCount}</td>' +
        		        '</tr>' +
        		        '</tbody>' +
        		    '</table>' +
        		'</div>' +
        		'<div class="container">' +
        		    '<table>' +
        		        '<thead>' +
        		        '<tr>' +
        		            '<th colspan="2">Connector</th>' +
        		        '</tr>' +
        		        '</thead>' +
        		        '<tbody>' +
        		        '<tr>' +
        		            '<td class="label">Name:</td>' +
        		            '<td>{connectorName}<tpl if="connectorName == null">Local</tpl></td>' +
        		        '</tr>' +
        		        '<tr>' +
        		            '<td class="label">Plugin:</td>' +
        		            '<td>{plugin}</td>' +
        		        '</tr>' +
        		        '<tr>' +
        		            '<td class="label">User Policy:</td>' +
        		            '<td>{userPolicy}</td>' +
        		        '</tr>' +
        		        '<tr>' +
        		            '<td class="label">Group Policy:</td>' +
        		            '<td>{groupPolicy}</td>' +
        		        '</tr>' +
        		        '</tbody>' +
        		    '</table>' +
        		'</div>';

        var Templates_userstore_previewHeader =
        		'<h1>{name}' +
        		    '<tpl if="defaultStore">(default)</tpl>' +
        		'</h1><span>{[values.connectorName==null ? "Local" : values.connectorName ]}</span>';

        var Templates_userstore_previewPhoto =
        		'<img src="resources/images/icons/128x128/userstore.png" alt="{name}"/>';

        return {
            xtype: 'container',
            itemId: 'userstoreDetails',
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
                    tpl: Templates_userstore_previewPhoto,
                    data: this.data,
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
                            tpl: Templates_userstore_previewHeader,
                            data: this.data
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
                                            itemId: 'configurationArea'
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
                    tpl: Templates_userstore_previewCommonInfo,
                    data: this.data
                }
            ]
        };
    },

    createNoneSelection: function () {
        var Templates_userstore_noUserstoreSelected = '<div>No userstore selected</div>';
        var tpl = new Ext.XTemplate(Templates_userstore_noUserstoreSelected);
        var panel = {
            xtype: 'panel',
            itemId: 'noneSelectedPanel',
            styleHtmlContent: true,
            padding: 10,
            border: 0,
            tpl: tpl,
            data: {}
        };

        return panel;
    },

    setData: function (data) {
        if (data) {

            this.data = data;

            var previewHeader = this.down('#previewHeader');
            previewHeader.update(data);

            var previewPhoto = this.down('#previewPhoto');
            previewPhoto.update(data);

            var previewInfo = this.down('#previewInfo');
            previewInfo.update(data);

            var configurationArea = this.down('#configurationArea');
            configurationArea.setValue(data.configXML);
            this.getLayout().setActiveItem('userstoreDetails');
        } else {
            this.getLayout().setActiveItem('noneSelectedPanel');
        }
    },

    getData: function () {
        return this.data;
    }


});