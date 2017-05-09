import '../../api.ts';

export class ContentPreviewPathChangedEvent extends api.event.Event {

    private previewPath: string;

    constructor(previewPath: string) {
        super();
        this.previewPath = previewPath;
    }

    getPreviewPath() {
        return this.previewPath;
    }

    static on(handler: (event: ContentPreviewPathChangedEvent) => void, contextWindow: Window = window) {
        api.event.Event.bind(api.ClassHelper.getFullName(this), handler, contextWindow);
    }

    static un(handler?: (event: ContentPreviewPathChangedEvent) => void, contextWindow: Window = window) {
        api.event.Event.unbind(api.ClassHelper.getFullName(this), handler, contextWindow);
    }
}
