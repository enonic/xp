module api.content {

    export class ResolveDependantsRequest extends ContentResourceRequest<api.content.json.ResolveDependantsResultJson, any> {

        private id: string;

        private includeChildren: boolean;

        constructor(contentId: string, includeChildren: boolean) {
            super();
            super.setMethod("POST");
            this.id = contentId;
            this.includeChildren = includeChildren;
        }

        getParams(): Object {
            return {
                id: this.id,
                includeChildren: this.includeChildren
            };
        }

        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), "getDependants");
        }

    }
}
