module api.content.page.image {

    export class ImageTemplateSummary extends api.content.page.TemplateSummary {

        constructor(builder: ImageTemplateSummaryBuilder) {
            super(builder);
        }
    }

    export class ImageTemplateSummaryBuilder extends api.content.page.TemplateSummaryBuilder {

        public build(): ImageTemplateSummary {
            return new ImageTemplateSummary(this);
        }

        static fromJson(json: api.content.page.image.json.ImageTemplateSummaryJson): ImageTemplateSummaryBuilder {
            var builder = new ImageTemplateSummaryBuilder();
            builder.setKey(api.content.page.TemplateKey.fromString(json.key));
            builder.setDisplayName(json.displayName);
            builder.setDescriptorKey(api.content.page.DescriptorKey.fromString(json.descriptorKey));
            return builder;
        }
    }
}