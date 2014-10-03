module api.content.page {

    import ContentTypeName = api.schema.content.ContentTypeName;

    export class PageTemplate extends api.content.Content implements api.Equitable {

        constructor(builder: PageTemplateBuilder) {

            super(builder);
        }

        getKey(): PageTemplateKey {

            return <PageTemplateKey>this.getContentId();
        }

        hasRegions(): boolean {
            return this.getPage().hasRegions();
        }

        getRegions(): PageRegions {
            return this.getPage().getRegions();
        }

        getConfig(): api.data.RootDataSet {
            return this.getPage().getConfig();
        }

        getCanRender(): ContentTypeName[] {

            var contentTypeNames: api.schema.content.ContentTypeName[] = [];
            this.getContentData().getPropertiesByName("supports").forEach((property: api.data.Property) => {
                contentTypeNames.push(new ContentTypeName(property.getString()));
            });
            return contentTypeNames;
        }

        getDescriptorKey(): DescriptorKey {

            return DescriptorKey.fromString(this.getContentData().getProperty("controller").getString());
        }

        isCanRender(pattern: ContentTypeName): boolean {
            return this.getCanRender().some((name: ContentTypeName) => {
                return name.equals(pattern);
            });
        }

        equals(o: api.Equitable): boolean {

            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, PageTemplate)) {
                return false;
            }

            return super.equals(o);
        }
    }

    export class PageTemplateBuilder extends api.content.ContentBuilder {

        constructor(source?: PageTemplate) {
            super(source);
        }

        fromContentJson(contentJson: api.content.json.ContentJson): PageTemplateBuilder {
            super.fromContentJson(contentJson);
            return this;
        }


        public build(): PageTemplate {
            return new PageTemplate(this);
        }
    }
}