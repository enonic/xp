module api.schema.content {

    export class ContentTypeSummaryLoader implements api.util.Loader {

        private findContentTypesRequest:GetAllContentTypesRequest;

        private isLoading:boolean;

        private contentTypes:ContentTypeSummary[];

        private listeners:ContentTypeSummaryLoaderListener[] = [];

        constructor() {
            this.isLoading = false;
            this.findContentTypesRequest = new GetAllContentTypesRequest();
            this.doRequest();
        }


        search(searchString:string) {

            if (this.contentTypes) {
                var filtered = this.contentTypes.filter((contentType:ContentTypeSummary) => {
                   return contentType.getContentTypeName().toString().indexOf(searchString.toLowerCase()) != -1;
                });
                this.notifyLoaded(filtered);
            }
        }



        private doRequest() {
            this.isLoading = true;
            this.notifyLoading();

            this.findContentTypesRequest.send()
                .done((jsonResponse:api.rest.JsonResponse<api.schema.content.json.ContentTypeSummaryListJson>) => {
                var result = jsonResponse.getResult();
                this.contentTypes = api.schema.content.ContentTypeSummary.fromJsonArray(result.contentTypes);
                this.isLoading = false;
                this.notifyLoaded(this.contentTypes);
            });
        }

        addListener(listener:ContentTypeSummaryLoaderListener) {
            this.listeners.push(listener);
        }

        removeListener(listenerToRemove:ContentTypeSummaryLoaderListener) {
            this.listeners = this.listeners.filter((listener) => {
                return listener != listenerToRemove;
            })
        }

        private notifyLoading() {
            this.listeners.forEach((listener:ContentTypeSummaryLoaderListener) => {
                if (listener.onLoading) {
                    listener.onLoading();
                }
            });
        }

        private notifyLoaded(contentTypeSummary:api.schema.content.ContentTypeSummary[]) {
            this.listeners.forEach((listener:ContentTypeSummaryLoaderListener) => {
                if (listener.onLoaded) {
                    listener.onLoaded(contentTypeSummary);
                }
            });
        }

    }

}