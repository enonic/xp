module app_import {

    export class TemplateUploader extends api_dom.Element
        implements api_ui_dialog.UploadDialogUploaderEl
    {
        private uploader:api_content_site_template.InstallSiteTemplateRequest;

        private dropzone:api_dom.DivEl;
        private progress:api_ui.ProgressBar;

        constructor() {
            super("div", "TemplateUploader", "image-uploader");

            this.dropzone = new api_dom.DivEl("DropZone", "dropzone");
            this.dropzone.getEl().setInnerHtml("Drop files here or click to select");
            this.appendChild(this.dropzone);

            this.progress = new api_ui.ProgressBar();
            this.progress.setClass("progress");
            this.appendChild(this.progress);
        }

        afterRender() {
            super.afterRender();
            this.uploader = new api_content_site_template.InstallSiteTemplateRequest(this.dropzone);
            this.setProgressVisible(false);
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

        onFinishUpload(fn:(resp:api_content_site_template.InstallSiteTemplateResponse)=>void) {
            this.uploader.done(fn);
        }

        onError(fn:(resp:api_rest.Response)=>void) {
            this.uploader.fail(fn);
        }
    }

}