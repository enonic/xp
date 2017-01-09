module api.content.resource {

    import GetPublishStatusesResult = api.content.resource.result.GetPublishStatusesResult;
    import GetPublishStatusesResultJson = api.content.json.GetPublishStatusesResultJson;

    export class GetPublishStatusesRequest extends ContentResourceRequest<GetPublishStatusesResultJson, GetPublishStatusesResult> {

        private ids: string[];

        constructor(ids: string[]) {
            super();
            super.setMethod("POST");
            this.ids = ids;
        }

        getParams(): Object {
            return {
                ids: this.ids
            };
        }

        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), "getPublishStatuses");
        }

        sendAndParse(): wemQ.Promise<GetPublishStatusesResult> {
            return this.send().then((response: api.rest.JsonResponse<api.content.json.GetPublishStatusesResultJson>) => {
                return this.fromJsonToGetPublishStatusesResult(response.getResult());
            });
        }

        fromJsonToGetPublishStatusesResult(json: api.content.json.GetPublishStatusesResultJson): GetPublishStatusesResult {
            return GetPublishStatusesResult.fromJson(json);
        }
    }
}