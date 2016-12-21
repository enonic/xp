module api.content.resource {

    import ContentJson = api.content.json.ContentJson;

    export class GetContentByIdRequest extends ContentResourceRequest<ContentJson, Content> {

        private id: ContentId;

        private expand: string;

        constructor(id: ContentId) {
            super();
            super.setMethod("GET");
            this.id = id;
        }

        public setExpand(expand: string): GetContentByIdRequest {
            this.expand = expand;
            return this;
        }

        getParams(): Object {
            return {
                id: this.id.toString(),
                expand: this.expand
            };
        }

        getRequestPath(): api.rest.Path {
            return super.getResourcePath();
        }

        sendAndParse(): wemQ.Promise<Content> {

            return this.send().then((response: api.rest.JsonResponse<ContentJson>) => {
                return this.fromJsonToContent(response.getResult());
            });
        }
    }
}