import ContentPath = api.content.ContentPath;
import FileUploadStartedEvent = api.ui.uploader.FileUploadStartedEvent;
import Content = api.content.Content;

export class NewContentDialogMediaUploader extends api.dom.DivEl {

    private mediaUploaderEl: api.content.MediaUploaderEl;

    constructor() {
        super("uploader-container");

        var uploaderMask = new api.dom.DivEl('uploader-mask');

        this.appendChild(uploaderMask);

        this.mediaUploaderEl = new api.content.MediaUploaderEl({
            operation: api.content.MediaUploaderElOperation.create,
            params: {
                parent: ContentPath.ROOT.toString()
            },
            name: 'new-content-uploader',
            showResult: false,
            showReset: false,
            showCancel: false,
            allowBrowse: false,
            allowMultiSelection: true,
            deferred: true
        });
        this.appendChild(this.mediaUploaderEl);

        this.mediaUploaderEl.setEnabled(true);
    }

    getMediaUploader(): api.content.MediaUploaderEl {
        return this.mediaUploaderEl;
    }

    setEnabled(value: boolean) {
        this.mediaUploaderEl.setEnabled(value);
    }

    isEnabled(): boolean {
        return this.mediaUploaderEl.isEnabled();
    }

    reset() {
        this.mediaUploaderEl.reset();
    }

    setParams(params: {[key: string]: any}) {
        this.mediaUploaderEl.setParams(params);
    }

    stop() {
        this.mediaUploaderEl.stop();
    }

    onUploadStarted(listener: (event: FileUploadStartedEvent<Content>) => void) {
        this.mediaUploaderEl.onUploadStarted(listener);
    }
}