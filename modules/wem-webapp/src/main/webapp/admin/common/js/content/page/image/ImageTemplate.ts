module api.content.page.image {

    export class ImageTemplate extends ImageTemplateSummary {

        private descriptor: ImageDescriptor;

        constructor(builder: ImageTemplateBuilder) {
            super(builder);
            this.descriptor = builder.descriptor;
        }

        getDescriptor(): ImageDescriptor {
            return this.descriptor;
        }
    }

    export class ImageTemplateBuilder extends api.content.page.TemplateSummaryBuilder<ImageTemplateKey,ImageTemplateName> {

        descriptor: ImageDescriptor;

        public fromJson(json: api.content.page.image.json.ImageTemplateJson): ImageTemplateBuilder {
            this.setKey(ImageTemplateKey.fromString(json.key));
            this.setName(new ImageTemplateName(json.name));
            this.setDisplayName(json.displayName);
            this.setDescriptorKey(api.module.ModuleResourceKey.fromString(json.descriptorKey));
            this.descriptor = new ImageDescriptorBuilder().fromJson(json.descriptor).build();
            return this;
        }

        public build(): ImageTemplate {
            return new ImageTemplate(this);
        }
    }
}