module api_content {

    export class CreateContentRequest extends ContentResourceRequest {

        constructor(params:api_remote_content.CreateOrUpdateParams) {
            super();
            super.setMethod("POST");
            super.setParams(params);
        }

        getUrl() {
            return super.getResourceUrl() + "/create";
        }
    }
}