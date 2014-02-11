module api.content.page {

    export class PageTemplateSummaryLoader implements api.util.Loader {

        private getTemplatesRequest: PageTemplateResourceRequest<api.content.page.json.PageTemplateSummaryListJson>;

        private isLoading: boolean;

        private templates: PageTemplateSummary[];

        private listeners: PageTemplateSummaryLoaderListener[] = [];

        constructor(request: PageTemplateResourceRequest<api.content.page.json.PageTemplateSummaryListJson>) {
            this.isLoading = false;
            this.setRequest(request);
        }

        setRequest(request: PageTemplateResourceRequest<api.content.page.json.PageTemplateSummaryListJson>) {
            this.getTemplatesRequest = request;
            this.isLoading = true;
            this.notifyLoading();
            this.doRequest(this.getTemplatesRequest).
                done((templates: PageTemplateSummary[]) => {
                    this.templates = templates;
                    this.isLoading = false;
                    this.notifyLoaded(this.templates);
                });
        }

        doRequest(getTemplatesRequest: PageTemplateResourceRequest<api.content.page.json.PageTemplateSummaryListJson>): Q.Promise<PageTemplateSummary[]> {
            var deferred = Q.defer<PageTemplateSummary[]>();

            getTemplatesRequest.sendAndParse()
                .done((templates:PageTemplateSummary[]) => {
                    deferred.resolve(templates)
                });
            return deferred.promise;
        }

        search(searchString: string) {

            if (this.templates) {
                var filtered = this.templates.filter((template: PageTemplateSummary) => {
                    return template.getDisplayName().toString().indexOf(searchString.toLowerCase()) != -1;
                });
                this.notifyLoaded(filtered);
            }
        }

        addListener(listener: PageTemplateSummaryLoaderListener) {
            this.listeners.push(listener);
        }

        removeListener(listenerToRemove: PageTemplateSummaryLoaderListener) {
            this.listeners = this.listeners.filter((listener) => {
                return listener != listenerToRemove;
            })
        }

        private notifyLoading() {
            this.listeners.forEach((listener: PageTemplateSummaryLoaderListener) => {
                if (listener.onLoading) {
                    listener.onLoading();
                }
            });
        }

        private notifyLoaded(templateSummary: PageTemplateSummary[]) {
            this.listeners.forEach((listener: PageTemplateSummaryLoaderListener) => {
                if (listener.onLoaded) {
                    listener.onLoaded(templateSummary);
                }
            });
        }
    }
}
