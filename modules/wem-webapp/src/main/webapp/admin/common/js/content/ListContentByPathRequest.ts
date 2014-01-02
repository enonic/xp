module api.content {

    export class ListContentByPathRequest<T> extends ContentResourceRequest<ListContentResult<T>> {

        private parentPath:ContentPath;

        private expand:api.rest.Expand = api.rest.Expand.SUMMARY;

        constructor(parentPath:ContentPath) {
            super();
            super.setMethod("GET");
            this.parentPath = parentPath;
        }

        setExpand(value:api.rest.Expand) {
            this.expand = value;
        }

        getParams():Object {
            return {
                parentPath: this.parentPath.toString(),
                expand: this.expand
            };
        }

        getRequestPath():api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), "list", "bypath");
        }
    }
}