declare var plupload;

module api.app.wizard {

    export class FormIcon extends api.dom.ButtonEl {

        private uploader;
        private img: api.dom.ImgEl;
        private progress: api.ui.ProgressBar;

        private uploadStartedListeners: {():void}[] = [];

        private uploadFinishedListeners: {(event: UploadFinishedEvent):void}[] = [];

        /*
         * Icon widget with tooltip and upload possibility
         * @param iconUrl url to the icon to display
         * @param iconTitle text to display in tooltip
         * @param uploadUrl url to upload new icon to
         */
        constructor(public iconUrl: string, public iconTitle: string, public uploadUrl?: string) {
            super("form-icon");
            var el = this.getEl();

            this.img = new api.dom.ImgEl(this.iconUrl);

            el.appendChild(this.img.getHTMLElement());

            if (this.uploadUrl) {
                this.progress = new api.ui.ProgressBar();
                el.appendChild(this.progress.getHTMLElement());
            }

            this.onRendered((event) => {
                if (!this.uploader && this.uploadUrl) {
                    this.uploader = this.initUploader(this.getId());
                }
            });

            this.onRemoved((event) => {
                if (this.uploader) {
                    this.uploader.destroy();
                }
            });
        }

        setSrc(src: string) {
            this.img.setSrc(src);
        }

        private initUploader(elId: string) {

            var uploader = new plupload.Uploader({
                runtimes: 'gears,html5,flash,silverlight,browserplus',
                multi_selection: false,
                browse_button: elId,
                url: this.uploadUrl,
                multipart: true,
                drop_element: elId,
                flash_swf_url: api.util.UriHelper.getUri('common/js/fileupload/plupload/js/plupload.flash.swf'),
                silverlight_xap_url: api.util.UriHelper.getUri('common/js/fileupload/plupload/js/plupload.silverlight.xap'),
                filters: [
                    {title: 'Image files', extensions: 'jpg,gif,png'}
                ]
            });

            uploader.bind('Init', (up, params) => {
                //console.log('uploader init', up, params);
            });

            uploader.bind('FilesAdded', (up, files) => {
                //console.log('uploader files added', up, files);
            });

            uploader.bind('QueueChanged', (up) => {
                //console.log('uploader queue changed', up);

                if (up.files.length > 0) {
                    up.start();
                    this.notifyUploadStarted();
                }
            });

            uploader.bind('UploadFile', (up, file) => {
                //console.log('uploader upload file', up, file);

                this.progress.show();
            });

            uploader.bind('UploadProgress', (up, file) => {
                //console.log('uploader upload progress', up, file);

                this.progress.setValue(file.percent);
            });

            uploader.bind('FileUploaded', (up, file: api.ui.uploader.PluploadFile, response) => {
                console.log('uploader file uploaded', up, file, response);

                var responseObj;
                if (response && response.status === 200) {

                    responseObj = JSON.parse(response.response);
                    if (responseObj.items && responseObj.items.length > 0) {
                        var uploadedFile = responseObj.items[0];

                        var uploadItem = new api.ui.uploader.UploadItem<any>(file);
                        this.notifyUploadFinished(uploadItem);
                    }
                }

                this.progress.hide();

            });

            uploader.bind('UploadComplete', (up, files) => {
                //console.log('uploader upload complete', up, files);

                up.total.reset();
                var uploadedFiles = up.splice();

            });

            uploader.init();

            return uploader;
        }

        onUploadStarted(listener: ()=>void) {
            this.uploadStartedListeners.push(listener);
        }

        onUploadFinished(listener: (event: UploadFinishedEvent)=>void) {
            this.uploadFinishedListeners.push(listener);
        }

        unUploadStarted(listener: ()=>void) {
            this.uploadStartedListeners = this.uploadStartedListeners.filter((currentListener: ()=>void) => {
                return currentListener != listener;
            });
        }

        unUploadFinished(listener: (event: UploadFinishedEvent)=>void) {
            this.uploadFinishedListeners = this.uploadFinishedListeners.filter((currentListener: (event: UploadFinishedEvent)=>void)=> {
                return currentListener != listener;
            });
        }

        private notifyUploadStarted() {
            this.uploadStartedListeners.forEach((listener: ()=>void) => {
                listener.call(this);
            });
        }

        private notifyUploadFinished(uploadItem: api.ui.uploader.UploadItem<any>) {
            this.uploadFinishedListeners.forEach((listener: (event: UploadFinishedEvent)=>void) => {
                listener.call(this, new UploadFinishedEvent(uploadItem));
            });
        }

    }

}
