module app.installation {

    import ApplicationKey = api.application.ApplicationKey;
    import FileUploadStartedEvent = api.ui.uploader.FileUploadStartedEvent;
    import UploadItem = api.ui.uploader.UploadItem;
    import Content = api.content.Content;
    import InputEl = api.dom.InputEl;
    import ApplicationUploaderEl = api.application.ApplicationUploaderEl;
    import Application = api.application.Application;

    export class InstallAppDialog extends api.ui.dialog.ModalDialog {

        private installAppDialogTitle: api.ui.dialog.ModalDialogHeader;

        private applicationInput: ApplicationInput;

        private applicationUploaderEl: api.application.ApplicationUploaderEl;

        constructor() {

            this.installAppDialogTitle = new api.ui.dialog.ModalDialogHeader("Install Application");

            super({
                title: this.installAppDialogTitle
            });

            this.addClass("install-application-dialog hidden");

            this.initApplicationInput();

            this.initApplicationUploader();

            api.dom.Body.get().appendChild(this);
        }

        private initApplicationInput() {
            this.applicationInput = new ApplicationInput('large').
                setPlaceholder("Drop files here");

            this.applicationInput.onUploadStarted((event: FileUploadStartedEvent<Application>) => {
                this.closeAndFireEventFromMediaUpload(event.getUploadItems());
            });

            this.applicationInput.onInput((event: Event) => {

            });

            this.applicationInput.onKeyUp((event: KeyboardEvent) => {
                if (event.keyCode === 27) {
                    this.getCancelAction().execute();
                }
            });

            this.appendChildToContentPanel(this.applicationInput);
        }

        private initApplicationUploader() {

            var uploaderContainer = new api.dom.DivEl('uploader-container');
            this.appendChild(uploaderContainer);

            var uploaderMask = new api.dom.DivEl('uploader-mask');
            uploaderContainer.appendChild(uploaderMask);

            this.applicationUploaderEl = new api.application.ApplicationUploaderEl({
                params: {},
                name: 'application-uploader',
                showResult: false,
                allowMultiSelection: false,
                deferred: true  // wait till the window is shown
            });
            uploaderContainer.appendChild(this.applicationUploaderEl);

            this.applicationUploaderEl.onUploadStarted((event: FileUploadStartedEvent<Application>) => {
                this.closeAndFireEventFromMediaUpload(event.getUploadItems());
            });

            var dragOverEl;
            // make use of the fact that when dragging
            // first drag enter occurs on the child element and after that
            // drag leave occurs on the parent element that we came from
            // meaning that to know when we left some element
            // we need to compare it to the one currently dragged over
            this.onDragEnter((event: DragEvent) => {
                var target = <HTMLElement> event.target;

                if (!!dragOverEl || dragOverEl == this.getHTMLElement()) {
                    uploaderContainer.show();
                }
                dragOverEl = target;
            });

            this.onDragLeave((event: DragEvent) => {
                var targetEl = <HTMLElement> event.target;

                if (dragOverEl == targetEl) {
                    uploaderContainer.hide();
                }
            });

            this.onDrop((event: DragEvent) => {
                uploaderContainer.hide();
            });
        }

        private closeAndFireEventFromMediaUpload(items: UploadItem<Application>[]) {
            this.close();
            new api.application.ApplicationUploadStartedEvent(items).fire();
        }

        open() {
            super.open();
        }

        show() {
            this.resetFileInputWithUploader();
            super.show();
            this.applicationInput.giveFocus();
        }

        hide() {
            super.hide();
            this.applicationUploaderEl.stop();
            this.addClass("hidden");
            this.removeClass("animated");
        }

        close() {
            this.applicationInput.reset();
            super.close();
        }

        private resetFileInputWithUploader() {
            this.applicationUploaderEl.reset();
            this.applicationInput.reset();
        }
    }

    export class ApplicationInput extends api.dom.CompositeFormInputEl {

        private textInput: InputEl;
        private applicationUploaderEl: ApplicationUploaderEl;

        constructor(className?: string, originalValue?: string) {
            this.textInput = new InputEl("text");

            this.applicationUploaderEl = new ApplicationUploaderEl({
                name: 'application-input-uploader',
                allowDrop: false,
                showResult: false,
                allowMultiSelection: true,
                deferred: true,  // wait till it's shown
                value: originalValue
            });

            this.applicationUploaderEl.onUploadStarted((event: api.ui.uploader.FileUploadStartedEvent<Application>) => {
                var names = event.getUploadItems().map((uploadItem: api.ui.uploader.UploadItem<Application>) => {
                    return uploadItem.getName();
                });
                this.textInput.setValue(names.join(', '));
            });

            super(this.textInput, this.applicationUploaderEl);
            this.addClass("file-input" + (className ? " " + className : ""));
        }

        setUploaderParams(params: {[key: string]: any}): ApplicationInput {
            this.applicationUploaderEl.setParams(params);
            return this;
        }

        getUploaderParams(): {[key: string]: string} {
            return this.applicationUploaderEl.getParams();
        }

        setPlaceholder(placeholder: string): ApplicationInput {
            this.textInput.setPlaceholder(placeholder);
            return this;
        }

        getPlaceholder(): string {
            return this.textInput.getPlaceholder();
        }

        reset(): ApplicationInput {
            this.textInput.reset();
            this.applicationUploaderEl.reset();
            return this;
        }

        stop(): ApplicationInput {
            this.applicationUploaderEl.stop();
            return this;
        }

        getUploader(): ApplicationUploaderEl {
            return this.applicationUploaderEl;
        }

        onUploadStarted(listener: (event: FileUploadStartedEvent<Application>) => void) {
            this.applicationUploaderEl.onUploadStarted(listener);
        }

        unUploadStarted(listener: (event: FileUploadStartedEvent<Application>) => void) {
            this.applicationUploaderEl.unUploadStarted(listener);
        }
    }
}
