module api.form.inputtype.content {

    export class ContentSummaryLoader implements api.rest.Loader {

        private findContentRequest:api.content.FindContentRequest<api.content.json.ContentSummaryJson>;

        private isLoading:boolean;

        private preservedSearchString:string;

        private listeners:ContentSummaryLoaderListener[] = [];

        private loaderHelper:api.rest.LoaderHelper;

        constructor(delay:number = 500) {
            this.isLoading = false;
            this.findContentRequest = new api.content.FindContentRequest().setExpand("summary").setCount(100);
            this.loaderHelper = new api.rest.LoaderHelper(this.doRequest, this, delay);
        }

        setCount(count:number) {
            this.findContentRequest.setCount(count);
        }

        setAllowedContentTypes(contentTypes:string[]) {
            this.findContentRequest.setContentTypes(contentTypes);
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

            this.findContentRequest.setFulltext(searchString).send()
                .done((jsonResponse:api.rest.JsonResponse<api.content.FindContentResult<api.content.json.ContentSummaryJson>>) => {
                var result = jsonResponse.getResult();

                this.isLoading = false;
                this.notifyLoaded(api.content.ContentSummary.fromJsonArray(result.contents));
                if (this.preservedSearchString) {
                    this.search(this.preservedSearchString);
                    this.preservedSearchString = null;
                }
            });
        }

        addListener(listener:ContentSummaryLoaderListener) {
            this.listeners.push(listener);
        }

        removeListener(listenerToRemove:ContentSummaryLoaderListener) {
            this.listeners = this.listeners.filter((listener) => {
                return listener != listenerToRemove;
            })
        }

        private notifyLoading() {
            this.listeners.forEach((listener:ContentSummaryLoaderListener) => {
                if (listener.onLoading) {
                    listener.onLoading();
                }
            });
        }

        private notifyLoaded(contentSummaries:api.content.ContentSummary[]) {
            this.listeners.forEach((listener:ContentSummaryLoaderListener) => {
                if (listener.onLoaded) {
                    listener.onLoaded(contentSummaries);
                }
            });
        }

    }

}