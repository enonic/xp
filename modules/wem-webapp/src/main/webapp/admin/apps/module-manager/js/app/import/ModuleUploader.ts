module app_import {

    export interface ModuleUploaderConfig {

        multiSelection?: boolean;

        buttonsVisible?: boolean;

    }

    export class ModuleUploader extends api_dom.FormInputEl {

        private name:string;
        private value:string;
        private uploader;

        private dropzone:api_dom.DivEl;
        private progress:api_ui.ProgressBar;
        private cancelBtn:api_ui.Button;
        private image:api_dom.ImgEl;
        private resetBtn:api_ui.Button;

        private multiSelection:boolean;
        private buttonsVisible:boolean;

        constructor(name:string, config:ModuleUploaderConfig = {}) {
            super("div", "ModuleUploader", "image-uploader");
            this.name = name;
            this.multiSelection = (config.multiSelection == undefined) ? false : config.multiSelection;
            this.buttonsVisible = (config.buttonsVisible == undefined) ? true : config.buttonsVisible;

            this.dropzone = new api_dom.DivEl("DropZone", "dropzone");
            this.refreshDropzoneLabel();
            this.appendChild(this.dropzone);

            this.progress = new api_ui.ProgressBar();
            this.progress.setClass("progress");
            this.appendChild(this.progress);

            this.image = new api_dom.ImgEl();
            this.appendChild(this.image);

            this.cancelBtn = new api_ui.Button("Cancel");
            this.cancelBtn.setVisible(this.buttonsVisible);
            this.cancelBtn.setClickListener(() => {
                this.stop();
                this.reset();
            });
            this.appendChild(this.cancelBtn);

            this.resetBtn = new api_ui.Button("Reset");
            this.resetBtn.setVisible(this.buttonsVisible);
            this.resetBtn.setClickListener(() => {
                this.reset();
            });
            this.appendChild(this.resetBtn);
        }

        afterRender() {
            super.afterRender();
            this.uploader = new api_module.InstallModuleRequest(this.dropzone );
            this.setProgressVisible(false);
            this.setImageVisible(false);
        }

        getName():string {
            return this.name;
        }

        getValue():string {
            return this.value;
        }

        setValue(value:string) {
            this.value = value;
            var src = api_util.getAdminUri(value ? 'rest/upload/' + value : 'common/images/x-user-photo.png');
            this.image.getEl().setSrc(src);
        }


        stop() {
           /* if (this.uploader) {
                this.uploader.stop();
            }*/
        }

        reset() {
            this.setProgressVisible(false);
            this.setImageVisible(false);
            this.setDropzoneVisible(true);
            this.setValue(undefined);
        }

        private refreshDropzoneLabel() {
            var label = "Drop files here or click to select";
            this.dropzone.getEl().setInnerHtml(label);
        }

        private setDropzoneVisible(visible:boolean) {
            this.dropzone.setVisible(visible);
        }

        private setProgressVisible(visible:boolean) {
            this.progress.setVisible(visible);
            this.cancelBtn.setVisible(visible && this.buttonsVisible);
        }

        private setImageVisible(visible:boolean) {
            this.image.setVisible(visible);
            this.resetBtn.setVisible(visible && this.buttonsVisible);
        }

        onFinishUpload(fn:(resp:api_module.InstallModuleResponse)=>void) {
            this.uploader.promise().done(fn);
        }

        onError(fn:(resp:api_rest.JsonResponse)=>void) {
            this.uploader.promise().fail(fn);
        }
    }
}