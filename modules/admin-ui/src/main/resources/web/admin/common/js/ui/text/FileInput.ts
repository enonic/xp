module api.ui.text {

    import InputEl = api.dom.InputEl;

    import Content = api.content.Content;
    import MediaUploader = api.content.MediaUploader;

    import FileUploadStartedEvent = api.ui.uploader.FileUploadStartedEvent;
    import FileUploadProgressEvent = api.ui.uploader.FileUploadProgressEvent;
    import FileUploadedEvent = api.ui.uploader.FileUploadedEvent;
    import FileUploadCompleteEvent = api.ui.uploader.FileUploadCompleteEvent;
    import FileUploadFailedEvent = api.ui.uploader.FileUploadFailedEvent;

    export class FileInput extends api.dom.FormInputEl {

        private textInput: InputEl;
        private mediaUploader: MediaUploader;

        constructor(className?: string) {
            super("div", "file-input");

            if (className) {
                this.addClass(className);
            }

            this.textInput = new InputEl("text");

            this.mediaUploader = new api.content.MediaUploader({
                operation: api.content.MediaUploaderOperation.create,
                name: 'file-input-uploader',
                allowDrop: false,
                showButtons: false,
                showResult: false,
                allowMultiSelection: true,
                deferred: true  // wait till it's shown
            });

            this.mediaUploader.onUploadStarted((event: api.ui.uploader.FileUploadStartedEvent<api.content.Content>) => {
                var names = event.getUploadItems().map((uploadItem: api.ui.uploader.UploadItem<api.content.Content>) => {
                    return uploadItem.getName();
                });
                this.textInput.setValue(names.join(', '));
            });

            this.appendChildren(this.textInput, this.mediaUploader);
        }

        setUploaderParams(params: {[key: string]: any}): FileInput {
            this.mediaUploader.setParams(params);
            return this;
        }

        getUploaderParams(): {[key: string]: string} {
            return this.mediaUploader.getParams();
        }

        setPlaceholder(placeholder: string): FileInput {
            this.textInput.setPlaceholder(placeholder);
            return this;
        }

        getPlaceholder(): string {
            return this.textInput.getPlaceholder();
        }

        getValue(): string {
            return this.textInput.getValue();
        }

        setValue(value: string): FileInput {
            this.textInput.setValue(value);
            return this;
        }

        onChange(listener: (event: Event) => void) {
            this.textInput.onChange(listener);
        }

        unChange(listener: (event: Event) => void) {
            this.textInput.unChange(listener);
        }

        onInput(listener: (event: Event) => void) {
            this.textInput.onInput(listener);
        }

        unInput(listener: (event: Event) => void) {
            this.textInput.unInput(listener);
        }

        giveFocus(): boolean {
            return this.textInput.giveFocus();
        }

        giveBlur(): boolean {
            return this.textInput.giveBlur();
        }

        reset(): FileInput {
            this.textInput.reset();
            this.mediaUploader.reset();
            return this;
        }

        stop(): FileInput {
            this.mediaUploader.stop();
            return this;
        }

        getUploader(): MediaUploader{
            return this.mediaUploader;
        }

        onUploadStarted(listener: (event: FileUploadStartedEvent<Content>) => void) {
            this.mediaUploader.onUploadStarted(listener);
        }

        unUploadStarted(listener: (event: FileUploadStartedEvent<Content>) => void) {
            this.mediaUploader.unUploadStarted(listener);
        }

        onUploadProgress(listener: (event: FileUploadProgressEvent<Content>) => void) {
            this.mediaUploader.onUploadProgress(listener);
        }

        unUploadProgress(listener: (event: FileUploadProgressEvent<Content>) => void) {
            this.mediaUploader.unUploadProgress(listener);
        }

        onFileUploaded(listener: (event: FileUploadedEvent<Content>) => void) {
            this.mediaUploader.onFileUploaded(listener);
        }

        unFileUploaded(listener: (event: FileUploadedEvent<Content>) => void) {
            this.mediaUploader.unFileUploaded(listener);
        }

        onUploadCompleted(listener: (event: FileUploadCompleteEvent<Content>) => void) {
            this.mediaUploader.onUploadCompleted(listener);
        }

        unUploadCompleted(listener: (event: FileUploadCompleteEvent<Content>) => void) {
            this.mediaUploader.unUploadCompleted(listener);
        }

        onUploadReset(listener: () => void) {
            this.mediaUploader.onUploadReset(listener);
        }

        unUploadReset(listener: () => void) {
            this.mediaUploader.unUploadReset(listener);
        }

        onUploadFailed(listener: (event: FileUploadFailedEvent<Content>) => void) {
            this.mediaUploader.onUploadFailed(listener);
        }

        unUploadFailed(listener: (event: FileUploadFailedEvent<Content>) => void) {
            this.mediaUploader.unUploadFailed(listener);
        }

    }
}