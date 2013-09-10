module api_content {

    export class GetContentByIdRequest extends ContentResourceRequest {

        constructor(id:string) {
            super();
            super.setMethod("GET");
            super.setParams({
                contentIds: [id]
            });
        }

        getUrl():string {
            return super.getResourceUrl();
        }
    }
}