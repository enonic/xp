module app.create {

    export class ContentTypesListSelectedEvent {

        private item: ContentTypeListItem;

        constructor(item: ContentTypeListItem) {
            this.item = item;
        }

        getItem(): ContentTypeListItem {
            return this.item;
        }
    }
}