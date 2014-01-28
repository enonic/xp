module api.content.site.template {

    import ContentTypeName = api.schema.content.ContentTypeName;

    export class ContentTypeFilter {

        private allow: ContentTypeName[];

        private deny: ContentTypeName[];

        constructor(builder: ContentTypeFilterBuilder) {
            this.allow = builder.allow;
            this.deny = builder.deny;
        }

        getAllow(): ContentTypeName[] {
            return this.allow;
        }

        getDeny(): ContentTypeName[] {
            return this.deny;
        }
    }

    export class ContentTypeFilterBuilder {

        allow: ContentTypeName[] = [];

        deny: ContentTypeName[] = [];

        fromJson(json: json.ContentTypeFilterJson): ContentTypeFilterBuilder {
            json.allow.forEach((name: string) => {
                this.allow.push(new ContentTypeName(name));
            });
            json.deny.forEach((name: string) => {
                this.deny.push(new ContentTypeName(name));
            });
            return this;
        }

        public build(): ContentTypeFilter {
            return new ContentTypeFilter(this);
        }
    }
}