module api.content.page.image {

    export class ImageTemplate extends ImageTemplateSummary {

        private image: api.content.ContentId;

        private descriptor: ImageDescriptor;

        constructor(builder: ImageTemplateBuilder) {
            super(builder);
            this.image = builder.image;
            this.descriptor = builder.descriptor;
        }

        getImage(): api.content.ContentId {
            return this.image;
        }

        getDescriptor(): ImageDescriptor {
            return this.descriptor;
        }
    }

    export class ImageTemplateBuilder extends api.content.page.TemplateSummaryBuilder<ImageTemplateKey,ImageTemplateName> {

        image: api.content.ContentId;

        descriptor: ImageDescriptor;

        public fromJson(json: api.content.page.image.json.ImageTemplateJson): ImageTemplateBuilder {
            this.setKey(ImageTemplateKey.fromString(json.key));
            this.setName(new ImageTemplateName(json.name));
            this.setDisplayName(json.displayName);
            this.setImage(new api.content.ContentId(json.image));
            this.setDescriptorKey(DescriptorKey.fromString(json.descriptorKey));
            this.descriptor = new ImageDescriptorBuilder().fromJson(json.descriptor).build();
            return this;
        }

        public setImage(value: api.content.ContentId): ImageTemplateBuilder {
            this.image = value;
            return this;
        }

        public build(): ImageTemplate {
            return new ImageTemplate(this);
        }
    }
}