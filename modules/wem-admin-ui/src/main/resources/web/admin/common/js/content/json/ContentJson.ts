module api.content.json {

    export interface ContentJson extends ContentSummaryJson {

        data: api.data.PropertyArrayJson[];

        attachments: api.content.attachment.AttachmentJson[];

        metadata: api.content.json.MetadataJson[];

        page: api.content.page.PageJson;

        permissions: api.security.acl.AccessControlEntryJson[];

        inheritPermissions: boolean;
    }
}