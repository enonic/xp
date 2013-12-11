module api_content_page_layout{

    export class LayoutTemplate extends api_content_page.Template<LayoutTemplateKey,LayoutTemplateName> {

        constructor(builder:LayoutTemplateBuilder) {
            super(builder);
        }
    }

    export class LayoutTemplateBuilder extends api_content_page.TemplateBuilder<LayoutTemplateKey,LayoutTemplateName> {

        descriptor:LayoutDescriptor;

        public fromJson(json: api_content_page_layout_json.LayoutTemplateJson): LayoutTemplateBuilder {
            this.setKey(LayoutTemplateKey.fromString(json.key));
            this.setName(new LayoutTemplateName(json.name));
            this.setDisplayName(json.displayName);
            this.setDescriptorModuleResourceKey(api_module.ModuleResourceKey.fromString(json.descriptorModuleResourceKey));
            this.descriptor = new LayoutDescriptorBuilder().fromJson(json.descriptor).build();
            return this;
        }

        public build(): LayoutTemplate {
            return new LayoutTemplate(this);
        }
    }
}