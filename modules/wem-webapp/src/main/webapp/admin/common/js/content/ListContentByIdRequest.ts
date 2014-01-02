module api.content {

    export class ListContentByIdRequest<T> extends ContentResourceRequest<ListContentResult<T>> {

        private parentId:string;

        private expand:api.rest.Expand = api.rest.Expand.SUMMARY;

        constructor(parentId:string) {
            super();
            super.setMethod("GET");
            this.parentId = parentId;
        }

        setExpand(value:api.rest.Expand) {
            this.expand = value;
        }

        getParams():Object {
            return {
                parentId: this.parentId,
                expand: this.expand
            };
        }

        getRequestPath():api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), "list");
        }
    }
}