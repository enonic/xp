module api.liveedit {

    import Event2 = api.event.Event2;
    import ImagePlaceholder = api.liveedit.image.ImagePlaceholder;

    export class ImageOpenUploadDialogEvent extends Event2 {

        private targetImagePlaceholder: ImagePlaceholder;

        constructor(target: ImagePlaceholder) {
            super();
            this.targetImagePlaceholder = target;
        }

        getTargetImagePlaceholder(): ImagePlaceholder {
            return this.targetImagePlaceholder;
        }

        static on(handler: (event: ImageOpenUploadDialogEvent) => void, contextWindow: Window = window) {
            Event2.bind(api.util.getFullName(this), handler, contextWindow);
        }

        static un(handler: (event: ImageOpenUploadDialogEvent) => void, contextWindow: Window = window) {
            Event2.unbind(api.util.getFullName(this), handler, contextWindow);
        }
    }

}