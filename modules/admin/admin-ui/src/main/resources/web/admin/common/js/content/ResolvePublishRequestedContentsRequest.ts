module api.content {

    export class ResolvePublishRequestedContentsRequest extends ContentResourceRequest<api.content.json.NewResolvePublishContentJson, any> {

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
            console.log("aa");
            return api.rest.Path.fromParent(super.getResourcePath(), "newResolvePublishContent");
        }
    }
}