module api.content.form.inputtype.contentselector {

    export class ContentSelectorLoader extends ContentSummaryPreLoader {

        private contentSelectorQueryRequest: ContentSelectorQueryRequest;

        private isPostLoading: boolean;

        constructor(builder: Builder) {
            this.contentSelectorQueryRequest = new ContentSelectorQueryRequest();
            super(this.contentSelectorQueryRequest);
            this.contentSelectorQueryRequest.setId(builder.id);
            this.contentSelectorQueryRequest.setInputName(builder.inputName);
            this.contentSelectorQueryRequest.setContentTypeNames(builder.contentTypeNames);
            this.contentSelectorQueryRequest.setAllowedContentPaths(builder.allowedContentPaths);
            this.contentSelectorQueryRequest.setRelationshipType(builder.relationshipType);
            this.isPostLoading = false;
        }

        search(searchString: string): wemQ.Promise<ContentSummary[]> {

            this.contentSelectorQueryRequest.setQueryExpr(searchString);
            return this.load();
        }

        sendRequest(): wemQ.Promise<ContentSummary[]> {
            if (!this.isPostLoading) {
                this.contentSelectorQueryRequest.resetParams();
            }
            return this.contentSelectorQueryRequest.sendAndParse();
        }

        postLoad() {
            // already have elements and not more than total
            if (this.contentSelectorQueryRequest.isPartiallyLoaded() && this.isLoaded()) {
                this.isPostLoading = true;
                this.load(true).then(() => {
                    this.isPostLoading = false;
                }).catch(() => {
                    this.isPostLoading = false;
                });
            }
        }

        // delegate the postLoad to ComboBox to trigger it before the end of the list

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