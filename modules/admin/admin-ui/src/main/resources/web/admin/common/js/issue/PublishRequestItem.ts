module api.issue {

    import PublishRequestItemJson = api.issue.resource.PublishRequestItemJson;

    export class PublishRequestItem {

        private id: ContentId;

        private includeChildren: boolean;

        constructor(builder: PublishRequestItemBuilder) {
            this.id = builder.id;
            this.includeChildren = builder.includeChildren;
        }

        toJson(): PublishRequestItemJson {
            return {
                id: this.id.toString(),
                includeChildren: this.includeChildren
            };
        }

        public static create(): PublishRequestItemBuilder {
            return new PublishRequestItemBuilder();
        }

    }

    export class PublishRequestItemBuilder {

        id: ContentId;

        includeChildren: boolean;

        public setId(id: ContentId): PublishRequestItemBuilder {
            this.id = id;
            return this;
        }

        public setIncludeChildren(includeChildren: boolean): PublishRequestItemBuilder {
            this.includeChildren = includeChildren;
            return this;
        }

        public build(): PublishRequestItem {
            return new PublishRequestItem(this);
        }
    }
}
