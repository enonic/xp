module app_new {

    export class TemplatesList extends api_ui_list.ListView<api_content_site_template.SiteTemplateSummary> implements api_event.Observable {

        private listeners: SiteTemplatesListListener[] = [];

        private contentTypes: ContentTypes;

        constructor(className?: string, title?: string) {
            super("TemplatesList", className, title);
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

        setSiteTemplates(siteTemplates: api_content_site_template.SiteTemplateSummary[], contentTypes: ContentTypes) {
            this.contentTypes = contentTypes;
            super.setItems(siteTemplates);
        }

        createListItem(item: api_content_site_template.SiteTemplateSummary): SiteTemplateListItem {

            var contentType = this.contentTypes.getByName(item.getRootContentType());
            var listItem = new SiteTemplateListItem(item, contentType);

            listItem.getEl().addEventListener("click", () => {
                this.notifySelected(listItem);
            });
            return listItem;
        }
    }

}