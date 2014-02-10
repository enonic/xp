module app.imp {

    export class ModuleUploader extends api.dom.Element
        implements api.ui.dialog.UploadDialogUploaderEl
    {
        private installModuleRequest: api.module.InstallModuleRequest;

        private dropzone:api.dom.DivEl;
        private progress:api.ui.ProgressBar;

        constructor() {
            super(new api.dom.ElementProperties().setTagName("div").setClassName("image-uploader"));

            this.dropzone = new api.dom.DivEl("dropzone");
            // id needed for plupload to init, adding timestamp in case of multiple occurences on page
            this.dropzone.setId('module-uploader-dropzone-' + new Date().getTime());
            this.appendChild(this.dropzone);

            this.progress = new api.ui.ProgressBar();
            this.progress.setClass("progress");
            this.appendChild(this.progress);
        }

        afterRender() {
            super.afterRender();
            this.installModuleRequest = new api.module.InstallModuleRequest(this.dropzone);
            this.setProgressVisible(false);
        }

        stop() {
           if (this.installModuleRequest) {
                this.installModuleRequest.stop();
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
            this.installModuleRequest.done(fn);
        }

        onError(fn:(resp:api.rest.Response)=>void) {
            this.installModuleRequest.fail(fn);
        }
    }
}