module app_import {

    export class ModuleUploader extends api_dom.Element
        implements api_ui_dialog.UploadDialogUploaderEl
    {
        private uploader;

        private dropzone:api_dom.DivEl;
        private progress:api_ui.ProgressBar;

        constructor() {
            super("div", "ModuleUploader", "image-uploader");

            this.dropzone = new api_dom.DivEl("DropZone", "dropzone");
            this.dropzone.getEl().setInnerHtml("Drop files here or click to select");
            this.appendChild(this.dropzone);

            this.progress = new api_ui.ProgressBar();
            this.progress.setClass("progress");
            this.appendChild(this.progress);
        }

        afterRender() {
            super.afterRender();
            this.uploader = new api_module.InstallModuleRequest(this.dropzone);
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

        onFinishUpload(fn:(resp:api_module.InstallModuleResponse)=>void) {
            this.uploader.done(fn);
        }

        onError(fn:(resp:api_rest.JsonResponse<any>)=>void) {
            this.uploader.fail(fn);
        }
    }
}