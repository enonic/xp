module api.content {

    export class PublishContentRequest  extends ContentResourceRequest<api.content.json.ContentJson, Content> {

        private id: string;

        constructor(id: string) {
            super();
            this.id = id;
            this.setMethod("POST");
        }

        getParams(): Object {
            return {
                contentId: this.id
            };
        }

        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), "publish");
        }

        sendAndParse(): Q.Promise<Content> {
            return this.send().then((response: api.rest.JsonResponse<api.content.json.ContentJson>) => {
                return this.fromJsonToContent(response.getResult());
            });
        }
    }
}
