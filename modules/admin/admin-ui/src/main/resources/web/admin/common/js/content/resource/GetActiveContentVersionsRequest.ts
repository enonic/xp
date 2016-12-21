module api.content.resource {

    import ActiveContentVersionJson = api.content.json.ActiveContentVersionJson;
    import ContentVersionJson = api.content.json.ContentVersionJson;
    import GetActiveContentVersionsResultsJson = api.content.json.GetActiveContentVersionsResultsJson;

    export class GetActiveContentVersionsRequest extends ContentResourceRequest<GetActiveContentVersionsResultsJson, ContentVersion[]> {

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

            return this.send().then((response: api.rest.JsonResponse<GetActiveContentVersionsResultsJson>) => {
                return this.fromJsonToContentVersions(response.getResult().activeContentVersions);
            });
        }

        private fromJsonToContentVersions(json: ActiveContentVersionJson[]): ContentVersion[] {

            var contentVersionJson: ContentVersionJson;
            var contentVersion: ContentVersion;
            var contentVersionsMap: {[id: string]: ContentVersion} = {};

            json.forEach((activeContentVersion: ActiveContentVersionJson) => {

                contentVersionJson = activeContentVersion.contentVersion;

                contentVersion = contentVersionsMap[contentVersionJson.id];
                if (!contentVersion) {
                    contentVersion = ContentVersion.fromJson(contentVersionJson, [activeContentVersion.branch]);
                    contentVersionsMap[contentVersion.id] = contentVersion;
                } else {
                    // just add new workspace if already exists
                    contentVersion.workspaces.push(activeContentVersion.branch);
                }
            });

            return Object.keys(contentVersionsMap).map(function (key) {
                return contentVersionsMap[key];
            });
        }

    }
}