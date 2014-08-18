module api.content {

    export class ContentVersion {

        modifier: string;

        displayName: string;

        modified: Date;

        comment: string;

        static fromJson(contentVersionJson: api.content.json.ContentVersionJson): ContentVersion {

            var contentVersion: ContentVersion = new ContentVersion();
            contentVersion.modifier = contentVersionJson.modifier;
            contentVersion.displayName = contentVersionJson.displayName;
            contentVersion.modified = contentVersionJson.modified;
            contentVersion.comment = contentVersionJson.comment;

            return contentVersion;
        }
    }

}