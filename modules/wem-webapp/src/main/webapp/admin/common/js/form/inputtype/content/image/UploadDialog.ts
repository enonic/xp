module api.form.inputtype.content.image {

    export class UploadDialog extends api.ui.dialog.ModalDialog implements api.event.Observable {

        private uploader:api.ui.ImageUploader;

        private listeners:UploadDialogListener[] = [];

        constructor() {
            super({
                title: "Image uploader",
                width: 800,
                height: 520,
                idPrefix: "UploadDialog"
            });

            this.getEl().addClass("image-selector-upload-dialog");

            var description = new api.dom.PEl();
            description.getEl().setInnerHtml("Images uploaded will be embedded directly in this content, you may move them to the library later if desired");
            this.appendChildToContentPanel(description);

            var uploaderConfig = {
                multiSelection: true,
                buttonsVisible: false,
                showImageAfterUpload: false
            };
            this.uploader = new api.ui.ImageUploader("image-selector-upload-dialog", api.util.getRestUri("blob/upload"), uploaderConfig);
            this.uploader.addListener({
                onFileUploaded: (uploadItem:api.ui.UploadItem) => {
                    this.notifyImageUploaded(uploadItem);
                },
                onUploadComplete: () => {
                    this.close();
                }
            });
            this.appendChildToContentPanel(this.uploader);

            this.setCancelAction(new UploadDialogCancelAction());
            this.getCancelAction().addExecutionListener((action:UploadDialogCancelAction) => {
                this.uploader.stop();
                this.uploader.reset();
                this.close();
            });

            api.dom.Body.get().appendChild(this);

        }

        setMaximumOccurrences(value:number) {
            this.uploader.setMaximumOccurrences(value);
        }

        open() {
            this.uploader.reset();
            super.open();
        }

        addListener(listener:UploadDialogListener) {
            this.listeners.push(listener);
        }

        removeListener(listener:UploadDialogListener) {
            this.listeners = this.listeners.filter(function (curr) {
                return curr != listener;
            });
        }

        private notifyImageUploaded(uploadItem:api.ui.UploadItem) {
            this.listeners.forEach((listener:UploadDialogListener) => {
                listener.onImageUploaded(uploadItem);
            });
        }

    }
}