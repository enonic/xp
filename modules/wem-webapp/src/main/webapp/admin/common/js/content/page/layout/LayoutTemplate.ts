module api.content.page.layout{

    export class LayoutTemplate extends api.content.page.Template<LayoutTemplateKey,LayoutTemplateName> {

        constructor(builder:LayoutTemplateBuilder) {
            super(builder);
        }
    }

    export class LayoutTemplateBuilder extends api.content.page.TemplateBuilder<LayoutTemplateKey,LayoutTemplateName> {

        descriptor:LayoutDescriptor;

        public fromJson(json: api.content.page.layout.json.LayoutTemplateJson): LayoutTemplateBuilder {
            this.setKey(LayoutTemplateKey.fromString(json.key));
            this.setName(new LayoutTemplateName(json.name));
            this.setDisplayName(json.displayName);
            this.setDescriptorModuleResourceKey(api.module.ModuleResourceKey.fromString(json.descriptorModuleResourceKey));
            this.descriptor = new LayoutDescriptorBuilder().fromJson(json.descriptor).build();
            return this;
        }

        public build(): LayoutTemplate {
            return new LayoutTemplate(this);
        }
    }
}