module api.content.resource {

    import ResolvePublishContentResultJson = api.content.json.ResolvePublishContentResultJson;
    import ResolvePublishDependenciesResult = api.content.resource.result.ResolvePublishDependenciesResult;

    export class ResolvePublishDependenciesRequest
            extends ContentResourceRequest<ResolvePublishContentResultJson, ResolvePublishDependenciesResult> {

        private ids: ContentId[] = [];

        private excludedIds: ContentId[] = [];

        private excludeChildrenIds: ContentId[] = [];

        private includeOffline: boolean;

        constructor(builder: ResolvePublishDependenciesRequestBuilder) {
            super();
            super.setMethod('POST');
            this.ids = builder.ids;
            this.excludedIds = builder.excludedIds;
            this.excludeChildrenIds = builder.excludeChildrenIds;
            this.includeOffline = builder.includeOffline;
        }

        getParams(): Object {
            return {
                ids: this.ids.map((el) => {
                    return el.toString();
                }),
                excludedIds: this.excludedIds.map((el) => {
                    return el.toString();
                }),
                excludeChildrenIds: this.excludeChildrenIds.map((el) => {
                    return el.toString();
                }),
                includeOffline: this.includeOffline
            };
        }

        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), 'resolvePublishContent');
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

        excludeChildrenIds: ContentId[] = [];

        includeOffline: boolean = false;

        public setIds(value: ContentId[]): ResolvePublishDependenciesRequestBuilder {
            this.ids = value;
            return this;
        }

        public setExcludedIds(value: ContentId[]): ResolvePublishDependenciesRequestBuilder {
            this.excludedIds = value;
            return this;
        }

        public setExcludeChildrenIds(value: ContentId[]): ResolvePublishDependenciesRequestBuilder {
            this.excludeChildrenIds = value;
            return this;
        }

        public setIncludeOffline(value: boolean): ResolvePublishDependenciesRequestBuilder {
            this.includeOffline = value;
            return this;
        }

        build() {
            return new ResolvePublishDependenciesRequest(this);
        }
    }
}
