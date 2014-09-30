module api.content.form.inputtype.image {

    import ImageUploadedEvent = api.ui.uploader.ImageUploadedEvent;
    import ImageUploadStartedEvent = api.ui.uploader.ImageUploadStartedEvent;
    import ImageUploadProgressEvent = api.ui.uploader.ImageUploadProgressEvent;

    export class ImageSelectorUploadDialog extends api.ui.dialog.ModalDialog {

        private dropzone: api.dom.DivEl;

        private uploader: any;

        private maximumOccurrences: number;

        private imageUploadedListeners: {(event: ImageUploadedEvent):void }[] = [];

        private imageUploadStartedListeners: { (event: ImageUploadStartedEvent):void }[] = [];

        private imageUploadProgressListeners: {(event: ImageUploadProgressEvent): void}[] = [];

        constructor() {
            super({
                title: new api.ui.dialog.ModalDialogHeader("Image uploader")
            });

            this.getEl().addClass("upload-dialog").addClass("image-uploader");

            var description = new api.dom.PEl();
            description.getEl().setInnerHtml("Images uploaded will be added as children to this content, you may move them later if desired");
            this.appendChildToContentPanel(description);

            this.dropzone = new api.dom.DivEl("dropzone");
            // id needed for plupload to init, adding timestamp in case of multiple occurences on page
            this.dropzone.setId('image-uploader-dropzone-' + new Date().getTime());
            this.appendChildToContentPanel(this.dropzone);

            this.setCancelAction(new UploadDialogCancelAction());
            this.getCancelAction().onExecuted((action: UploadDialogCancelAction) => {
                this.uploader.stop();
                this.close();
            });

            api.dom.Body.get().appendChild(this);

            this.uploader = this.initUploader();

            this.onRemoved((event) => this.uploader.destroy());

        }

        private initUploader() {

            if (!plupload) {
                throw new Error("ImageSelectorUploadDialog: plupload not found, check if it is included in page.");
            }

            var uploader = new plupload.Uploader({
                runtimes: 'gears,html5,flash,silverlight,browserplus',
                multi_selection: true,
                browse_button: this.dropzone.getId(),
                url: api.util.UriHelper.getRestUri("blob/upload"),
                multipart: true,
                drop_element: this.dropzone.getId(),
                flash_swf_url: api.util.UriHelper.getUri('common/js/fileupload/plupload/js/plupload.flash.swf'),
                silverlight_xap_url: api.util.UriHelper.getUri('common/js/fileupload/plupload/js/plupload.silverlight.xap'),
                filters: [
                    {title: 'Image files', extensions: 'jpg,gif,png'}
                ]
            });

            uploader.bind('FilesAdded', (up, files) => {
                if (this.maximumOccurrences > 0 && files.length > this.maximumOccurrences) {
                    files.splice(this.maximumOccurrences);
                }
            });

            uploader.bind('QueueChanged', (up) => {
                if (up.files.length > 0) {
                    up.start();

                    var uploadItems = up.files.map((file: any) => {
                        return new api.ui.uploader.UploadItemBuilder().setId(file.id).setName(file.name).setSize(file.size).build();
                    });
                    this.notifyUploadStarted(uploadItems);
                }
            });

            uploader.bind('UploadProgress', (up, file) => {
                var uploadItem = new api.ui.uploader.UploadItemBuilder().setId(file.id).setName(file.name).setSize(file.size).
                    setProgress(file.percent).build();
                this.notifyUploadProgress(uploadItem);
            });

            uploader.bind('FileUploaded', (up, file, response) => {
                if (response && response.status === 200) {
                    try {
                        var responseObj: any = JSON.parse(response.response);

                        if (responseObj.items && responseObj.items.length > 0) {
                            var uploadedFile = responseObj.items[0];

                            var uploadItem: api.ui.uploader.UploadItem = new api.ui.uploader.UploadItemBuilder().
                                setId(file.id).
                                setBlobKey(new api.blob.BlobKey(uploadedFile.id)).
                                setName(uploadedFile.name).
                                setMimeType(uploadedFile.mimeType).
                                setSize(uploadedFile.size).
                                build();

                            this.notifyImageUploaded(uploadItem);
                        }
                    } catch (e) {
                        console.warn("Failed to parse the response", response, e);
                    }
                }
            });

            uploader.bind('UploadComplete', (up, files) => {
                console.log('uploadComplete');
                if (this.uploader.files.length > 0) {
                    this.uploader.splice();
                }
            });

            uploader.init();

            return uploader;
        }

        setMaximumOccurrences(value: number) {
            this.maximumOccurrences = value;
        }

        open() {
            super.open();
        }

        onUploadStarted(listener: (event: ImageUploadStartedEvent) => void) {
            this.imageUploadStartedListeners.push(listener);
        }

        unUploadStarted(listener: (event: ImageUploadStartedEvent) => void) {
            this.imageUploadStartedListeners = this.imageUploadStartedListeners.filter((current) => (current != listener));
        }

        private notifyUploadStarted(uploadItems: api.ui.uploader.UploadItem[]) {
            var event = new ImageUploadStartedEvent(uploadItems);
            this.imageUploadStartedListeners.forEach((listener: (event: ImageUploadStartedEvent) => void) => listener(event));
        }

        onImageUploaded(listener: (event: ImageUploadedEvent)=>void) {
            this.imageUploadedListeners.push(listener);
        }

        unImageUploaded(listener: (event: ImageUploadedEvent)=>void) {
            this.imageUploadedListeners = this.imageUploadedListeners.filter((currentListener: (event: ImageUploadedEvent)=>void) => {
                return listener != currentListener;
            });
        }

        private notifyImageUploaded(uploadItem: api.ui.uploader.UploadItem) {
            var event = new ImageUploadedEvent(uploadItem);
            this.imageUploadedListeners.forEach((listener: (event: ImageUploadedEvent)=>void) => listener(event));
        }

        onUploadProgress(listener: (event: ImageUploadProgressEvent) => void) {
            this.imageUploadProgressListeners.push(listener);
        }

        unUploadProgress(listener: (event: ImageUploadProgressEvent) => void) {
            this.imageUploadProgressListeners = this.imageUploadProgressListeners.filter((current) => (current != listener));
        }

        private notifyUploadProgress(uploadItem: api.ui.uploader.UploadItem) {
            var event = new ImageUploadProgressEvent(uploadItem);
            this.imageUploadProgressListeners.forEach((listener: (event: ImageUploadProgressEvent) => void) => listener(event));
        }
    }

}