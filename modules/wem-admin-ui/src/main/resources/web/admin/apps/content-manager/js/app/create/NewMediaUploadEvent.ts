module app.create {

    import Content = api.content.Content;
    import UploadItem = api.ui.uploader.UploadItem;

    export class NewMediaUploadEvent extends api.event.Event {

        private uploadItems: UploadItem<Content>[];

        private parentContent: Content;

        constructor(items: UploadItem<Content>[], parentContent: Content) {
            super();
            this.uploadItems = items;
            this.parentContent = parentContent;
        }

        getUploadItems(): UploadItem<Content>[] {
            return this.uploadItems;
        }

        getParentContent(): Content {
            return this.parentContent;
        }

        static on(handler: (event: NewMediaUploadEvent) => void) {
            api.event.Event.bind(api.ClassHelper.getFullName(this), handler);
        }

        static un(handler?: (event: NewMediaUploadEvent) => void) {
            api.event.Event.unbind(api.ClassHelper.getFullName(this), handler);
        }
    }

}