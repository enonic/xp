module api_content {

    export class GetContentByIdRequest extends ContentResourceRequest<api_content_json.ContentJson> {

        private id:string;

        private expand:string;

        constructor(id:string) {
            super();
            super.setMethod("GET");
            this.id = id;
        }

        public setExpand(expand:string):GetContentByIdRequest {
            this.expand = expand;
            return this;
        }

        getParams():Object {
            return {
                id: this.id,
                expand: this.expand
            };
        }

        getRequestPath():api_rest.Path {
            return super.getResourcePath();
        }
    }
}