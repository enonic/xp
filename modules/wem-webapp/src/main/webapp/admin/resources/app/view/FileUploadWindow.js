Ext.define('Admin.view.FileUploadWindow', {
    extend: 'Admin.view.BaseDialogWindow',
    alias: 'widget.fileUploadWindow',

    require: [
        'Admin.lib.UriHelper'
    ],

    dialogTitle: undefined,
    dialogSubTitle: undefined,
    dialogInfoTpl: undefined,

    width: 800,
    height: 560,

    layout: {
        type: 'vbox',
        align: 'stretch'
    },
    defaultType: 'container',


    initComponent: function () {
        var me = this;

        this.items = [
            me.header(
                'Image uploader',
                'Images uploaded will be embedded directly in this content, you may move them to the library later if desired'
            ),
            me.plupload(),
            me.buttonRow({
                xtype: 'button',
                text: 'Cancel',
                ui: 'grey',
                handler: function (btn, evt) {
                    if (me.uploader) {
                        me.uploader.stop();
                    }
                    me.close();
                }
            })
        ];

        this.callParent(arguments);

        this.addEvents('uploadcomplete');
    },

    plupload: function () {
        return {
            xtype: 'container',
            flex: 1,
            layout: 'card',
            listeners: {
                afterrender: this.onPluploadAfterrender,
                scope: this
            },
            items: [
                {
                    itemId: 'uploadForm',
                    cls: 'admin-upload-form',
                    xtype: 'container',
                    layout: {
                        type: 'vbox',
                        align: 'stretch'
                    },
                    items: [
                        {
                            xtype: 'textfield',
                            emptyText: 'Paste URL to image here',
                            margin: '0 0 20 0',
                            enableKeyEvents: true
                        },
                        {
                            flex: 1,
                            xtype: 'component',
                            itemId: 'dropZone',
                            cls: 'admin-drop-zone',
                            styleHtmlContent: true
                        }
                    ]
                },
                {
                    itemId: 'progressForm',
                    cls: 'admin-upload-progress',
                    xtype: 'component',
                    tpl: '<h4>{percent}% complete</h4>' +
                         '<div class="admin-progress-bar"><div class="admin-progress" style="width: {percent}%;"></div></div>' +
                         '<p>{[values.uploaded + values.failed + 1]} of {[values.uploaded + values.queued + values.failed]}</p>',
                    data: {
                        percent: 40,
                        uploaded: 2,
                        queued: 2,
                        failed: 1
                    }
                }
            ]
        };
    },

    onPluploadAfterrender: function (container) {
        var me = this;
        var dropZoneEl = container.down('#dropZone').el;

        this.uploader = new plupload.Uploader({
            runtimes: 'gears,html5,flash',
            browse_button: dropZoneEl.dom.id,
            url: Admin.lib.UriHelper.getAbsoluteUri('admin/rest/upload'),
            autoStart: false,
            max_file_size: '100mb',
            drop_element: dropZoneEl.dom.id,
            statusQueuedText: 'Ready to upload',
            statusUploadingText: 'Uploading ({0}%)',
            statusFailedText: '<span style="color: red">Error</span>',
            statusDoneText: '<span style="color: green">Complete</span>',

            statusInvalidSizeText: 'File too large',
            statusInvalidExtensionText: 'Invalid file type'
        });

        this.uploader.bind('Init', function (up) {
            var hint = '';
            if (!!up.features.dragdrop) {
                hint = '<h4>Drop files here or click to select</h4>'
            } else {
                hint = '<h4>Click to select</h4>'
            }
            dropZoneEl.update(hint);
        });

        this.uploader.bind('QueueChanged', function (up) {

            var activeItem;
            if (up.files.length > 0) {

                activeItem = 'progressForm';
                up.start();

            } else {

                activeItem = 'uploadForm';
                up.stop();
            }
            container.getLayout().setActiveItem(activeItem);
        });

        this.uploader.bind('UploadProgress', function (up, file) {

            container.down('#progressForm').update(up.total);
        });

        this.uploader.bind('FileUploaded', function (up, file, response) {

            // save server response to file
            if (response && response.response) {
                var json = Ext.JSON.decode(response.response);
                if (json.success && json.items && json.items.length == 1) {
                    file.response = json.items[0];
                }
            }

        });

        this.uploader.bind('UploadComplete', function (up, files) {
            // resets counters
            up.total.reset();
            // remove uploaded files
            var uploaded = up.splice();

            container.getLayout().setActiveItem('uploadForm');
            me.fireEvent('uploadcomplete', me, uploaded);
        });

        this.uploader.init();
    }
});

