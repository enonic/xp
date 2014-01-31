module api.content.page {

    export class TemplateSummaryLoader<T extends TemplateSummary> implements api.util.Loader {

        private getTemplatesRequest:api.rest.ResourceRequest<any>;

        private isLoading:boolean;

        private templates:T[];

        private listeners:TemplateSummaryLoaderListener<T>[] = [];

        constructor() {
            this.isLoading = false;
        }

        setRequest(request:api.rest.ResourceRequest<any>) {
            this.getTemplatesRequest = request;
            this.isLoading = true;
            this.notifyLoading();
            this.doRequest(this.getTemplatesRequest).done((templates:T[]) => {
                this.templates = templates;
                this.isLoading = false;
                this.notifyLoaded(this.templates);
            });
        }


        search(searchString:string) {

            if (this.templates) {
                var filtered = this.templates.filter((template:T) => {
                    return template.getDisplayName().toString().indexOf(searchString.toLowerCase()) != -1;
                });
                this.notifyLoaded(filtered);
            }
        }

        doRequest(getTemplatesRequest:api.rest.ResourceRequest<any>):Q.Promise<T[]> {
            throw new Error("To be inherrited by subclass");
        }

        addListener(listener:TemplateSummaryLoaderListener<T>) {
            this.listeners.push(listener);
        }

        removeListener(listenerToRemove:TemplateSummaryLoaderListener<T>) {
            this.listeners = this.listeners.filter((listener) => {
                return listener != listenerToRemove;
            })
        }


        private notifyLoading() {
            this.listeners.forEach((listener:TemplateSummaryLoaderListener<T>) => {
                if (listener.onLoading) {
                    listener.onLoading();
                }
            });
        }

            private notifyLoaded(templateSummary:T[]) {
            this.listeners.forEach((listener:TemplateSummaryLoaderListener<T>) => {
                if (listener.onLoaded) {
                    listener.onLoaded(templateSummary);
                }
            });
        }

    }

}