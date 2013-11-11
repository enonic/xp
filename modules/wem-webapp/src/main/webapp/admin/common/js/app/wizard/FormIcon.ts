declare var plupload;

module api_app_wizard {

    export interface FormIconListener extends api_event.Listener {

        onUploadStarted?();

        onUploadFinished?(uploadId:string, mimeType:string, uploadName:string);
    }

    export class FormIcon extends api_dom.ButtonEl implements api_event.Observable {

        private uploader;
        private img:api_dom.ImgEl;
        private progress:api_ui.ProgressBar;
        private tooltip:api_ui.Tooltip;

        private listeners:FormIconListener[] = [];

        /*
         * Icon widget with tooltip and upload possibility
         * @param iconUrl url to the icon to display
         * @param iconTitle text to display in tooltip
         * @param uploadUrl url to upload new icon to
         */
        constructor(public iconUrl:string, public iconTitle:string, public uploadUrl?:string) {
            super("FormIcon", "form-icon");
            var el = this.getEl();

            this.tooltip = new api_ui.Tooltip(this, iconTitle, 0, 0);

            var img = this.img = new api_dom.ImgEl(this.iconUrl, "FormIcon");
            img.getEl().addEventListener("load", () => {
                if (img.isVisible()) {
                    this.tooltip.showFor(10000);
                }
            });
            el.appendChild(img.getHTMLElement());

            if (this.uploadUrl) {
                this.progress = new api_ui.ProgressBar();
                el.appendChild(this.progress.getHTMLElement());
            }

        }

        afterRender() {
            super.afterRender();
            if (!this.uploader && this.uploadUrl) {
                this.uploader = this.initUploader(this.getId());
            }
        }

        setSrc(src:string) {
            this.img.getEl().setSrc(src);
        }

        addListener(listener:FormIconListener) {
            this.listeners.push(listener);
        }

        removeListener(listener:FormIconListener) {
            this.listeners = this.listeners.filter(function (curr) {
                return curr != listener;
            });
        }

        private initUploader(elId:string) {

            if (!plupload) {
                console.log('FormIcon: plupload not found, check if it is included in page.');
            }

            var uploader = new plupload.Uploader({
                runtimes: 'gears,html5,flash,silverlight,browserplus',
                multi_selection: false,
                browse_button: elId,
                url: this.uploadUrl,
                multipart: true,
                drop_element: elId,
                flash_swf_url: api_util.getUri('common/js/fileupload/plupload/js/plupload.flash.swf'),
                silverlight_xap_url: api_util.getUri('common/js/fileupload/plupload/js/plupload.silverlight.xap'),
                filters: [
                    {title: 'Image files', extensions: 'jpg,gif,png'}
                ]
            });

            uploader.bind('Init', (up, params) => {
                console.log('uploader init', up, params);
            });

            uploader.bind('FilesAdded', (up, files) => {
                console.log('uploader files added', up, files);
            });

            uploader.bind('QueueChanged', (up) => {
                console.log('uploader queue changed', up);

                if (up.files.length > 0) {
                    up.start();
                    this.notifyUploadStarted();
                }
            });

            uploader.bind('UploadFile', (up, file) => {
                console.log('uploader upload file', up, file);

                this.progress.show();
            });

            uploader.bind('UploadProgress', (up, file) => {
                console.log('uploader upload progress', up, file);

                this.progress.setValue(file.percent);
            });

            uploader.bind('FileUploaded', (up, file, response) => {
                console.log('uploader file uploaded', up, file, response);

                var responseObj;
                if (response && response.status === 200) {

                    responseObj = Ext.decode(response.response);
                    if (responseObj.items && responseObj.items.length > 0) {
                        var file = responseObj.items[0];
                        this.notifyUploadFinished(file.id, file.mimeType, file.name);
                    }
                }

                this.progress.hide();

            });

            uploader.bind('UploadComplete', (up, files) => {
                console.log('uploader upload complete', up, files);

                up.total.reset();
                var uploadedFiles = up.splice();

            });

            uploader.init();

            return uploader;
        }

        private notifyUploadStarted() {
            this.listeners.forEach((listener:FormIconListener) => {
                if (listener.onUploadStarted) {
                    listener.onUploadStarted();
                }
            });
        }

        private notifyUploadFinished(uploadId:string, mimeType:string, uploadName:string) {
            this.listeners.forEach((listener:FormIconListener) => {
                if (listener.onUploadFinished) {
                    listener.onUploadFinished(uploadId, mimeType, uploadName);
                }
            });
        }

    }

}
