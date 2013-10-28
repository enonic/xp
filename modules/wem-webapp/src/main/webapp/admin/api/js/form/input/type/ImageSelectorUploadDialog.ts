module api_form_input_type {

    export class ImageSelectorUploadDialog extends api_ui_dialog.ModalDialog implements api_event.Observable {

        private uploader:api_ui.ImageUploader;

        private listeners:ImageSelectorUploadDialogListener[] = [];

        constructor() {
            super({
                title: "Image uploader",
                width: 800,
                height: 520,
                idPrefix: "ImageSelectorUploadDialog"
            });

            this.getEl().addClass("image-selector-upload-dialog");

            var description = new api_dom.PEl();
            description.getEl().setInnerHtml("Images uploaded will be embedded directly in this content, you may move them to the library later if desired");
            this.appendChildToContentPanel(description);

            this.uploader = new api_ui.ImageUploader("image-selector-upload-dialog", api_util.getRestUri("upload"));
            this.uploader.setButtonsVisible(false);
            this.uploader.addListener({
                onFileUploaded: (id:string, name:string, mimeType:string) => {
                    this.close();
                    this.notifyImageUploaded(id, name, mimeType);
                }
            });
            this.appendChildToContentPanel(this.uploader);

            this.setCancelAction(new CancelImageSelectorUploadDialog());
            this.getCancelAction().addExecutionListener((action:CancelImageSelectorUploadDialog) => {
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

        addListener(listener:ImageSelectorUploadDialogListener) {
            this.listeners.push(listener);
        }

        removeListener(listener:ImageSelectorUploadDialogListener) {
            this.listeners = this.listeners.filter(function (curr) {
                return curr != listener;
            });
        }

        private notifyImageUploaded(id:string, name:string, mimeType:string) {
            this.listeners.forEach((listener:ImageSelectorUploadDialogListener) => {
                if (listener.onImageUploaded) {
                    listener.onImageUploaded(id, name, mimeType);
                }
            });
        }

    }

    export interface ImageSelectorUploadDialogListener extends api_event.Listener {

        onImageUploaded: (id:string, name:string, mimeType:string) => void;

    }

    class CancelImageSelectorUploadDialog extends api_ui.Action {

        constructor() {
            super("Cancel", "esc");
        }
    }

}