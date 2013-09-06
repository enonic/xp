module api_content {

    export class GetContentByIdRequest extends ContentResourceRequest {

        private id:string;

        constructor(id:string) {
            super();
            super.setMethod("GET");
            this.id = id;
        }

        getUrl():string {

            var resourceUrl = super.getResourceUrl();
            return resourceUrl + "?contentIds=" + this.id;
        }
    }
}