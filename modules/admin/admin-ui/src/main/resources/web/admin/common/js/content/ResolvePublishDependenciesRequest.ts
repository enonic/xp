module api.content {

    import ResolvePublishContentResultJson = api.content.json.ResolvePublishContentResultJson;

    export class ResolvePublishDependenciesRequest extends ContentResourceRequest<ResolvePublishContentResultJson, ResolvePublishDependenciesResult> {

        private ids: ContentId[] = [];

        private excludedIds: ContentId[] = [];

        private includeChildren: boolean;

        private from: number;

        private size: number;

        constructor(builder: ResolvePublishDependenciesRequestBuilder) {
            super();
            super.setMethod("POST");
            this.ids = builder.ids;
            this.excludedIds = builder.excludedIds;
            this.includeChildren = builder.includeChildren;
            this.from = builder.from;
            this.size = builder.size;
        }

        getParams(): Object {
            return {
                ids: this.ids.map((el) => {
                    return el.toString();
                }),
                excludedIds: this.excludedIds.map((el) => {
                    return el.toString();
                }),
                from: this.from || 0,
                size: this.size || 20,
                includeChildren: this.includeChildren
            };
        }

        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), "resolvePublishContent");
        }

        sendAndParse(): wemQ.Promise<ResolvePublishDependenciesResult> {

            return this.send().then((response: api.rest.JsonResponse<ResolvePublishContentResultJson>) => {
                return ResolvePublishDependenciesResult.fromJson(response.getResult());
            });
        }

        static create() {
            return new ResolvePublishDependenciesRequestBuilder();
        }
    }

    export class ResolvePublishDependenciesRequestBuilder {

        ids: ContentId[] = [];

        excludedIds: ContentId[] = [];

        includeChildren: boolean;

        from: number;

        size: number;


        public setIds(value: ContentId[]): ResolvePublishDependenciesRequestBuilder {
            this.ids = value;
            return this;
        }

        public setExcludedIds(value: ContentId[]): ResolvePublishDependenciesRequestBuilder {
            this.excludedIds = value;
            return this;
        }

        public setIncludeChildren(value: boolean): ResolvePublishDependenciesRequestBuilder {
            this.includeChildren = value;
            return this;
        }

        public setFrom(value: number): ResolvePublishDependenciesRequestBuilder {
            this.from = value;
            return this;
        }

        public setSize(value: number): ResolvePublishDependenciesRequestBuilder {
            this.size = value;
            return this;
        }

        build() {
            return new ResolvePublishDependenciesRequest(this);
        }
    }
}