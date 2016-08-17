module api.ui.text {

    import InputEl = api.dom.InputEl;

    import Content = api.content.Content;

    import FileUploadStartedEvent = api.ui.uploader.FileUploadStartedEvent;
    import FileUploadProgressEvent = api.ui.uploader.FileUploadProgressEvent;
    import FileUploadedEvent = api.ui.uploader.FileUploadedEvent;
    import FileUploadCompleteEvent = api.ui.uploader.FileUploadCompleteEvent;
    import FileUploadFailedEvent = api.ui.uploader.FileUploadFailedEvent;

    export class FileInput extends api.dom.CompositeFormInputEl {

        private textInput: InputEl;
        private mediaUploaderEl: api.ui.uploader.MediaUploaderEl;

        constructor(className?: string, originalValue?: string) {
            this.textInput = new InputEl("text");

            this.mediaUploaderEl = new api.ui.uploader.MediaUploaderEl({
                operation: api.ui.uploader.MediaUploaderElOperation.create,
                name: 'file-input-uploader',
                allowDrop: true,
                showResult: false,
                showCancel: false,
                allowMultiSelection: true,
                deferred: false,  // wait till it's shown
                value: originalValue
            });

            this.mediaUploaderEl.onUploadStarted((event: api.ui.uploader.FileUploadStartedEvent<api.content.Content>) => {
                var names = event.getUploadItems().map((uploadItem: api.ui.uploader.UploadItem<api.content.Content>) => {
                    return uploadItem.getName();
                });
                this.textInput.setValue(names.join(', '));
            });

            super(this.textInput, this.mediaUploaderEl);
            this.addClass("file-input" + (className ? " " + className : ""));
        }

        setUploaderParams(params: {[key: string]: any}): FileInput {
            this.mediaUploaderEl.setParams(params);
            return this;
        }

        getUploaderParams(): {[key: string]: string} {
            return this.mediaUploaderEl.getParams();
        }

        setPlaceholder(placeholder: string): FileInput {
            this.textInput.setPlaceholder(placeholder);
            return this;
        }

        getPlaceholder(): string {
            return this.textInput.getPlaceholder();
        }

        reset(): FileInput {
            this.textInput.reset();
            this.mediaUploaderEl.reset();
            return this;
        }

        stop(): FileInput {
            this.mediaUploaderEl.stop();
            return this;
        }

        enable() {
            this.textInput.getEl().setDisabled(false);
            this.mediaUploaderEl.getDropzone().getEl().removeAttribute("disabled");
            this.mediaUploaderEl.getEl().removeAttribute("disabled");
        }

        disable() {
            this.textInput.getEl().setDisabled(true);
            this.mediaUploaderEl.getDropzone().getEl().setAttribute("disabled", "true");
            this.mediaUploaderEl.getEl().setAttribute("disabled", "true");
        }

        getUploader(): api.ui.uploader.MediaUploaderEl {
            return this.mediaUploaderEl;
        }

        onUploadStarted(listener: (event: FileUploadStartedEvent<Content>) => void) {
            this.mediaUploaderEl.onUploadStarted(listener);
        }

        unUploadStarted(listener: (event: FileUploadStartedEvent<Content>) => void) {
            this.mediaUploaderEl.unUploadStarted(listener);
        }

        onUploadProgress(listener: (event: FileUploadProgressEvent<Content>) => void) {
            this.mediaUploaderEl.onUploadProgress(listener);
        }

        unUploadProgress(listener: (event: FileUploadProgressEvent<Content>) => void) {
            this.mediaUploaderEl.unUploadProgress(listener);
        }

        onFileUploaded(listener: (event: FileUploadedEvent<Content>) => void) {
            this.mediaUploaderEl.onFileUploaded(listener);
        }

        unFileUploaded(listener: (event: FileUploadedEvent<Content>) => void) {
            this.mediaUploaderEl.unFileUploaded(listener);
        }

        onUploadCompleted(listener: (event: FileUploadCompleteEvent<Content>) => void) {
            this.mediaUploaderEl.onUploadCompleted(listener);
        }

        unUploadCompleted(listener: (event: FileUploadCompleteEvent<Content>) => void) {
            this.mediaUploaderEl.unUploadCompleted(listener);
        }

        onUploadReset(listener: () => void) {
            this.mediaUploaderEl.onUploadReset(listener);
        }

        unUploadReset(listener: () => void) {
            this.mediaUploaderEl.unUploadReset(listener);
        }

        onUploadFailed(listener: (event: FileUploadFailedEvent<Content>) => void) {
            this.mediaUploaderEl.onUploadFailed(listener);
        }

        unUploadFailed(listener: (event: FileUploadFailedEvent<Content>) => void) {
            this.mediaUploaderEl.unUploadFailed(listener);
        }

    }
}