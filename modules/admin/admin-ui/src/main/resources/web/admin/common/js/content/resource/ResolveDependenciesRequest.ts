module api.content.resource {

    import ContentDependencyJson = api.content.json.ContentDependencyJson;

    export class ResolveDependenciesRequest extends ContentResourceRequest<ContentDependencyJson, any> {

        private id: ContentId;

        constructor(contentId: ContentId) {
            super();
            super.setMethod("GET");
            this.id = contentId;
        }

        getParams(): Object {
            return {
                id: this.id.toString()
            };
        }

        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), "getDependencies");
        }
    }
}
