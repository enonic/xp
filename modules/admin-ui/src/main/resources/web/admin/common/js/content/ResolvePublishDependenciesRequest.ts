module api.content {

    export class ResolvePublishDependenciesRequest extends ContentResourceRequest<ResolvePublishDependenciesResult, any> {

        private ids: ContentId[] = [];

        constructor(contentIds: ContentId[]) {
            super();
            super.setMethod("POST");
            this.ids = contentIds;
        }

        getParams(): Object {
            return {
                ids: this.ids.map((el) => {
                    return el.toString();
                })
            };
        }

        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), "resolvePublishDependencies");
        }
    }
}
