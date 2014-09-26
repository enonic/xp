module api.content {

    import ContentVersionJson = json.ContentVersionJson;
    import ActiveContentVersionJson = json.ActiveContentVersionJson;

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

        private fromJsonToContentVersions(json: ActiveContentVersionJson[]): ContentVersion[] {

            var contentVersionJson: ContentVersionJson;
            var contentVersion: ContentVersion;
            var contentVersionsMap: {[id:string]:ContentVersion} = {};

            json.forEach((activeContentVersion: ActiveContentVersionJson) => {

                console.log("ActiveContentVersion: ", activeContentVersion);

                contentVersionJson = activeContentVersion.contentVersion;

                console.log("contentVersionJson: ", activeContentVersion.contentVersion);

                contentVersion = contentVersionsMap[contentVersionJson.id];
                if (!contentVersion) {
                    contentVersion = ContentVersion.fromJson(contentVersionJson, [activeContentVersion.workspace]);
                    contentVersionsMap[contentVersion.id] = contentVersion;
                } else {
                    // just add new workspace if already exists
                    contentVersion.workspaces.push(activeContentVersion.workspace);
                }
            });

            return Object.keys(contentVersionsMap).map(function (key) {
                return contentVersionsMap[key];
            });
        }

    }
}