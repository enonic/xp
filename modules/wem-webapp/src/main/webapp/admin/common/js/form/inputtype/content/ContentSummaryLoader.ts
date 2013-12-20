module api_form_inputtype_content {

    export class ContentSummaryLoader implements api_rest.Loader {

        private findContentRequest:api_content.FindContentRequest<api_content_json.ContentSummaryJson>;

        private isLoading:boolean;

        private preservedSearchString:string;

        private listeners:ContentSummaryLoaderListener[] = [];

        private loaderHelper:api_rest.LoaderHelper;

        constructor(delay:number = 500) {
            this.isLoading = false;
            this.findContentRequest = new api_content.FindContentRequest().setExpand("summary").setCount(100);
            this.loaderHelper = new api_rest.LoaderHelper(this.doRequest, this, delay);
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
                .done((jsonResponse:api_rest.JsonResponse<api_content.FindContentResult<api_content_json.ContentSummaryJson>>) => {
                var result = jsonResponse.getResult();

                this.isLoading = false;
                this.notifyLoaded(api_content.ContentSummary.fromJsonArray(result.contents));
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

        private notifyLoaded(contentSummaries:api_content.ContentSummary[]) {
            this.listeners.forEach((listener:ContentSummaryLoaderListener) => {
                if (listener.onLoaded) {
                    listener.onLoaded(contentSummaries);
                }
            });
        }

    }

}