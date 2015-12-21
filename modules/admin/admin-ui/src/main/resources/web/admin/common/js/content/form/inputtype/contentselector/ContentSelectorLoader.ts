module api.content.form.inputtype.contentselector {

    export class ContentSelectorLoader extends api.util.loader.BaseLoader<json.ContentQueryResultJson<json.ContentSummaryJson>, ContentSummary> {

        private contentSelectorQueryRequest: ContentSelectorQueryRequest;

        constructor(builder: Builder) {
            this.contentSelectorQueryRequest = new ContentSelectorQueryRequest();
            super(this.contentSelectorQueryRequest);
            this.contentSelectorQueryRequest.setId(builder.id);
            this.contentSelectorQueryRequest.setInputName(builder.inputName);
            this.contentSelectorQueryRequest.setContentTypeNames(builder.contentTypeNames);
            this.contentSelectorQueryRequest.setAllowedContentPaths(builder.allowedContentPaths);
            this.contentSelectorQueryRequest.setRelationshipType(builder.relationshipType);
        }

        search(searchString: string): wemQ.Promise<ContentSummary[]> {

            this.contentSelectorQueryRequest.setQueryExpr(searchString);

            return this.load();
        }

        sendRequest(): wemQ.Promise<ContentSummary[]> {
            return this.contentSelectorQueryRequest.sendAndParse();
        }

        public static create(): Builder {
            return new Builder();
        }
    }

    export class Builder {

        constructor() {
        }

        id: ContentId;

        inputName: string;

        contentTypeNames: string[] = [];

        allowedContentPaths: string[] = [];

        relationshipType: string;

        public setId(id: ContentId): Builder {
            this.id = id;
            return this;
        }

        public setInputName(name: string): Builder {
            this.inputName = name;
            return this;
        }

        public setContentTypeNames(contentTypeNames: string[]): Builder {
            this.contentTypeNames = contentTypeNames;
            return this;
        }

        public setAllowedContentPaths(allowedContentPaths: string[]): Builder {
            this.allowedContentPaths = allowedContentPaths;
            return this;
        }

        public setRelationshipType(relationshipType: string): Builder {
            this.relationshipType = relationshipType;
            return this;
        }

        public build(): ContentSelectorLoader {
            return new ContentSelectorLoader(this);
        }
    }
}