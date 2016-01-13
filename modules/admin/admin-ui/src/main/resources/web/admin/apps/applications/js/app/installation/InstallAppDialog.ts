module app.installation {

    import ApplicationKey = api.application.ApplicationKey;
    import FileUploadStartedEvent = api.ui.uploader.FileUploadStartedEvent;
    import UploadItem = api.ui.uploader.UploadItem;
    import Content = api.content.Content;

    export class InstallAppDialog extends api.ui.dialog.ModalDialog {

        private installAppDialogTitle: InstallAppDialogTitle;

        private fileInput: api.ui.text.FileInput;

        private mediaUploaderEl: api.content.MediaUploaderEl;

        constructor() {

            this.installAppDialogTitle = new InstallAppDialogTitle("Install Application");

            super({
                title: this.installAppDialogTitle
            });

            this.addClass("install-application-dialog hidden");

            this.initFileInput();

            this.initMediaUploader();

            api.dom.Body.get().appendChild(this);
        }

        private initFileInput() {
            this.fileInput = new api.ui.text.FileInput('large').setPlaceholder("Drop files here").setUploaderParams({});

            this.fileInput.onUploadStarted((event: FileUploadStartedEvent<Content>) => {
                this.closeAndFireEventFromMediaUpload(event.getUploadItems());
            });

            this.fileInput.onInput((event: Event) => {

            });

            this.fileInput.onKeyUp((event: KeyboardEvent) => {
                if (event.keyCode === 27) {
                    this.getCancelAction().execute();
                }
            });

            this.appendChildToContentPanel(this.fileInput);
        }

        private initMediaUploader() {

            var uploaderContainer = new api.dom.DivEl('uploader-container');
            this.appendChild(uploaderContainer);

            var uploaderMask = new api.dom.DivEl('uploader-mask');
            uploaderContainer.appendChild(uploaderMask);

            this.mediaUploaderEl = new api.content.MediaUploaderEl({
                operation: api.content.MediaUploaderElOperation.create,
                params: {},
                name: 'new-content-uploader',
                showResult: false,
                allowMultiSelection: true,
                deferred: true  // wait till the window is shown
            });
            uploaderContainer.appendChild(this.mediaUploaderEl);

            this.mediaUploaderEl.onUploadStarted((event: FileUploadStartedEvent<Content>) => {
                this.closeAndFireEventFromMediaUpload(event.getUploadItems());
            });

            var dragOverEl;
            // make use of the fact that when dragging
            // first drag enter occurs on the child element and after that
            // drag leave occurs on the parent element that we came from
            // meaning that to know when we left some element
            // we need to compare it to the one currently dragged over
            this.onDragEnter((event: DragEvent) => {
                if (true) {
                    var target = <HTMLElement> event.target;

                    if (!!dragOverEl || dragOverEl == this.getHTMLElement()) {
                        uploaderContainer.show();
                    }
                    dragOverEl = target;
                }
            });

            this.onDragLeave((event: DragEvent) => {
                if (true) {
                    var targetEl = <HTMLElement> event.target;

                    if (dragOverEl == targetEl) {
                        uploaderContainer.hide();
                    }
                }
            });

            this.onDrop((event: DragEvent) => {
                if (true) {
                    uploaderContainer.hide();
                }

            });
        }

        private closeAndFireEventFromMediaUpload(items: UploadItem<Content>[]) {
            this.close();
            // fire install app event?
        }

        open() {
            super.open();
        }

        show() {
            this.resetFileInputWithUploader();

            super.show();

            this.fileInput.giveFocus();
        }

        hide() {
            super.hide();
            this.mediaUploaderEl.stop();
            this.addClass("hidden");
            this.removeClass("animated");
        }

        close() {
            this.fileInput.reset();
            super.close();
        }

        private resetFileInputWithUploader() {
            this.mediaUploaderEl.reset();
            this.fileInput.reset();
            this.mediaUploaderEl.setEnabled(true);
            this.fileInput.getUploader().setEnabled(true);
        }
    }

    export class InstallAppDialogTitle extends api.ui.dialog.ModalDialogHeader {

        constructor(title: string) {
            super(title);
        }
    }
}
