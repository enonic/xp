module api.content {

    export class ContentVersions {

        private contentVersions: api.content.ContentVersion[] = [];

        constructor(contentVersions: api.content.ContentVersion[]) {
            this.contentVersions = contentVersions;
        }

        static fromJson(json: api.content.json.ContentVersionsJson): ContentVersions {

            var contentVersions: ContentVersion[] = [];

            json.contentVersions.forEach((contentVersionJson: api.content.json.ContentVersionJson) => {
                contentVersions.push(ContentVersion.fromJson(contentVersionJson));
            });

            return new ContentVersions(contentVersions);
        }
    }
}