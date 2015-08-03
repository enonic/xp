module api.schema.content {

    import ApplicationKey = api.application.ApplicationKey;

    export class GetContentTypesByModuleRequest extends ContentTypeResourceRequest<ContentTypeSummaryListJson, ContentTypeSummary[]> {

        private applicationKey: ApplicationKey;

        constructor(applicationKey: ApplicationKey) {
            super();
            super.setMethod("GET");
            this.applicationKey = applicationKey;
        }

        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), "byModule");
        }

        getParams(): Object {
            return {
                applicationKey: this.applicationKey.toString()
            };
        }

        sendAndParse(): wemQ.Promise<ContentTypeSummary[]> {

            return this.send().then((response: api.rest.JsonResponse<ContentTypeSummaryListJson>) => {
                return response.getResult().contentTypes.map((contentTypeJson: ContentTypeSummaryJson) => {
                    return this.fromJsonToContentTypeSummary(contentTypeJson);
                });
            });
        }

    }

}