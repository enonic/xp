module api.content.form.inputtype.contentselector {

    /**
     * Extends ContentSummaryLoader to restrict requests before allowed content types are set.
     * If search() method was called before allowed content types are set
     * then search string is preserved and request postponed.
     * After content types are set, search request is made with latest preserved search string.
     */
    export class ContentSelectorLoader extends api.util.loader.BaseLoader<json.ContentQueryResultJson<json.ContentSummaryJson>, ContentSummary> {

        private postponedSearchString: string;

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

        sendRequest(): wemQ.Promise<ContentSummary[]> {
            return this.contentSelectorQueryRequest.sendAndParse();
        }

    }
}