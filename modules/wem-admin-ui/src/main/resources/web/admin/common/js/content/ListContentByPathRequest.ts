module api.content {

    export class ListContentByPathRequest<T> extends ContentResourceRequest<ListContentResult<T>, any> {

        private parentPath:ContentPath;

        private expand:api.rest.Expand = api.rest.Expand.SUMMARY;

        private from: number;

        private size: number;

        constructor(parentPath:ContentPath) {
            super();
            super.setMethod("GET");
            this.parentPath = parentPath;
        }

        setExpand(value:api.rest.Expand) {
            this.expand = value;
        }

        setFrom(value: number) {
            this.from = value;
        }

        setSize(value: number) {
            this.size = value;
        }

        getParams():Object {
            return {
                parentPath: this.parentPath.toString(),
                expand: this.expand,
                from: this.from,
                size: this.size
            };
        }

        getRequestPath():api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), "list", "bypath");
        }
    }
}