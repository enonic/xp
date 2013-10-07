Ext.define('Admin.plugin.fileupload.PhotoUploadButton', {
    extend: 'Ext.Component',
    alias: 'widget.photoUploadButton',
    width: 132,
    height: 132,
    uploadUrl: 'rest/upload',
    progressBarHeight: 8,

    tpl: new Ext.XTemplate('<div id="{id}" title="{title}" class="admin-image-upload-button-container" style="width:{width}px;height:{height}px; margin: 0">' +
                           '<img src="{photoUrl}" class="admin-image-upload-button-image" style="width:{width - 2}px;height:{height - 2}px"/>' +
                           '<div class="admin-image-upload-button-progress-bar-container" style="width:{width - 3}px">' +
                           '<div class="admin-image-upload-button-progress-bar" style="height:{progressBarHeight}px"><!-- --></div>' +
                           '</div>' +
                           '</div>'),

    initComponent: function () {
        if (!window['plupload']) {
            alert('ImageUploadButton requires Plupload!');
        }
        var me = this;
        this.addEvents("fileuploaded", "dirtychange");
        this.addListener("fileuploaded", function () {
            me.fireEvent('dirtychange', me, true);
        });
    },

    onRender: function () {
        this.callParent(arguments);

        var buttonElementId = Ext.id(null, 'image-upload-button-');
        var width = this.width;
        var height = this.height;
        var title = this.title;
        var progressBarHeight = this.progressBarHeight;
        var photoUrl = this.photoUrl || 'resources/images/x-user-photo.png';
        this.update({
            id: buttonElementId,
            width: width,
            height: height,
            progressBarHeight: progressBarHeight,
            photoUrl: photoUrl,
            title: title
        });

        this.buttonElementId = buttonElementId;
    },

    afterRender: function () {
        this.initUploader();
        this.addBodyMouseEventListeners();
    },

    initUploader: function () {
        var uploadButton = this;
        var buttonId = this.getId();

        var uploader = new plupload.Uploader({
            runtimes: 'html5,flash,silverlight',
            multi_selection: false,
            browse_button: buttonId,
            url: this.uploadUrl,
            multipart: true,
            drop_element: buttonId,
            flash_swf_url: 'common/js/fileupload/plupload/js/plupload.flash.swf',
            silverlight_xap_url: 'common/js/fileupload/plupload/js/plupload.silverlight.xap',
            filters: [
                {title: 'Image files', extensions: 'jpg,gif,png'}
            ]
        });

        uploader.bind('Init', function (up, params) {
        });

        uploader.bind('FilesAdded', function (up, files) {
        });

        uploader.bind('QueueChanged', function (up) {
            // TODO: Check files length. Only one should be allowed
            // TODO: Check file extension. Only jpeg,jpg,png,gif,tiff,bmp is allowed.
            up.start();
        });

        uploader.bind('UploadFile', function (up, file) {
            uploadButton.showProgressBar();
        });

        uploader.bind('UploadProgress', function (up, file) {
            uploadButton.updateProgressBar(file);
        });

        uploader.bind('FileUploaded', function (up, file, response) {
            var responseObj, uploadedResUrl;
            if (response && response.status === 200) {
                responseObj = Ext.decode(response.response);
                uploadedResUrl = (responseObj.items && responseObj.items.length > 0) ? 'rest/upload/' + responseObj.items[0].id
                    : 'resources/images/x-user-photo.png';
                uploadButton.updateImage(uploadedResUrl);
            }
            uploadButton.hideProgressBar();

            uploadButton.fireEvent('fileuploaded', this, responseObj);
        });

        uploader.bind('UploadComplete', function (up, files) {

        });

        setTimeout(function () {
            uploader.init();
        }, 1);
    },

    updateImage: function (src) {
        this.getImageElement().src = src;
    },

    showProgressBar: function () {
        this.getProgressBarContainerElement().style.opacity = 1;
        this.getProgressBarContainerElement().style.visibility = 'visible';
    },

    updateProgressBar: function (file) {
        var progressBar = this.getProgressBarElement();
        var percent = file.percent || 0;
        progressBar.style.width = percent + '%';
    },

    hideProgressBar: function () {
        this.getProgressBarElement().style.width = '0';
        this.getProgressBarContainerElement().style.visibility = 'hidden';
    },

    getImageElement: function () {
        return Ext.DomQuery.select('#' + this.buttonElementId + ' .admin-image-upload-button-image')[0];
    },

    getProgressBarContainerElement: function () {
        return Ext.DomQuery.select('#' + this.buttonElementId +
                                   ' .admin-image-upload-button-progress-bar-container')[0];
    },

    getProgressBarElement: function () {
        return Ext.DomQuery.select('#' + this.buttonElementId + ' .admin-image-upload-button-progress-bar')[0];
    },

    addBodyMouseEventListeners: function () {
        var me = this;
        var bodyElement = Ext.getBody();
        var dropTarget = Ext.get(this.buttonElementId);
        var border = Ext.get(this.buttonElementId + '-over-border');

        function cancelEvent(event) {
            if (event.preventDefault) {
                event.preventDefault();
            }
            return false;
        }

        function highlightDropTarget() {
            dropTarget.addCls('admin-file-upload-drop-target');
        }

        function removeHighlightFromDropTarget() {
            dropTarget.dom.className = dropTarget.dom.className.replace(/ admin-file-upload-drop-target/, '');
        }

        dropTarget.on('mouseenter', function (event) {
            highlightDropTarget();
            me.fireEvent('mouseenter');
            cancelEvent(event);
        });
        dropTarget.on('mouseleave', function (event) {
            removeHighlightFromDropTarget();
            me.fireEvent('mouseleave');
            cancelEvent(event);
        });
        dropTarget.on('dragenter', function (event) {
            cancelEvent(event);
        });

        bodyElement.on('dragover', function (event) {
            highlightDropTarget();
            cancelEvent(event);
        });
        bodyElement.on('dragenter', function (event) {
            highlightDropTarget();
            cancelEvent(event);
        });
        bodyElement.on('dragleave', function (event) {
            removeHighlightFromDropTarget();
            cancelEvent(event);
        });
        bodyElement.on('drop', function (event) {
            removeHighlightFromDropTarget();
            cancelEvent(event);
        });
        bodyElement.on('dragend', function (event) {
            removeHighlightFromDropTarget();
            cancelEvent(event);
        });
    }

});
