module api.ui.dialog {

    export class UploadDialog extends api.ui.dialog.ModalDialog {

        private uploader: UploadDialogUploaderEl;

        constructor(title: string, description: string, uploaderEl: UploadDialogUploaderEl) {
            super({
                title: new api.ui.dialog.ModalDialogHeader(title)
            });

            this.getEl().addClass("upload-dialog");

            var descriptionEl = new api.dom.PEl();
            descriptionEl.getEl().setInnerHtml(description);
            this.appendChildToContentPanel(descriptionEl);

            this.uploader = uploaderEl;
            this.appendChildToContentPanel(<any>this.uploader);

            this.setCancelAction(new UploadDialogCancelAction());
            this.getCancelAction().onExecuted((action: UploadDialogCancelAction) => {
                this.uploader.stop();
                this.uploader.reset();
                this.close();
            });

            api.dom.Body.get().appendChild(this);

        }

        open() {
            this.uploader.reset();
            super.open();
        }

    }
}