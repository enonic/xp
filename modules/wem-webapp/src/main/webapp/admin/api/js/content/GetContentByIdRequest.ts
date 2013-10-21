module api_content {

    export class GetContentByIdRequest extends ContentResourceRequest<api_content_json.ContentJson> {

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