module api.content.page.image {

    export class OpenImageUploadDialogEvent extends api.event.Event2 {

        constructor() {
            super('openImageUploadDialogEvent.liveEdit');
        }

        static on(handler: (event: OpenImageUploadDialogEvent) => void, contextWindow: Window = window) {
            api.event.onEvent2('openImageUploadDialogEvent.liveEdit', handler, contextWindow);
        }

        static un(handler: (event: OpenImageUploadDialogEvent) => void, contextWindow: Window = window) {
            api.event.unEvent2('openImageUploadDialogEvent.liveEdit', handler, contextWindow);
        }
    }

}