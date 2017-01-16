module api.content.resource {

    import AccessControlList = api.security.acl.AccessControlList;
    import ContentsPermissionsEntryJson = api.content.json.ContentPermissionsJson;
    import ContentAccessControlList = api.security.acl.ContentAccessControlList;

    export class GetContentPermissionsByIdsRequest
    extends ContentResourceRequest<ContentsPermissionsEntryJson[], ContentAccessControlList[]> {

        private contentIds: ContentId[];

        constructor(contentIds: ContentId[]) {
            super();
            super.setMethod("POST");
            this.contentIds = contentIds;
        }

        getParams(): Object {
            let fn = (contentId: ContentId) => {
                return contentId.toString();
            };
            return {
                contentIds: this.contentIds.map(fn)
            };
        }

        getRequestPath(): api.rest.Path {
            return api.rest.Path.fromParent(super.getResourcePath(), "contentPermissionsByIds");
        }

        sendAndParse(): wemQ.Promise<ContentAccessControlList[]> {

            return this.send().then((response: api.rest.JsonResponse<ContentsPermissionsEntryJson[]>) => {
                let result = [];

                response.getResult().forEach((entry: ContentsPermissionsEntryJson) => {
                    result.push(ContentAccessControlList.fromJson(entry));
                });

                return result;
            });
        }
    }
}
