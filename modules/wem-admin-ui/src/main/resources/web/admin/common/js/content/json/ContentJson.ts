module api.content.json {

    export interface ContentJson extends ContentSummaryJson {

        data: api.data.json.DataTypeWrapperJson[];

        metadata: api.content.json.MetadataJson[];

        form: api.form.json.FormJson;

        page: api.content.page.PageJson;

        permissions: api.security.acl.AccessControlEntryJson[];

        inheritedPermissions: api.security.acl.AccessControlEntryJson[];

        inheritPermissions: boolean;
    }
}