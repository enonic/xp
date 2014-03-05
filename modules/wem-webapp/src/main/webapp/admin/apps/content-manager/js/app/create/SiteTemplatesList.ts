module app.create {

    export class SiteTemplatesList extends api.ui.list.ListBox<api.content.site.template.SiteTemplateSummary> {

        private selectedListeners: {(event: SiteTemplatesListSelectedEvent):void}[] = [];

        private contentTypes: ContentTypes;

        constructor(className?: string, title?: string) {
            super(className, title);
        }

        onSelected(listener: (event: SiteTemplatesListSelectedEvent)=>void) {
            this.selectedListeners.push(listener);
        }

        unSelected(listener: (event: SiteTemplatesListSelectedEvent)=>void) {
            this.selectedListeners = this.selectedListeners.filter((currentListener: (event: SiteTemplatesListSelectedEvent)=>void)=> {
                return listener != currentListener;
            });
        }

        private notifySelected(item: SiteTemplateListItem) {
            this.selectedListeners.forEach((listener: (event: SiteTemplatesListSelectedEvent)=>void)=> {
                listener.call(this, new SiteTemplatesListSelectedEvent(item));
            });
        }

        setSiteTemplates(siteTemplates: api.content.site.template.SiteTemplateSummary[], contentTypes: ContentTypes) {
            this.contentTypes = contentTypes;
            super.setItems(siteTemplates);
        }

        createListItem(item: api.content.site.template.SiteTemplateSummary): SiteTemplateListItem {

            var contentType = this.contentTypes.getByName(item.getRootContentType());
            var listItem = new SiteTemplateListItem(item, contentType);

            listItem.getEl().addEventListener("click", () => {
                this.notifySelected(listItem);
            });
            return listItem;
        }
    }

}