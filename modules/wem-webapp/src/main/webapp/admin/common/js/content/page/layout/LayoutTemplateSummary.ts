module api_content_page_layout{

    export class LayoutTemplateSummary extends api_content_page.TemplateSummary<LayoutTemplateKey,LayoutTemplateName> {

        constructor(builder:LayoutTemplateSummaryBuilder) {
            super(builder);
        }
    }

    export class LayoutTemplateSummaryBuilder extends api_content_page.TemplateSummaryBuilder<LayoutTemplateKey,LayoutTemplateName> {

        public build():LayoutTemplateSummary {
            return new LayoutTemplateSummary(this);
        }

        static fromJson( json: api_content_page_layout_json.LayoutTemplateSummaryJson ):LayoutTemplateSummaryBuilder {
            var builder = new LayoutTemplateSummaryBuilder();
            builder.setKey( LayoutTemplateKey.fromString( json.key ) );
            builder.setName( new LayoutTemplateName( json.name ) );
            builder.setDisplayName( json.displayName );
            builder.setDescriptor( api_module.ModuleResourceKey.fromString( json.descriptor ) );
            return builder;
        }
    }
}