module app.create {

    export class SiteTemplatesListSelectedEvent {

        private item: SiteTemplateListItem;

        constructor(item: SiteTemplateListItem) {
            this.item = item;
        }

        getItem(): SiteTemplateListItem {
            return this.item;
        }
    }
}