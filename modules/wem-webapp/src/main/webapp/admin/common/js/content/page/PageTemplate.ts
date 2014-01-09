module api.content.page {

    export class PageTemplate extends Template<PageTemplateKey,PageTemplateName> {

        private regions: region.PageRegions;

        private canRender: api.schema.content.ContentTypeName[];

        private descriptor: PageDescriptor;

        constructor(builder: PageTemplateBuilder) {
            super(builder);
            this.regions = builder.regions;
            this.canRender = builder.canRender;
            this.descriptor = builder.descriptor;
        }

        hasRegions(): boolean {
            return this.regions != null;
        }

        getRegions(): region.PageRegions {
            return this.regions;
        }

        getCanRender(): api.schema.content.ContentTypeName[] {
            return this.canRender;
        }

        getDescriptor(): PageDescriptor {
            return this.descriptor;
        }
    }

    export class PageTemplateBuilder extends TemplateBuilder<PageTemplateKey,PageTemplateName> {

        regions: region.PageRegions;

        canRender: api.schema.content.ContentTypeName[] = [];

        descriptor: PageDescriptor;

        fromJson(json: api.content.page.json.PageTemplateJson): PageTemplateBuilder {

            this.setKey(PageTemplateKey.fromString(json.key));
            this.setName(new PageTemplateName(json.name));
            this.setDisplayName(json.displayName);
            this.setDescriptorKey(DescriptorKey.fromString(json.descriptorKey));
            this.descriptor = new PageDescriptorBuilder().fromJson(json.descriptor).build();
            this.setConfig(api.data.DataFactory.createRootDataSet(json.config));
            this.setRegions(new region.PageRegionsBuilder().fromJson(json.regions).build());
            json.canRender.forEach((name: string)=> {
                this.canRender.push(new api.schema.content.ContentTypeName(name))
            });
            return this;
        }

        public setRegions(value: region.PageRegions): PageTemplateBuilder {
            this.regions = value;
            return this;
        }

        setCanRender(value: api.schema.content.ContentTypeName[]): PageTemplateBuilder {
            this.canRender = value;
            return this;
        }

        public build(): PageTemplate {
            return new PageTemplate(this);
        }
    }
}