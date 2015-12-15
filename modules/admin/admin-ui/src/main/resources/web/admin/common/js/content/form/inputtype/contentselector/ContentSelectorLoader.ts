module api.content.form.inputtype.contentselector {

    export class ContentSelectorLoader extends api.util.loader.BaseLoader<json.ContentQueryResultJson<json.ContentSummaryJson>, ContentSummary> {

        private contentSelectorQueryRequest: ContentSelectorQueryRequest;

        constructor(contentId: api.content.ContentId, inputName: string) {
            this.contentSelectorQueryRequest = new ContentSelectorQueryRequest();
            super(this.contentSelectorQueryRequest);
            this.contentSelectorQueryRequest.setId(contentId);
            this.contentSelectorQueryRequest.setInputName(inputName);
        }

        search(searchString: string): wemQ.Promise<ContentSummary[]> {

            this.contentSelectorQueryRequest.setQueryExpr(searchString);

            return this.load();
        }

        setContentTypeNames(contentTypeNames: string[]) {
            this.contentSelectorQueryRequest.setContentTypeNames(contentTypeNames);
        }

        sendRequest(): wemQ.Promise<ContentSummary[]> {
            return this.contentSelectorQueryRequest.sendAndParse();
        }

    }
}