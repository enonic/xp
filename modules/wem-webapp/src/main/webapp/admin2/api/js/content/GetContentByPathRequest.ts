module api_content {

    export class GetContentByPathRequest extends ContentResourceRequest {

        constructor(path:ContentPath) {
            super();
            super.setMethod("GET");
            super.setParams({
                path: path
            });
        }

        getUrl():string {
            return super.getResourceUrl();
        }
    }
}