module api.content.page.image{

    export class ImageTemplateSummary extends api.content.page.TemplateSummary<ImageTemplateKey,ImageTemplateName> {

        constructor(builder:ImageTemplateSummaryBuilder) {
            super(builder);
        }
    }

    export class ImageTemplateSummaryBuilder extends api.content.page.TemplateSummaryBuilder<ImageTemplateKey,ImageTemplateName> {

        public build():ImageTemplateSummary {
            return new ImageTemplateSummary(this);
        }

        static fromJson( json: api.content.page.image.json.ImageTemplateSummaryJson ):ImageTemplateSummaryBuilder {
            var builder = new ImageTemplateSummaryBuilder();
            builder.setKey( ImageTemplateKey.fromString( json.key ) );
            builder.setName( new ImageTemplateName( json.name ) );
            builder.setDisplayName( json.displayName );
            builder.setDescriptorModuleResourceKey( api.module.ModuleResourceKey.fromString( json.descriptorModuleResourceKey ) );
            return builder;
        }
    }
}