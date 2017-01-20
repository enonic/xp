module api.application {

    import UploadItem = api.ui.uploader.UploadItem;

    export class ApplicationUploadStartedEvent extends api.event.Event {

        private uploadItems: UploadItem<Application>[];

        constructor(items: UploadItem<Application>[]) {
            super();
            this.uploadItems = items;
        }

        getUploadItems(): UploadItem<Application>[] {
            return this.uploadItems;
        }

        static on(handler: (event: ApplicationUploadStartedEvent) => void) {
            api.event.Event.bind(api.ClassHelper.getFullName(this), handler);
        }

        static un(handler?: (event: ApplicationUploadStartedEvent) => void) {
            api.event.Event.unbind(api.ClassHelper.getFullName(this), handler);
        }
    }

}
