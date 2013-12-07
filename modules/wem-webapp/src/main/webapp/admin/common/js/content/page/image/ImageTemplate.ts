module api_content_page_image {

    export class ImageTemplate extends ImageTemplateSummary {

        private descriptor:ImageDescriptor;

        constructor(builder: ImageTemplateBuilder) {
            super(builder);
            this.descriptor = builder.descriptor;
        }

        getDescriptor():ImageDescriptor {
            return this.descriptor;
        }
    }

    export class ImageTemplateBuilder extends api_content_page.TemplateSummaryBuilder<ImageTemplateKey,ImageTemplateName> {

        descriptor:ImageDescriptor;

        public fromJson(json: api_content_page_image_json.ImageTemplateJson): ImageTemplateBuilder {
            this.setKey(ImageTemplateKey.fromString(json.key));
            this.setName(new ImageTemplateName(json.name));
            this.setDisplayName(json.displayName);
            this.setDescriptorModuleResourceKey(api_module.ModuleResourceKey.fromString(json.descriptorModuleResourceKey));
            this.descriptor = new ImageDescriptorBuilder().fromJson(json.descriptor).build();
            return this;
        }

        public build(): ImageTemplate {
            return new ImageTemplate(this);
        }
    }
}