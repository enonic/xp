module api_content_page_image{

    export class ImageTemplate extends ImageTemplateSummary {

        constructor(builder:ImageTemplateBuilder) {
            super(builder);
        }
    }

    export class ImageTemplateBuilder extends api_content_page.TemplateSummaryBuilder<ImageTemplateKey,ImageTemplateName> {

        public build():ImageTemplate {
            return new ImageTemplateSummary(this);
        }

        static fromJson( json: api_content_page_image_json.ImageTemplateSummaryJson ):ImageTemplateSummary {
            var builder = new ImageTemplateBuilder();
            builder.setKey( ImageTemplateKey.fromString( json.key ) );
            builder.setName( new ImageTemplateName( json.name ) );
            builder.setDisplayName( json.displayName );
            builder.setDescriptor( new api_module.ModuleResourceKey( json.descriptor ) );
            return builder.build();
        }
    }
}