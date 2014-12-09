module api.content.form.inputtype.image {

    export class ImageUploadDialog extends api.ui.dialog.ModalDialog {

        private imageUploader: api.ui.uploader.ImageUploader;

        constructor() {
            super({
                title: new api.ui.dialog.ModalDialogHeader("Image uploader")
            });

            this.getEl().addClass("upload-dialog");

            var description = new api.dom.PEl();
            description.getEl().setInnerHtml("Images uploaded will be added as children to this content, you may move them later if desired");
            this.appendChildToContentPanel(description);

            this.imageUploader = new api.ui.uploader.ImageUploader({
                name: 'image-selector-upload-dialog',
                url: api.util.UriHelper.getRestUri("blob/upload"),
                showButtons: false,
                showResult: false
            });

            this.imageUploader.onUploadCompleted(() => {
                this.close();
            });
            this.appendChildToContentPanel(this.imageUploader);

            this.setCancelAction(new UploadDialogCancelAction());
            this.getCancelAction().onExecuted((action: UploadDialogCancelAction) => {
                this.imageUploader.stop();
                this.imageUploader.reset();
                this.close();
            });

            api.dom.Body.get().appendChild(this);

        }

        setMaximumOccurrences(value: number) {
            this.imageUploader.setMaximumOccurrences(value);
        }

        open() {
            this.imageUploader.reset();
            super.open();
        }

        onImageUploaded(listener: (event: api.ui.uploader.FileUploadedEvent) => void) {
            this.imageUploader.onFileUploaded(listener);
        }

        unImageUploaded(listener: (event: api.ui.uploader.FileUploadedEvent) => void) {
            this.imageUploader.unFileUploaded(listener);
        }

    }
}