module api.content.page {

    export class PageTemplateSummaryLoader implements api.util.Loader {

        private findContentTypesRequest:GetPageTemplatesRequest;

        private isLoading:boolean;

        private pageTemplates:PageTemplateSummary[];

        private listeners:PageTemplateSummaryLoaderListener[] = [];

        constructor(siteTemplateKey: api.content.site.template.SiteTemplateKey) {
            this.isLoading = false;
            this.findContentTypesRequest = new GetPageTemplatesRequest(siteTemplateKey);
            this.doRequest();
        }


        search(searchString:string) {

            if (this.pageTemplates) {
                var filtered = this.pageTemplates.filter((pageTemplate:PageTemplateSummary) => {
                   return pageTemplate.getDisplayName().toString().indexOf(searchString.toLowerCase()) != -1;
                });
                this.notifyLoaded(filtered);
            }
        }



        private doRequest() {
            this.isLoading = true;
            this.notifyLoading();

            this.findContentTypesRequest.send()
                .done((jsonResponse:api.rest.JsonResponse<api.content.page.json.PageTemplateSummaryListJson>) => {
                var result = jsonResponse.getResult();
                this.pageTemplates = PageTemplateSummary.fromJsonArray(result.templates);
                this.isLoading = false;
                this.notifyLoaded(this.pageTemplates);
            });
        }

        addListener(listener:PageTemplateSummaryLoaderListener) {
            this.listeners.push(listener);
        }

        removeListener(listenerToRemove:PageTemplateSummaryLoaderListener) {
            this.listeners = this.listeners.filter((listener) => {
                return listener != listenerToRemove;
            })
        }

        private notifyLoading() {
            this.listeners.forEach((listener:PageTemplateSummaryLoaderListener) => {
                if (listener.onLoading) {
                    listener.onLoading();
                }
            });
        }

        private notifyLoaded(pageTemplateSummary:PageTemplateSummary[]) {
            this.listeners.forEach((listener:PageTemplateSummaryLoaderListener) => {
                if (listener.onLoaded) {
                    listener.onLoaded(pageTemplateSummary);
                }
            });
        }

    }

}