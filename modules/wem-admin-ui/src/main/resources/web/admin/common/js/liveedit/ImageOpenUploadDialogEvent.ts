module api.liveedit {

    import Event = api.event.Event;
    import ImagePlaceholder = api.liveedit.image.ImagePlaceholder;

    export class ImageOpenUploadDialogEvent extends api.event.Event {

        private targetImagePlaceholder: ImagePlaceholder;

        constructor(target: ImagePlaceholder) {
            super();
            this.targetImagePlaceholder = target;
        }

        getTargetImagePlaceholder(): ImagePlaceholder {
            return this.targetImagePlaceholder;
        }

        static on(handler: (event: ImageOpenUploadDialogEvent) => void, contextWindow: Window = window) {
            Event.bind(api.ClassHelper.getFullName(this), handler, contextWindow);
        }

        static un(handler: (event: ImageOpenUploadDialogEvent) => void, contextWindow: Window = window) {
            Event.unbind(api.ClassHelper.getFullName(this), handler, contextWindow);
        }
    }

}