module api_content {

    export class ListContentByIdRequest extends ContentResourceRequest {

        private parentId:string;

        private expand:api_rest.Expand = api_rest.Expand.SUMMARY;

        constructor(parentId:string) {
            super();
            super.setMethod("GET");
            this.parentId = parentId;
        }

        setExpand(value:api_rest.Expand) {
            this.expand = value;
        }

        getParams():Object {
            return {
                parentId: this.parentId,
                expand: this.expand
            };
        }

        getRequestPath():api_rest.Path {
            return api_rest.Path.fromParent(super.getResourcePath(), "list", "bypath");
        }
    }
}