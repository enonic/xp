import ContentPath = api.content.ContentPath;
import FileUploadStartedEvent = api.ui.uploader.FileUploadStartedEvent;
import Content = api.content.Content;

export class NewContentDialogMediaUploader extends api.dom.DivEl {

    private uploaderEnabled: boolean;

    private mediaUploaderEl: api.content.MediaUploaderEl;

    constructor() {
        super("uploader-container");

        this.uploaderEnabled = true;

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
            allowMultiSelection: true,
            deferred: true  // wait till the window is shown
        });
        this.appendChild(this.mediaUploaderEl);
    }

    setEnabled(value: boolean) {
        this.uploaderEnabled = value;
    }

    isEnabled(): boolean {
        return this.uploaderEnabled;
    }

    reset() {
        this.mediaUploaderEl.reset();
        this.mediaUploaderEl.setEnabled(this.uploaderEnabled);
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