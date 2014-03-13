module api.schema.content {

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

        toJson(): Object {
            var json = {
                allow: [],
                deny: []
            };

            this.allow.forEach((name: ContentTypeName) => {
                json.allow.push(name.toString());
            });
            this.deny.forEach((name: ContentTypeName) => {
                json.deny.push(name.toString());
            });

            return json;
        }
    }

    export class ContentTypeFilterBuilder {

        allow: ContentTypeName[] = [];

        deny: ContentTypeName[] = [];

        fromJson(json: api.content.site.template.ContentTypeFilterJson): ContentTypeFilterBuilder {
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