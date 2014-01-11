module app.create {

    export class ContentTypesList extends api.ui.list.ListView<api.schema.content.ContentTypeSummary> implements api.event.Observable {

        private markRoots: boolean;

        private siteRootContentTypes: SiteRootContentTypes;

        private listeners: ContentTypesListListener[] = [];

        constructor(className?: string, title?: string, markRoots?: boolean) {
            super(true, className, title);

            this.markRoots = markRoots;
        }

        addListener(listener: ContentTypesListListener) {
            this.listeners.push(listener);
        }

        removeListener(listener: ContentTypesListListener) {
            this.listeners = this.listeners.filter(function (curr) {
                return curr != listener;
            });
        }

        private notifySelected(contentTypeListItem: ContentTypeListItem) {
            this.listeners.forEach((listener: ContentTypesListListener) => {
                listener.onSelected(contentTypeListItem);
            });
        }

        setContentTypes(contentTypes: ContentTypes, siteRootContentTypes: SiteRootContentTypes) {
            // should be set first, cuz super depends on it
            this.siteRootContentTypes = siteRootContentTypes;
            super.setItems(contentTypes.get());
        }

        createListItem(contentType: api.schema.content.ContentTypeSummary): ContentTypeListItem {

            var isSiteRoot = false;
            if(this.siteRootContentTypes) {
                isSiteRoot = this.siteRootContentTypes.isSiteRoot(contentType.getName());
            }
            var listItem = new ContentTypeListItem(contentType, isSiteRoot, this.markRoots);

            listItem.getEl().addEventListener("click", () => {
                this.notifySelected(listItem);
            });
            return listItem;
        }
    }

}