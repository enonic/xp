module api_content {

    export class GetContentByPathRequest extends ContentResourceRequest {

        private contentPath:ContentPath;

        constructor(path:ContentPath) {
            super();
            super.setMethod("GET");
            this.contentPath = path;
        }

        getParams():Object {
            return {
                path: this.contentPath.toString()
            };
        }

        getRequestPath():api_rest.Path {
            return super.getResourcePath();
        }
    }
}