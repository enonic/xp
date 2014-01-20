module app.create {

    export class SiteTemplatesList extends api.ui.list.ListView<api.content.site.template.SiteTemplateSummary> implements api.event.Observable {

        private listeners: SiteTemplatesListListener[] = [];

        private contentTypes: ContentTypes;

        constructor(className?: string, title?: string) {
            super(className, title);
        }

        addListener(listener: SiteTemplatesListListener) {
            this.listeners.push(listener);
        }

        removeListener(listener: SiteTemplatesListListener) {
            this.listeners = this.listeners.filter(function (curr) {
                return curr != listener;
            });
        }

        private notifySelected(item: SiteTemplateListItem) {
            this.listeners.forEach((listener: SiteTemplatesListListener) => {
                listener.onSelected(item);
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