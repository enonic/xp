module api.content.page.part {

    export class PartTemplate extends api.content.page.Template {

        constructor(builder: PartTemplateBuilder) {
            super(builder);
        }
    }

    export class PartTemplateBuilder extends api.content.page.TemplateBuilder {

        descriptor: PartDescriptor;

        public fromJson(json: api.content.page.part.json.PartTemplateJson): PartTemplateBuilder {
            this.setKey(api.content.page.TemplateKey.fromString(json.key));
            this.setDisplayName(json.displayName);
            this.setDescriptorKey(DescriptorKey.fromString(json.descriptorKey));
            this.descriptor = new PartDescriptorBuilder().fromJson(json.descriptor).build();
            return this;
        }

        public build(): PartTemplate {
            return new PartTemplate(this);
        }
    }
}