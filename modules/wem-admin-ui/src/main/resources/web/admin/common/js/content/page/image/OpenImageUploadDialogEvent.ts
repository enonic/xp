module api.content.page.image {

    import Event2 = api.event.Event2;

    export class OpenImageUploadDialogEvent extends Event2 {

        constructor() {
            super('openImageUploadDialogEvent.liveEdit');
        }

        static on(handler: (event: OpenImageUploadDialogEvent) => void, contextWindow: Window = window) {
            Event2.bind('openImageUploadDialogEvent.liveEdit', handler, contextWindow);
        }

        static un(handler: (event: OpenImageUploadDialogEvent) => void, contextWindow: Window = window) {
            Event2.unbind('openImageUploadDialogEvent.liveEdit', handler, contextWindow);
        }
    }

}