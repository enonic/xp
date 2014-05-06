module api.schema.content {

    export class GetAllContentTypesRequest extends ContentTypeResourceRequest<api.schema.content.json.ContentTypeSummaryListJson> {

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

        sendAndParse(): Q.Promise<api.schema.content.ContentTypeSummary[]> {

            return this.send().then((response: api.rest.JsonResponse<api.schema.content.json.ContentTypeSummaryListJson>) => {
                return response.getResult().contentTypes.map((contentTypeJson:api.schema.content.json.ContentTypeSummaryJson) => {
                    return this.fromJsonToContentTypeSummary(contentTypeJson);
                });
            });
        }
    }
}