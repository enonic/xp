module api.content.form.inputtype.contentselector {

    import ContentSummaryPreLoader = api.content.resource.ContentSummaryPreLoader;
    import ContentSelectorQueryRequest = api.content.resource.ContentSelectorQueryRequest;

    export class ContentSelectorLoader extends ContentSummaryPreLoader {

        protected request: ContentSelectorQueryRequest;
        
        constructor(builder: Builder) {
            super();

            this.initRequest(builder);
        }

        protected createRequest(): ContentSelectorQueryRequest {
            return new ContentSelectorQueryRequest();
        }

        private initRequest(builder: Builder) {
            let request = this.getRequest();
            request.setContent(builder.content);
            request.setInputName(builder.inputName);
            request.setContentTypeNames(builder.contentTypeNames);
            request.setAllowedContentPaths(builder.allowedContentPaths);
            request.setRelationshipType(builder.relationshipType);
        }

        protected getRequest(): ContentSelectorQueryRequest {
            return this.request;
        }
        
        search(searchString: string): wemQ.Promise<ContentSummary[]> {

            this.getRequest().setQueryExpr(searchString);
            return this.load();
        }

        isPartiallyLoaded(): boolean {
            return this.getRequest().isPartiallyLoaded();
        }

        resetParams() {
            this.getRequest().resetParams();
        }

        // delegate the postLoad to ComboBox to trigger it before the end of the list

        public static create(): Builder {
            return new Builder();
        }
    }

    export class Builder {

        constructor() {
        }

        content: ContentSummary;

        inputName: string;

        contentTypeNames: string[] = [];

        allowedContentPaths: string[] = [];

        relationshipType: string;

        public setContent(content: ContentSummary): Builder {
            this.content = content;
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