module api.content.page.layout {

    export class LayoutTemplate extends api.content.page.Template {

        constructor(builder: LayoutTemplateBuilder) {
            super(builder);
        }
    }

    export class LayoutTemplateBuilder extends api.content.page.TemplateBuilder {

        descriptor: LayoutDescriptor;

        public fromJson(json: api.content.page.layout.json.LayoutTemplateJson): LayoutTemplateBuilder {
            this.setKey(api.content.page.TemplateKey.fromString(json.key));
            this.setDisplayName(json.displayName);
            this.setDescriptorKey(DescriptorKey.fromString(json.descriptorKey));
            this.descriptor = new LayoutDescriptorBuilder().fromJson(json.descriptor).build();
            return this;
        }

        public build(): LayoutTemplate {
            return new LayoutTemplate(this);
        }
    }
}