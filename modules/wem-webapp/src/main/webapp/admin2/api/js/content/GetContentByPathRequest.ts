module api_content {

    export class GetContentByPathRequest extends ContentResourceRequest {

        private path:ContentPath;

        constructor(path:ContentPath) {
            super();
            super.setMethod("GET");
            this.path = path;
        }

        getUrl():string {

            var resourceUrl = super.getResourceUrl();
            return resourceUrl + "?path=" + this.path.toString();
        }
    }
}