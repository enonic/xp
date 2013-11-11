module api_content {

    export class ListContentByPathRequest<T> extends ContentResourceRequest<ListContentResult<T>> {

        private parentPath:ContentPath;

        private expand:api_rest.Expand = api_rest.Expand.SUMMARY;

        constructor(parentPath:ContentPath) {
            super();
            super.setMethod("GET");
            this.parentPath = parentPath;
        }

        setExpand(value:api_rest.Expand) {
            this.expand = value;
        }

        getParams():Object {
            return {
                parentPath: this.parentPath.toString(),
                expand: this.expand
            };
        }

        getRequestPath():api_rest.Path {
            return api_rest.Path.fromParent(super.getResourcePath(), "list", "bypath");
        }
    }
}