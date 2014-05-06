module api.content {

    export class GetContentByIdRequest extends ContentResourceRequest<api.content.json.ContentJson> {

        private id:ContentId;

        private expand:string;

        constructor(id:ContentId) {
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
                id: this.id.toString(),
                expand: this.expand
            };
        }

        getRequestPath():api.rest.Path {
            return super.getResourcePath();
        }

        sendAndParse(): Q.Promise<api.content.Content> {

            return this.send().then((response: api.rest.JsonResponse<api.content.json.ContentJson>) => {
                return this.fromJsonToContent(response.getResult());
            });
        }
    }
}