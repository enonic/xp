Ext.define('Admin.view.contentManager.wizard.form.input.Image', {
    extend: 'Admin.view.contentManager.wizard.form.input.Base',
    alias: 'widget.Image',

    cls: 'admin-image-upload',
    width: 500,
    minHeight: 250,
    flex: 1,
    layout: 'card',
    imageAttachmentName: null,
    fileUploaded: null,

    initComponent: function () {
        var me = this;

        me.items = [
            me.createUploadForm(),
            me.createProgressForm(),
            me.createLoadingForm(),
            me.createImageForm()
        ];

        me.listeners = {
            afterrender: me.setupUploader,
            scope: me
        };

        me.callParent(arguments);


        if (me.value && me.value.length > 0) {
            me.on('beforerender', function () {
                me.setValue(me.value);
            });
        }
    },

    createUploadForm: function () {
        return {
            xtype: 'container',
            itemId: 'uploadForm',
            cls: 'admin-upload-input',
            height: 250,
            layout: {
                type: 'vbox',
                align: 'stretch'
            },
            items: [
                {
                    xtype: 'textfield',
                    emptyText: 'Paste URL to image here',
                    margin: '0 0 5 0',
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
        };
    },

    createProgressForm: function () {
        return {
            xtype: 'container',
            itemId: 'progressForm',
            cls: 'admin-progress-form',
            height: 250,
            layout: {
                type: 'vbox',
                align: 'center',
                pack: 'center'
            },
            items: [
                {
                    itemId: 'progressBar',
                    width: 500,
                    tpl: '<h3>{percent}% complete</h3>' +
                         '<div class="admin-progress-bar"><div class="admin-progress" style="width: {percent}%;"></div></div>',
                    data: {
                        percent: 0
                    }
                },
                {
                    xtype: 'button',
                    text: 'Cancel',
                    action: 'cancel',
                    cls: 'icon-button',
                    scale: 'medium',
                    width: 125,
                    height: 40,
                    margin: 15,
                    style: {
                        borderColor: '#929292',
                        backgroundColor: '#929292'
                    }
                }
            ]
        };
    },

    createLoadingForm: function () {
        return {
            itemId: 'loadingForm',
            width: '100%',
            height: '100%',
            maxWidth: 500,
            maxHeight: 500,
            html: '<div class="admin-loading-form"><span class="loader"></span></div>'
        };
    },

    createImageForm: function () {
        var me = this;

        return {
            itemId: 'imageForm',
            xtype: 'container',
            width: 500,
            items: [
                {
                    itemId: 'image',
                    xtype: 'component',
                    tpl: [
                        '<tpl for=".">',
                        '<div class="admin-image-form">',
                        '<img src="{imageUrl}?size=500" alt="test image"/>',
                        '</div>',
                        '</tpl>'
                    ]
                },
                {
                    xtype: 'container',
                    layout: {
                        type: 'hbox',
                        align: 'top',
                        pack: 'end'
                    },
                    items: [
                        {
                            xtype: 'button',
                            text: 'Edit',
                            cls: 'icon-button',
                            scale: 'medium',
                            width: 130,
                            height: 30,
                            margin: 10,
                            listeners: {
                                click: {
                                    fn: me.removeUploadedImage,
                                    scope: me
                                }
                            }
                        },
                        {
                            xtype: 'button',
                            text: 'Remove',
                            cls: 'icon-button',
                            scale: 'medium',
                            width: 130,
                            height: 30,
                            margin: '10 0',
                            style: {
                                borderColor: '#7A7A7A',
                                backgroundColor: '#7A7A7A'
                            },
                            listeners: {
                                click: {
                                    fn: me.removeUploadedImage,
                                    scope: me
                                }
                            }
                        }
                    ]
                }
            ]
        };
    },

    setupUploader: function (container) {
        var me = this,
            dropZoneEl = container.down('#dropZone').el,
            cancelBtn = container.down('[action=cancel]');

        me.uploader = new plupload.Uploader({
            runtimes: 'gears,html5,flash',
            browse_button: dropZoneEl.dom.id,
            url: Admin.lib.UriHelper.getAbsoluteUri('admin/rest/upload'),
            multi_selection: false,
            max_file_size: '100mb',
            drop_element: dropZoneEl.dom.id
        });

        me.uploader.bind('Init', function (up) {
            dropZoneEl.update('<h4>' + (!!up.features.dragdrop ? 'Drop files here or click to select' : 'Click to select') + '</h4>');
        });

        me.uploader.bind('QueueChanged', function (up) {
            if (up.files.length == 1) {
                up.start();
            }
        });

        me.uploader.bind('UploadFile', function (up, file) {
            container.getLayout().setActiveItem('progressForm');
        });

        me.uploader.bind('UploadProgress', function (up, file) {
            container.down('#progressBar').update(up.total);
        });

        cancelBtn.on('click', function (up) {
            up.stop();

            up.total.reset();
            up.splice();

            container.getLayout().setActiveItem('uploadForm');
        }, me);

        me.uploader.bind('FileUploaded', function (up, file, response) {
            // save server response to file
            if (response && response.response) {
                var json = Ext.JSON.decode(response.response);
                if (json.success && json.items && json.items.length == 1) {
                    file.response = json.items[0];
                }
            }
        });

        me.uploader.bind('UploadComplete', function (up, files) {
            container.getLayout().setActiveItem('loadingForm');

            up.total.reset();
            var uploaded = up.splice();

            me.loadFile(uploaded[0]);
        });

        me.uploader.init();
    },

    loadFile: function (file) {
        var me = this;
        me.createImageContent(file.response, function (contentId, attachments) {
            var attachmentName = attachments[0].name;
            var uploadedResUrl = Admin.lib.UriHelper.getAbsoluteUri('admin/rest/attachment/') + contentId + '/' + attachmentName;
            me.hideLoaderOnImageLoad(uploadedResUrl);
        });
    },

    /**
     * @private
     */
    createImageContent: function (file, callback) {
        var me = this;
        this.fileUploaded = file;
        this.imageAttachmentName = null;
        me.remoteCreateImageContent(file.name, file.mimeType, file.name, function (contentId) {
            me.remoteCreateAttachment(contentId, file.id, callback);
        });
    },

    /**
     * @private
     */
    remoteCreateAttachment: function (contentId, fileUploadId, callback) {
        var createAttachmentCommand = {
            contentId: contentId,
            uploadFileId: fileUploadId
        };
        Admin.lib.RemoteService.attachment_create(createAttachmentCommand, function (response) {
            if (response && response.success) {
                callback(response.contentId, response.attachments);
            } else {
                Ext.Msg.alert("Error", response ? response.error : "Unable to create attachment.");
            }
        });
    },

    /**
     * @private
     */
    remoteCreateImageContent: function (displayName, mimeType, imageAttachmentName, callback) {
        var createContentCommand = {
            "contentData": {
                "mimeType": mimeType,
                "image": imageAttachmentName
            },
            "qualifiedContentTypeName": 'system:image',
            "displayName": displayName,
            "temporary": true
        };

        Admin.lib.RemoteService.content_createOrUpdate(createContentCommand, function (response) {
            if (response && response.success) {
                callback(response.contentId);
            } else {
                Ext.Msg.alert("Error", response ? response.error : "Unable to create image content.");
            }
        });
    },

    /**
     * @private
     */
    hideLoaderOnImageLoad: function (imageUrl) {
        var me = this,
            imageForm = me.getLayout().getLayoutItems()[3];

        imageForm.down('#image').update({imageUrl: imageUrl});

        Ext.fly(imageForm.getEl().dom).down('img').on('load', function (event, target, opts) {
            me.getLayout().setActiveItem('imageForm');
        });
    },

    removeUploadedImage: function () {
        this.getLayout().setActiveItem('uploadForm');
    },

    getValue: function () {
        var fileUploaded = this.fileUploaded;
        if (!fileUploaded && !this.imageAttachmentName) {
            return null;
        } else if (fileUploaded) {
            return {
                path: this.name + '[0]',
                value: [fileUploaded.id, fileUploaded.name].join(',')
            }
        } else {
            return {
                path: this.name + '[0]',
                value: this.imageAttachmentName
            };
        }
    },

    setValue: function (value) {
        var me = this;
        this.imageAttachmentName = value[0].value;
        var getContentCommand = {
            contentIds: [me.contentId]
        };
        me.getLayout().setActiveItem('loadingForm');

        var contentImageUrl = Admin.lib.UriHelper.getAbsoluteUri('admin/rest/attachment/') + me.contentId + '/' + this.imageAttachmentName;
        Admin.lib.RemoteService.content_get(getContentCommand, function (getContentResponse) {
            if (getContentResponse && getContentResponse.success) {
                console.log(getContentResponse);
                var contentData = getContentResponse.content[0];
                var contentModel = new Admin.model.contentManager.ContentModel(contentData);
                me.hideLoaderOnImageLoad(contentImageUrl);
            }
        });
    }
});