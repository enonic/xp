module api.content {

    export class CompareContentRequest extends ContentResourceRequest<CompareContentResultJson> {

        private id: ContentId;

        constructor(id: ContentId) {
            super();
            super.setMethod("GET");
            this.id = id;
        }

        getParams(): Object {
            return {
                id: this.id.toString()
            };
        }

        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), "compare");
        }

        sendAndParse(): Q.Promise<CompareContentResult> {
            return this.send().then((response: api.rest.JsonResponse<CompareContentResultJson>) => {
                return this.fromJsonToCompareRequest(response.getResult());
            });
        }

        fromJsonToCompareRequest(json: CompareContentResultJson): CompareContentResult {
            return CompareContentResult.fromJson(json);
        }
    }
}