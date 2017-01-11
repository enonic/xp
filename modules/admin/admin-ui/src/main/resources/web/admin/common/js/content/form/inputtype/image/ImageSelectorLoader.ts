module api.content.form.inputtype.image {

    import ContentSummaryPreLoader = api.content.resource.ContentSummaryPreLoader;
    import ContentSelectorQueryRequest = api.content.resource.ContentSelectorQueryRequest;

    export class ImageSelectorLoader extends ContentSummaryPreLoader {

        private imageSelectorQueryRequest: ContentSelectorQueryRequest;

        constructor(builder: Builder) {
            let imageSelectorQueryRequest = new ContentSelectorQueryRequest();

            super(imageSelectorQueryRequest);

            this.imageSelectorQueryRequest = imageSelectorQueryRequest;
            this.imageSelectorQueryRequest.setContent(builder.content);
            this.imageSelectorQueryRequest.setInputName(builder.inputName);
            this.imageSelectorQueryRequest.setContentTypeNames(builder.contentTypeNames);
            this.imageSelectorQueryRequest.setAllowedContentPaths(builder.allowedContentPaths);
            this.imageSelectorQueryRequest.setRelationshipType(builder.relationshipType);
        }

        search(searchString: string): wemQ.Promise<ContentSummary[]> {

            this.imageSelectorQueryRequest.setQueryExpr(searchString);
            return this.load();
        }

        isPartiallyLoaded(): boolean {
            return this.imageSelectorQueryRequest.isPartiallyLoaded();
        }

        resetParams() {
            this.imageSelectorQueryRequest.resetParams();
        }

        // delegate the postLoad to ComboBox to trigger it before the end of the list

        public static create(): Builder {
            return new Builder();
        }
    }

    export class Builder {

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

        public build(): ImageSelectorLoader {
            return new ImageSelectorLoader(this);
        }
    }
}
