module api.content {

    export class ContentVersion {

        modifier: string;

        displayName: string;

        modified: Date;

        comment: string;

        id: string;

        workspace: string;

        static fromJson(contentVersionJson: api.content.json.ContentVersionJson, workspace?: string): ContentVersion {

            var contentVersion: ContentVersion = new ContentVersion();
            contentVersion.modifier = contentVersionJson.modifier;
            contentVersion.displayName = contentVersionJson.displayName;
            contentVersion.modified = new Date(contentVersionJson.modified);
            contentVersion.comment = contentVersionJson.comment;
            contentVersion.id = contentVersionJson.id;
            contentVersion.workspace = workspace;

            return contentVersion;
        }
    }

}