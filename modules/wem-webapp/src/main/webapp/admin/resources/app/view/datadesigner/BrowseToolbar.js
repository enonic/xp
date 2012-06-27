Ext.define('Admin.view.datadesigner.BrowseToolbar', {
    extend: 'Ext.toolbar.Toolbar',
    alias: 'widget.datadesigner.browseToolbar',

    defaults: {
        scale: 'medium',
        iconAlign: 'top'
    },

    initComponent: function () {
        var buttonDefaults = {
            scale: 'medium',
            iconAlign: 'top',
            minWidth: 64
        };

        this.items = [
            {
                xtype: 'buttongroup',
                columns: 1,
                defaults: buttonDefaults,
                items: [
                    {
                        text: 'New',
                        iconCls: 'icon-data-designer-24',
                        menu: [
                            {text: 'Shortcut', menu: [
                                {
                                    text: 'Site'
                                }
                            ]},
                            {text: 'Folder'},
                            {text: 'Structured'},
                            {text: 'Media', menu: [
                                {
                                    text: 'unknown'
                                },
                                {
                                    text: 'Audio'
                                },
                                {
                                    text: 'Image'
                                },
                                {
                                    text: 'Video'
                                },
                                {
                                    text: 'Document'
                                },
                                {
                                    text: 'Presentation'
                                },
                                {
                                    text: 'Spreadsheets'
                                },
                                {
                                    text: 'XML'
                                },
                                {
                                    text: 'HTML'
                                },
                                {
                                    text: 'Archive'
                                }
                            ]},
                            {text: 'Form', menu: [
                                {
                                    text: 'Response'
                                }
                            ]}
                        ]
                    }
                ]
            },
            {
                xtype: 'buttongroup',
                columns: 2,
                defaults: buttonDefaults,
                items: [
                    {
                        text: 'Edit',
                        disabled: true,
                        action: 'editContentType',
                        iconCls: 'icon-edit-generic'
                    },
                    {
                        text: 'Delete',
                        disabled: true,
                        action: 'deleteContentType',
                        iconCls: 'icon-delete-user-24'
                    }
                ]
            },
            {
                xtype: 'buttongroup',
                columns: 1,
                defaults: buttonDefaults,
                items: [
                    {
                        text: 'View',
                        disabled: true,
                        action: 'viewContentType',
                        iconCls: 'icon-view-24'
                    }
                ]
            },
            {
                xtype: 'buttongroup',
                columns: 1,
                defaults: buttonDefaults,
                items: [
                    {
                        text: 'Re-index',
                        action: 'reindexContentTypes',
                        iconCls: 'icon-re-index-24'
                    }
                ]
            },
            {
                xtype: 'buttongroup',
                columns: 1,
                defaults: buttonDefaults,
                items: [
                    {
                        text: 'Export',
                        action: 'exportContentTypes',
                        iconCls: 'icon-export-24'
                    }
                ]
            }
        ];

        this.callParent(arguments);
    }

});
