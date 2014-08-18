module api.schema.content {

    export class GetAllContentTypesRequest extends ContentTypeResourceRequest<json.ContentTypeSummaryListJson, ContentTypeSummary[]> {

        private mixinReferencesToFormItems:boolean = true;

        constructor() {
            super();
            super.setMethod("GET");
        }

        setMixinReferencesToFormItems(value:boolean):GetAllContentTypesRequest {
            this.mixinReferencesToFormItems = value;
            return this;
        }

        getParams():Object {
            return {
                mixinReferencesToFormItems: this.mixinReferencesToFormItems
            };
        }

        getRequestPath():api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), "all");
        }

        sendAndParse(): Q.Promise<ContentTypeSummary[]> {

            return this.send().then((response: api.rest.JsonResponse<json.ContentTypeSummaryListJson>) => {
                return response.getResult().contentTypes.map((contentTypeJson:json.ContentTypeSummaryJson) => {
                    return this.fromJsonToContentTypeSummary(contentTypeJson);
                });
            });
        }
    }
}