module api.content.page {

    import ContentTypeName = api.schema.content.ContentTypeName;
    import Property = api.data2.Property;
    import PropertyTree = api.data2.PropertyTree;
    import PropertyIdProvider = api.data2.PropertyIdProvider;

    export class PageTemplate extends api.content.Content implements api.Equitable {

        private canRender: ContentTypeName[];

        constructor(builder: PageTemplateBuilder) {

            super(builder);

            this.canRender = [];
            this.getContentData().forEachProperty("supports", (property: Property) => {
                this.canRender.push(new ContentTypeName(property.getString()));
            });
        }

        getKey(): PageTemplateKey {

            return <PageTemplateKey>this.getContentId();
        }

        getController(): DescriptorKey {

            return this.getPage().getController();
        }

        isCanRender(pattern: ContentTypeName): boolean {
            return this.getCanRender().some((name: ContentTypeName) => {
                return name.equals(pattern);
            });
        }

        getCanRender(): ContentTypeName[] {

            return this.canRender;
        }

        hasRegions(): boolean {
            return this.getPage().hasRegions();
        }

        getRegions(): PageRegions {
            return this.getPage().getRegions();
        }

        getConfig(): PropertyTree {
            return this.getPage().getConfig();
        }

        equals(o: api.Equitable): boolean {

            if (!api.ObjectHelper.iFrameSafeInstanceOf(o, PageTemplate)) {
                return false;
            }

            return super.equals(o);
        }

        clone(): PageTemplate {

            return this.newBuilder().build();
        }

        newBuilder(): PageTemplateBuilder {
            return new PageTemplateBuilder(this);
        }
    }

    export class PageTemplateBuilder extends api.content.ContentBuilder {

        constructor(source?: PageTemplate) {
            super(source);
        }

        fromContentJson(contentJson: api.content.json.ContentJson, propertyIdProvider: PropertyIdProvider): PageTemplateBuilder {
            super.fromContentJson(contentJson, propertyIdProvider);
            return this;
        }


        public build(): PageTemplate {
            return new PageTemplate(this);
        }
    }
}