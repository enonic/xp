module api.content {

    export class SetActiveContentVersionRequest extends ContentResourceRequest<any, any> {

        private versionId: string;

        private contentId: ContentId;

        constructor(versionId: string, contentId: ContentId) {
            super();
            super.setMethod("POST");
            this.versionId = versionId;
            this.contentId = contentId;
        }

        getParams(): Object {
            return {
                versionId: this.versionId,
                contentId: this.contentId.toString()
            };
        }

        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), 'setActiveVersion');
        }

        sendAndParse(): wemQ.Promise<ContentId> {

            return this.send().then((response: api.rest.JsonResponse<any>) => {
                return new ContentId(response.getResult()["id"]);
            });
        }
    }
}