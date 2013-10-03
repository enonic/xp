module api_content {

    export class GetContentByIdRequest extends ContentResourceRequest {

        private id:string;

        constructor(id:string) {
            super();
            super.setMethod("GET");
            this.id = id;
        }

        getParams():Object {
            return {
                id: this.id
            };
        }

        getRequestPath():api_rest.Path {
            return super.getResourcePath();
        }
    }
}