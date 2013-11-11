module api_form_inputtype_content {

    export class ContentSummaryLoader implements api_event.Observable {

        private findContentRequest:api_content.FindContentRequest<api_content_json.ContentSummaryJson>;

        private delay:number;

        private timerId:number;

        private isLoading:boolean;

        private preservedSearchString:string;

        private listeners:ContentSummaryLoaderListener[] = [];

        constructor(delay:number = 500) {
            this.delay = delay;
            this.isLoading = false;
            this.findContentRequest = new api_content.FindContentRequest().setExpand("summary").setCount(100);
        }

        setAllowedContentTypes(contentTypes:string[]) {
            this.findContentRequest.setContentTypes(contentTypes);
        }

        search(searchString:string) {
            if (this.isLoading) {
                this.preservedSearchString = searchString;
                return;
            }

            if (this.timerId) {
                window.clearTimeout(this.timerId);
            }
            this.timerId = window.setTimeout(() => {
                this.doRequest(searchString);
                this.timerId = null;
            }, this.delay);
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
            })
            ;
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