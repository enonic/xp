module api.content.form.inputtype.image {

    import Content = api.content.Content;
    import FileUploadedEvent = api.ui.uploader.FileUploadedEvent;
    import FileUploadStartedEvent = api.ui.uploader.FileUploadStartedEvent;
    import FileUploadProgressEvent = api.ui.uploader.FileUploadProgressEvent;
    import FileUploadCompleteEvent = api.ui.uploader.FileUploadCompleteEvent;
    import FileUploadFailedEvent = api.ui.uploader.FileUploadFailedEvent;

    export class ImageUploadDialog extends api.ui.dialog.ModalDialog {

        private imageUploader: api.content.ImageUploader;

        constructor(parentContent: api.content.ContentId, allowMultiSelection = false) {

            api.util.assertNotNull(parentContent, "parentContent required");
            super({
                title: new api.ui.dialog.ModalDialogHeader("Image uploader")
            });

            this.getEl().addClass("upload-dialog");

            var description = new api.dom.PEl();
            description.getEl().setInnerHtml("Images uploaded will be added as children to this content, you may move them later if desired");
            this.appendChildToContentPanel(description);

            this.imageUploader = new api.content.ImageUploader({
                params: {
                    parent: parentContent.toString()
                },
                operation: api.content.MediaUploaderOperation.create,
                name: 'image-selector-upload-dialog',
                showButtons: false,
                showResult: false,
                allowMultiSelection: allowMultiSelection,
                skipWizardEvents: true
            });

            this.imageUploader.onUploadCompleted(() => {
                this.close();
            });
            this.appendChildToContentPanel(this.imageUploader);

            this.getCancelAction().onExecuted((action: UploadDialogCancelAction) => {
                this.imageUploader.stop();
                this.imageUploader.reset();
                this.close();
            });

            api.dom.Body.get().appendChild(this);

            this.addCancelButtonToBottom();
        }

        setMaximumOccurrences(value: number) {
            this.imageUploader.setMaximumOccurrences(value);
        }

        open() {
            this.imageUploader.reset();
            super.open();
        }

        onUploadStarted(listener: (event: FileUploadStartedEvent<Content>) => void) {
            this.imageUploader.onUploadStarted(listener);
        }

        unUploadStarted(listener: (event: FileUploadStartedEvent<Content>) => void) {
            this.imageUploader.unUploadStarted(listener);
        }

        onUploadProgress(listener: (event: FileUploadProgressEvent<Content>) => void) {
            this.imageUploader.onUploadProgress(listener);
        }

        unUploadProgress(listener: (event: FileUploadProgressEvent<Content>) => void) {
            this.imageUploader.unUploadProgress(listener);
        }

        onImageUploaded(listener: (event: FileUploadedEvent<Content>)=>void) {
            this.imageUploader.onFileUploaded(listener);
        }

        unImageUploaded(listener: (event: FileUploadedEvent<Content>)=>void) {
            this.imageUploader.unFileUploaded(listener);
        }

        onUploadCompleted(listener: (event: FileUploadCompleteEvent<Content>) => void) {
            this.imageUploader.onUploadCompleted(listener)
        }

        unUploadCompleted(listener: (event: FileUploadCompleteEvent<Content>) => void) {
            this.imageUploader.unUploadCompleted(listener)
        }

        onUploadReset(listener: () => void) {
            this.imageUploader.onUploadReset(listener);
        }

        unUploadReset(listener: () => void) {
            this.imageUploader.unUploadReset(listener);
        }

        onUploadFailed(listener: (event: FileUploadFailedEvent<Content>) => void) {
            this.imageUploader.onUploadFailed(listener);
        }

        unUploadFailed(listener: (event: FileUploadFailedEvent<Content>) => void) {
            this.imageUploader.unUploadFailed(listener);
        }
    }
}