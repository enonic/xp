declare var plupload;

module api_app_wizard {

    export class FormIcon extends api_dom.ButtonEl {

        ext;

        private uploader;
        private img:api_dom.ImgEl;
        private progress:api_ui.ProgressBar;
        private tooltip:api_ui.Tooltip;

        /*
         * Icon widget with tooltip and upload possibility
         * @param iconUrl url to the icon to display
         * @param iconTitle text to display in tooltip
         * @param uploadUrl url to upload new icon to
         */
        constructor(public iconUrl:string, public iconTitle:string, public uploadUrl?:string) {
            super("FormIcon", "form-icon");
            var el = this.getEl();
            var me = this;

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

                var firstClickHandler = (event:Event) => {
                    if (!me.uploader) {
                        if (!plupload) {
                            console.log('FormIcon: plupload not found, check if it is included in page.');
                        } else {
                            me.uploader = me.initUploader(me.getId());
                            me.getHTMLElement().click();
                        }
                    }
                    me.getEl().removeEventListener("click", firstClickHandler);
                };

                this.getEl().addEventListener("click", firstClickHandler);
            }

            this.ext = this.initExt();
        }

        private initExt() {
            return new Ext.Component({
                contentEl: this.getHTMLElement()
            });
        }

        private initUploader(elId:string) {

            var uploader = new plupload.Uploader({
                runtimes: 'gears,html5,flash,silverlight,browserplus',
                multi_selection: false,
                browse_button: elId,
                url: this.uploadUrl,
                multipart: true,
                drop_element: elId,
                flash_swf_url: 'common/js/fileupload/plupload/js/plupload.flash.swf',
                silverlight_xap_url: 'common/js/fileupload/plupload/js/plupload.silverlight.xap',
                filters: [
                    {title: 'Image files', extensions: 'jpg,gif,png'}
                ]
            });

            uploader.bind('Init', function (up, params) {
                console.log('uploader init', up, params);
            });

            uploader.bind('FilesAdded', function (up, files) {
                console.log('uploader files added', up, files);
            });

            uploader.bind('QueueChanged', function (up) {
                console.log('uploader queue changed', up);

                up.start();
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

                var responseObj, uploadedResUrl;
                if (response && response.status === 200) {
                    responseObj = Ext.decode(response.response);
                    uploadedResUrl = (responseObj.items && responseObj.items.length > 0) ? 'rest/upload/' + responseObj.items[0].id
                        : 'resources/images/x-user-photo.png';
                    this.setSrc(uploadedResUrl);
                }

                this.progress.hide();

            });

            uploader.bind('UploadComplete', function (up, files) {
                console.log('uploader upload complete', up, files);
            });

            uploader.init();

            return uploader;
        }

        setSrc(src:string) {
            this.img.getEl().setSrc(src);
        }

    }

}
