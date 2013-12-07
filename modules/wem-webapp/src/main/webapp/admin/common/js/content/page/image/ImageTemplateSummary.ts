module api_content_page_image{

    export class ImageTemplateSummary extends api_content_page.TemplateSummary<ImageTemplateKey,ImageTemplateName> {

        constructor(builder:ImageTemplateSummaryBuilder) {
            super(builder);
        }
    }

    export class ImageTemplateSummaryBuilder extends api_content_page.TemplateSummaryBuilder<ImageTemplateKey,ImageTemplateName> {

        public build():ImageTemplateSummary {
            return new ImageTemplateSummary(this);
        }

        static fromJson( json: api_content_page_image_json.ImageTemplateSummaryJson ):ImageTemplateSummaryBuilder {
            var builder = new ImageTemplateSummaryBuilder();
            builder.setKey( ImageTemplateKey.fromString( json.key ) );
            builder.setName( new ImageTemplateName( json.name ) );
            builder.setDisplayName( json.displayName );
            builder.setDescriptorModuleResourceKey( api_module.ModuleResourceKey.fromString( json.descriptorModuleResourceKey ) );
            return builder;
        }
    }
}