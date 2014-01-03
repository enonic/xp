module api.content.page {

    export class PageTemplate extends Template<PageTemplateKey,PageTemplateName> {

        private canRender: api.schema.content.ContentTypeName[];

        private descriptor: PageDescriptor;

        constructor(builder: PageTemplateBuilder) {
            super(builder);
            this.canRender = builder.canRender;
            this.descriptor = builder.descriptor;
        }

        getCanRender(): api.schema.content.ContentTypeName[] {
            return this.canRender;
        }

        getDescriptor(): PageDescriptor {
            return this.descriptor;
        }
    }

    export class PageTemplateBuilder extends TemplateBuilder<PageTemplateKey,PageTemplateName> {

        canRender: api.schema.content.ContentTypeName[] = [];

        descriptor: PageDescriptor;

        fromJson(json: api.content.page.json.PageTemplateJson): PageTemplateBuilder {

            this.setKey(PageTemplateKey.fromString(json.key));
            this.setName(new PageTemplateName(json.name));
            this.setDisplayName(json.displayName);
            this.setDescriptorKey(api.module.ModuleResourceKey.fromString(json.descriptorKey));
            this.descriptor = new PageDescriptorBuilder().fromJson(json.descriptor).build();
            this.setConfig(api.data.DataFactory.createRootDataSet(json.config));
            json.canRender.forEach((name: string)=> {
                this.canRender.push(new api.schema.content.ContentTypeName(name))
            });
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