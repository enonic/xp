module api.content {

    import ResolvePublishContentResultJson = api.content.json.ResolvePublishContentResultJson;

    export class ResolvePublishDependenciesRequest extends ContentResourceRequest<ResolvePublishContentResultJson, ResolvePublishDependenciesResult> {

        private ids: ContentId[] = [];

        private includeChildren: boolean;

        constructor(contentIds: ContentId[], includeChildren: boolean) {
            super();
            super.setMethod("POST");
            this.ids = contentIds;
            this.includeChildren = includeChildren;
        }

        getParams(): Object {
            return {
                ids: this.ids.map((el) => {
                    return el.toString();
                }),
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
    }
}