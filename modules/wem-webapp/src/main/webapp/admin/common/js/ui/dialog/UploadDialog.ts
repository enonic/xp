module api_ui_dialog {

    export class UploadDialog extends api_ui_dialog.ModalDialog {

        private uploader:UploadDialogUploaderEl;

        constructor(title:string, description: string, uploaderEl:UploadDialogUploaderEl) {
            super({
                title: title,
                width: 800,
                height: 520,
                idPrefix: "UploadDialog"
            });

            this.getEl().addClass("upload-dialog");

            var descriptionEl = new api_dom.PEl();
            descriptionEl.getEl().setInnerHtml(description);
            this.appendChildToContentPanel(descriptionEl);

            this.uploader = uploaderEl;
            this.appendChildToContentPanel(<any>this.uploader);

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

    }
}