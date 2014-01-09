module api.schema.content {

    export class ContentTypeSummaryLoader implements api.rest.Loader {

        private findContentTypesRequest:FindSchemaRequest;

        private isLoading:boolean;

        private preservedSearchString:string;

        private listeners:ContentTypeSummaryLoaderListener[] = [];

        private loaderHelper:api.rest.LoaderHelper;

        constructor(delay:number = 500) {
            this.isLoading = false;
            this.findContentTypesRequest = new FindSchemaRequest().setTypes(["content_type"]);
            this.loaderHelper = new api.rest.LoaderHelper(this.doRequest, this, delay);
        }


        search(searchString:string) {
            if (this.isLoading) {
                this.preservedSearchString = searchString;
                return;
            }

            this.loaderHelper.search(searchString);
        }

        private doRequest(searchString:string) {
            this.isLoading = true;
            this.notifyLoading();

            this.findContentTypesRequest.send()
                .done((jsonResponse:api.rest.JsonResponse<api.schema.content.json.ContentTypeSummaryListJson>) => {
                var result = jsonResponse.getResult();

                this.isLoading = false;
                this.notifyLoaded(api.schema.content.ContentTypeSummary.fromJsonArray(result.contentTypes));
                if (this.preservedSearchString) {
                    this.search(this.preservedSearchString);
                    this.preservedSearchString = null;
                }
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
            console.log("summary:", contentTypeSummary);
            this.listeners.forEach((listener:ContentTypeSummaryLoaderListener) => {
                if (listener.onLoaded) {
                    listener.onLoaded(contentTypeSummary);
                }
            });
        }

    }

}