module api.content {

    import ContentVersionJson = json.ContentVersionJson;

    export class GetActiveContentVersionsRequest extends ContentResourceRequest<json.GetActiveContentVersionsResultsJson, ContentVersion[]> {

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
            return api.rest.Path.fromParent(super.getResourcePath(), 'getActiveVersions');
        }

        sendAndParse(): wemQ.Promise<ContentVersion[]> {

            return this.send().then((response: api.rest.JsonResponse<json.GetActiveContentVersionsResultsJson>) => {
                return this.fromJsonToContentVersions(response.getResult().activeContentVersions);
            });
        }

        private fromJsonToContentVersions(json: {[workspace: string]: ContentVersionJson}): ContentVersion[] {

            var contentVersions: ContentVersion[] = [];
            for (var workspace in json) {
                if (json.hasOwnProperty(workspace)) {
                    contentVersions.push(ContentVersion.fromJson(json[workspace], workspace));
                }
            }

            return contentVersions;
        }

    }
}