module app.imp {

    export class TemplateUploader extends api.dom.Element
        implements api.ui.dialog.UploadDialogUploaderEl
    {
        private uploader:api.content.site.template.InstallSiteTemplateRequest;

        private dropzone:api.dom.DivEl;
        private progress:api.ui.ProgressBar;

        constructor() {
            super(new api.dom.ElementProperties().setTagName("div").setClassName("image-uploader"));

            this.dropzone = new api.dom.DivEl("dropzone");
            // id needed for plupload to init, adding timestamp in case of multiple occurences on page
            this.dropzone.setId('template-uploader-dropzone-' + new Date().getTime());
            this.appendChild(this.dropzone);

            this.progress = new api.ui.ProgressBar();
            this.progress.setClass("progress");
            this.appendChild(this.progress);

            this.onRendered((event) => {
                console.log("TemplateUploader rendered, creating plupload");
                this.uploader = new api.content.site.template.InstallSiteTemplateRequest(this.dropzone);
                this.setProgressVisible(false);
            });
            this.onRemoved((event) => {
                console.log("TemplateUploader removed, destroying plupload");
                this.uploader.destroy();
            })
        }

        stop() {
            if (this.uploader) {
                this.uploader.stop();
            }
        }

        reset() {
            this.setProgressVisible(false);
            this.setDropzoneVisible(true);
        }

        private setDropzoneVisible(visible:boolean) {
            this.dropzone.setVisible(visible);
        }

        private setProgressVisible(visible:boolean) {
            this.progress.setVisible(visible);
        }

        onFinishUpload(fn:(resp:api.content.site.template.InstallSiteTemplateResponse)=>void) {
            this.uploader.done(fn);
        }

        onError(fn:(resp:api.rest.RequestError)=>void) {
            this.uploader.fail(fn);
        }
    }

}