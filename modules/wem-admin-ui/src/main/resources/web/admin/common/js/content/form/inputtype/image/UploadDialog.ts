module api.content.form.inputtype.image {

    export class UploadDialog extends api.ui.dialog.ModalDialog {

        private uploader: api.ui.uploader.ImageUploader;

        private imageUploadedListeners: {(event: api.ui.uploader.ImageUploadedEvent):void}[] = [];

        constructor() {
            super({
                title: new api.ui.dialog.ModalDialogHeader("Image uploader")
            });

            this.getEl().addClass("upload-dialog");

            var description = new api.dom.PEl();
            description.getEl().setInnerHtml("Images uploaded will be added as children to this content, you may move them later if desired");
            this.appendChildToContentPanel(description);

            var uploaderConfig = {
                multiSelection: true,
                buttonsVisible: false,
                showImageAfterUpload: false
            };
            this.uploader =
            new api.ui.uploader.ImageUploader("image-selector-upload-dialog", api.util.getRestUri("blob/upload"), uploaderConfig);
            this.uploader.onImageUploaded((event: api.ui.uploader.ImageUploadedEvent) => {
                this.notifyImageUploaded(event.getUploadedItem());
            });
            this.uploader.onImageUploadComplete(() => {
                this.close();
            });
            this.appendChildToContentPanel(this.uploader);

            this.setCancelAction(new UploadDialogCancelAction());
            this.getCancelAction().onExecuted((action: UploadDialogCancelAction) => {
                this.uploader.stop();
                this.uploader.reset();
                this.close();
            });

            api.dom.Body.get().appendChild(this);

        }

        setMaximumOccurrences(value: number) {
            this.uploader.setMaximumOccurrences(value);
        }

        open() {
            this.uploader.reset();
            super.open();
        }

        onImageUploaded(listener: (event: api.ui.uploader.ImageUploadedEvent)=>void) {
            this.imageUploadedListeners.push(listener);
        }

        unImageUploaded(listener: (event: api.ui.uploader.ImageUploadedEvent)=>void) {
            this.imageUploadedListeners =
            this.imageUploadedListeners.filter((currentListener: (event: api.ui.uploader.ImageUploadedEvent)=>void) => {
                return listener != currentListener;
            })
        }

        private notifyImageUploaded(uploadItem: api.ui.uploader.UploadItem) {
            this.imageUploadedListeners.forEach((listener: (event: api.ui.uploader.ImageUploadedEvent)=>void) => {
                listener.call(this, new api.ui.uploader.ImageUploadedEvent(uploadItem));
            });
        }

    }
}