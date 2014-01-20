module app.imp {

    export class ModuleUploader extends api.dom.Element
        implements api.ui.dialog.UploadDialogUploaderEl
    {
        private uploader;

        private dropzone:api.dom.DivEl;
        private progress:api.ui.ProgressBar;

        constructor() {
            super(new api.dom.ElementProperties().setTagName("div").setClassName("image-uploader"));

            this.dropzone = new api.dom.DivEl("dropzone");
            this.dropzone.getEl().setInnerHtml("Drop files here or click to select");
            this.appendChild(this.dropzone);

            this.progress = new api.ui.ProgressBar();
            this.progress.setClass("progress");
            this.appendChild(this.progress);
        }

        afterRender() {
            super.afterRender();
            this.uploader = new api.module.InstallModuleRequest(this.dropzone);
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

        onFinishUpload(fn:(resp:api.module.InstallModuleResponse)=>void) {
            this.uploader.done(fn);
        }

        onError(fn:(resp:api.rest.Response)=>void) {
            this.uploader.fail(fn);
        }
    }
}