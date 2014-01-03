module api.content.page.part {

    export class PartTemplateSummary extends api.content.page.TemplateSummary<PartTemplateKey,PartTemplateName> {

        constructor(builder: PartTemplateSummaryBuilder) {
            super(builder);
        }
    }

    export class PartTemplateSummaryBuilder extends api.content.page.TemplateSummaryBuilder<PartTemplateKey,PartTemplateName> {

        public build(): PartTemplateSummary {
            return new PartTemplateSummary(this);
        }

        static fromJson(json: api.content.page.part.json.PartTemplateSummaryJson): PartTemplateSummaryBuilder {
            var builder = new PartTemplateSummaryBuilder();
            builder.setKey(PartTemplateKey.fromString(json.key));
            builder.setName(new PartTemplateName(json.name));
            builder.setDisplayName(json.displayName);
            builder.setDescriptorKey(api.module.ModuleResourceKey.fromString(json.descriptorKey));
            return builder;
        }
    }
}