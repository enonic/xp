module api_content_page_part{

    export class PartTemplate extends api_content_page.Template<PartTemplateKey,PartTemplateName> {

        constructor(builder:PartTemplateBuilder) {
            super(builder);
        }
    }

    export class PartTemplateBuilder extends api_content_page.TemplateBuilder<PartTemplateKey,PartTemplateName> {

        descriptor:PartDescriptor;

        public fromJson(json: api_content_page_part_json.PartTemplateJson): PartTemplateBuilder {
            this.setKey(PartTemplateKey.fromString(json.key));
            this.setName(new PartTemplateName(json.name));
            this.setDisplayName(json.displayName);
            this.setDescriptorModuleResourceKey(api_module.ModuleResourceKey.fromString(json.descriptorModuleResourceKey));
            this.descriptor = new PartDescriptorBuilder().fromJson(json.descriptor).build();
            return this;
        }

        public build(): PartTemplate {
            return new PartTemplate(this);
        }
    }
}