module app.create {

    export class ContentTypesList extends api.ui.selector.list.ListBox<api.schema.content.ContentTypeSummary> {

        private markRoots: boolean;

        private siteRootContentTypes: SiteRootContentTypes;

        private selectedListeners: {(event: ContentTypesListSelectedEvent):void}[] = [];


        constructor(className?: string, title?: string, markRoots?: boolean) {
            super(className, title);

            this.markRoots = markRoots;
        }

        onSelected(listener: (event: ContentTypesListSelectedEvent)=>void) {
            this.selectedListeners.push(listener);
        }

        unSelected(listener: (event: ContentTypesListSelectedEvent)=>void) {
            this.selectedListeners = this.selectedListeners.filter((currentListener: (event: ContentTypesListSelectedEvent)=>void)=> {
                return currentListener != listener;
            });
        }

        private notifySelected(contentTypeListItem: ContentTypeListItem) {
            this.selectedListeners.forEach((listener: (event: ContentTypesListSelectedEvent)=>void) => {
                listener.call(this, new ContentTypesListSelectedEvent(contentTypeListItem));
            });
        }

        setContentTypes(contentTypes: ContentTypes, siteRootContentTypes: SiteRootContentTypes) {
            // should be set first, cuz super depends on it
            this.siteRootContentTypes = siteRootContentTypes;
            super.setItems(contentTypes.get());
        }

        createListItem(contentType: api.schema.content.ContentTypeSummary): ContentTypeListItem {

            var isSiteRoot = false;
            if (this.siteRootContentTypes) {
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