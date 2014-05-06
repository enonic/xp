declare var plupload;

Ext.define('Admin.plugin.fileupload.FileUploadGrid', {
    extend: 'Ext.grid.Panel',
    alias: 'widget.fileUploadGrid',
    width: 300,
    height: 150,

    initComponent: function () {
        if (!window['plupload']) {
            alert('FileUploadGrid requires Plupload!');
        }

        this.createStore();
        this.createToolbar();
        this.columns = [
            { header: 'Name', dataIndex: 'fileName', flex: 2 },
            { header: 'Size', dataIndex: 'fileSize', flex: 1 }
        ];

        this.selModel = <any> Ext.create('Ext.selection.RowModel', {
            mode: 'MULTI'
        });

        this.on('afterrender', function () {
            this.addBodyListeners();
        }, this);

        this.callParent(arguments);
    },

    createToolbar: function () {
        var grid = this;
        grid.tbar = <any> Ext.create('Ext.toolbar.Toolbar', {
            items: [
                {
                    xtype: 'button',
                    text: 'Browse...',
                    iconCls: 'icon-browse',
                    itemId: 'browseButton'
                },
                {
                    xtype: 'button',
                    text: 'Upload',
                    iconCls: 'icon-upload',
                    disabled: true,
                    itemId: 'uploadButton',
                    handler: function () {
                        Ext.Msg.alert('TODO', 'Upload');
                    }
                },
                {
                    xtype: 'button',
                    text: 'Remove',
                    iconCls: 'icon-remove',
                    disabled: true,
                    itemId: 'removeButton',
                    handler: function () {
                        grid.removeSelectedFiles(grid.getSelectionModel().getSelection());

                    }
                }
            ],
            listeners: {
                afterrender: {
                    fn: grid.initUploader,
                    scope: grid
                }
            }
        });

        grid.getStore().on('datachanged', this.onStoreDataChanged, grid);
        grid.on('selectionchange', this.onSelectionChange, grid);
    },

    initUploader: function () {
        var store = this.getStore();
        var browseButtonHtmlElementId = this.down('toolbar').down('button[itemId=browseButton]').getEl().id;
        var gridHtmlElementId = this.getEl().dom.id;

        this.uploader = new plupload.Uploader(
            {
                runtimes: 'html5,flash,silverlight',
                multi_selection: true,
                browse_button: browseButtonHtmlElementId,
                url: 'data/user/photo',
                multipart: true,
                drop_element: gridHtmlElementId,
                flash_swf_url: 'common/js/fileupload/plupload/js/plupload.flash.swf',
                silverlight_xap_url: 'common/js/fileupload/plupload/js/plupload.silverlight.xap'
            }
        );

        this.uploader.bind('FilesAdded', function (up, files) {
            var file = null;
            var i;
            for (i = 0; i < files.length; i += 1) {
                file = files[i];
                store.add({
                    'fileId': file.id,
                    'fileName': file.name,
                    'fileSize': file.size
                });
            }
        });

        this.uploader.bind('UploadProgress', function (up, file) {
        });

        this.uploader.bind('UploadComplete', function (up, files) {
        });

        this.uploader.bind('Init', function (up, params) {
        });

        this.uploader.init();
    },

    removeSelectedFiles: function (selected) {
        var store = this.getStore(), fileRecord = null;
        var i;
        for (i = 0; i < selected.length; i += 1) {
            fileRecord = selected[i];
            store.remove(fileRecord);

            this.removeFileFromUploaderQueue(fileRecord.data);
        }
    },

    removeFileFromUploaderQueue: function (recordData) {
        var uploaderFiles = this.uploader.files;
        var j;
        for (j = 0; j < uploaderFiles.length; j += 1) {
            if (uploaderFiles[j].id === recordData.fileId) {
                this.uploader.removeFile(uploaderFiles[j]);
            }
        }
    },

    createStore: function () {
        this.store = <any> Ext.create('Ext.data.Store', {
            fields: ['fileName', 'fileSize', 'fileId'],
            data: {'items': [
            ]},
            proxy: {
                type: 'memory',
                reader: {
                    type: 'json',
                    root: 'items'
                }
            }
        });
    },

    onStoreDataChanged: function (store, eOpts) {
        var uploadButton = this.down('toolbar').down('button[itemId=uploadButton]');
        uploadButton.setDisabled(store.data.items.length === 0);
    },

    onSelectionChange: function (model, selected, eOpts) {
        var removeButton = this.down('toolbar').down('button[itemId=removeButton]');
        removeButton.setDisabled(selected.length === 0);
    },

    addBodyListeners: function () {
        var bodyElement = Ext.getBody();
        var gridHtmlElement = this.getEl();

        function cancelEvent(event) {
            if (event.preventDefault) {
                event.preventDefault();
            }
            return false;
        }

        function addDragOverCls() {
            gridHtmlElement.addCls('admin-file-upload-drop-target');
        }

        function removeDragOverCls() {
            gridHtmlElement.removeCls('admin-file-upload-drop-target');
        }

        bodyElement.on('dragover', function (event) {
            addDragOverCls();
            cancelEvent(event);
        });
        bodyElement.on('dragenter', function (event) {
            addDragOverCls();
            cancelEvent(event);
        });
        bodyElement.on('dragleave', function (event) {
            removeDragOverCls();
            cancelEvent(event);
        });
        bodyElement.on('drop', function (event) {
            removeDragOverCls();
            cancelEvent(event);
        });
        bodyElement.on('dragend', function (event) {
            removeDragOverCls();
            cancelEvent(event);
        });
    }
});
