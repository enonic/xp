module api_content_page_part{

    export class PartTemplateSummary extends api_content_page.TemplateSummary<PartTemplateKey,PartTemplateName> {

        constructor(builder:PartTemplateSummaryBuilder) {
            super(builder);
        }
    }

    export class PartTemplateSummaryBuilder extends api_content_page.TemplateSummaryBuilder<PartTemplateKey,PartTemplateName> {

        public build():PartTemplateSummary {
            return new PartTemplateSummary(this);
        }

        static fromJson( json: api_content_page_part_json.PartTemplateSummaryJson ):PartTemplateSummaryBuilder {
            var builder = new PartTemplateSummaryBuilder();
            builder.setKey( PartTemplateKey.fromString( json.key ) );
            builder.setName( new PartTemplateName( json.name ) );
            builder.setDisplayName( json.displayName );
            builder.setDescriptorModuleResourceKey( api_module.ModuleResourceKey.fromString( json.descriptorModuleResourceKey ) );
            return builder;
        }
    }
}