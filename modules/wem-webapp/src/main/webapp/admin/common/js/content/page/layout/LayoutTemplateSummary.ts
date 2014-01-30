module api.content.page.layout {

    export class LayoutTemplateSummary extends api.content.page.TemplateSummary {

        constructor(builder: LayoutTemplateSummaryBuilder) {
            super(builder);
        }
    }

    export class LayoutTemplateSummaryBuilder extends api.content.page.TemplateSummaryBuilder {

        public build(): LayoutTemplateSummary {
            return new LayoutTemplateSummary(this);
        }

        static fromJson(json: api.content.page.layout.json.LayoutTemplateSummaryJson): LayoutTemplateSummaryBuilder {
            var builder = new LayoutTemplateSummaryBuilder();
            builder.setKey(api.content.page.TemplateKey.fromString(json.key));
            builder.setDisplayName(json.displayName);
            builder.setDescriptorKey(api.content.page.DescriptorKey.fromString(json.descriptorKey));
            return builder;
        }
    }
}