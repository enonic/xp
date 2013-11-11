module api_form_inputtype_content_image {

    export class UploadDialog extends api_ui_dialog.ModalDialog implements api_event.Observable {

        private uploader:api_ui.ImageUploader;

        private listeners:UploadDialogListener[] = [];

        constructor() {
            super({
                title: "Image uploader",
                width: 800,
                height: 520,
                idPrefix: "UploadDialog"
            });

            this.getEl().addClass("image-selector-upload-dialog");

            var description = new api_dom.PEl();
            description.getEl().setInnerHtml("Images uploaded will be embedded directly in this content, you may move them to the library later if desired");
            this.appendChildToContentPanel(description);

            var uploaderConfig = {
                multiSelection: true,
                buttonsVisible: false,
                imageVisible: false
            };
            this.uploader = new api_ui.ImageUploader("image-selector-upload-dialog", api_util.getRestUri("upload"), uploaderConfig);
            this.uploader.addListener({
                onFileUploaded: (id:string, name:string, mimeType:string) => {
                    this.notifyImageUploaded(id, name, mimeType);
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

            api_dom.Body.get().appendChild(this);

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

        private notifyImageUploaded(id:string, name:string, mimeType:string) {
            this.listeners.forEach((listener:UploadDialogListener) => {
                listener.onImageUploaded(id, name, mimeType);
            });
        }

    }
}