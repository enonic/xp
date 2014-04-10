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

            var deferred = Q.defer<api.schema.content.ContentTypeSummary[]>();

            this.send().
                then((response: api.rest.JsonResponse<api.schema.content.json.ContentTypeSummaryListJson>) => {

                    var array:api.schema.content.ContentTypeSummary[] = [];
                    response.getResult().contentTypes.forEach((contentTypeJson:api.schema.content.json.ContentTypeSummaryJson) => {
                        array.push(this.fromJsonToContentTypeSummary(contentTypeJson));
                    });
                    deferred.resolve(array);
                }).catch((reason: api.rest.RequestError) => {
                    deferred.reject(reason);
                }).done();

            return deferred.promise;
        }
    }
}