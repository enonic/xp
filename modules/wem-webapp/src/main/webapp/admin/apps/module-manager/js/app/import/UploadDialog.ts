module app_import {

    export class UploadDialog extends api_ui_dialog.ModalDialog {

        private uploader:ModuleUploader;

        private listeners:UploadDialogListener[] = [];

        constructor() {
            super({
                      title: "Module Importer",
                      width: 800,
                      height: 520,
                      idPrefix: "UploadDialog"
                  });

            this.getEl().addClass("image-selector-upload-dialog");

            var description = new api_dom.PEl();
            description.getEl().setInnerHtml("Modules will be imported in application");
            this.appendChildToContentPanel(description);

            var uploaderConfig = {
                multiSelection: true,
                buttonsVisible: false,
                imageVisible: false
            };
            this.uploader = new ModuleUploader("module-upload-dialog", uploaderConfig);
            this.appendChildToContentPanel(this.uploader);

            this.setCancelAction(new UploadDialogCancelAction());
            this.getCancelAction().addExecutionListener((action:UploadDialogCancelAction) => {
                this.uploader.stop();
                this.uploader.reset();
                this.close();
            });

            api_dom.Body.get().appendChild(this);

        }

        open() {
            this.uploader.reset();
            super.open();
        }

        onFinishUpload(fn:(resp:api_module.InstallModuleResponse)=>void):UploadDialog {
            this.uploader.onFinishUpload(fn);
            return this;
        }

        onError(fn:(resp:api_rest.JsonResponse)=>void):UploadDialog {
            this.uploader.onError(fn)
            ;return this;
        }

    }
}