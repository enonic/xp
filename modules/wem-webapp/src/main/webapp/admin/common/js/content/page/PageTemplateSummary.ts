module api.content.page {

    export class PageTemplateSummary extends TemplateSummary<PageTemplateKey,PageTemplateName> {

        constructor(builder: PageTemplateSummaryBuilder) {
            super(builder);
        }
    }

    export class PageTemplateSummaryBuilder extends TemplateSummaryBuilder<PageTemplateKey,PageTemplateName> {

        fromJson(json: api.content.page.json.PageTemplateSummaryJson): PageTemplateSummaryBuilder {

            this.setKey(PageTemplateKey.fromString(json.key));
            this.setName(new PageTemplateName(json.name));
            this.setDisplayName(json.displayName);
            this.setDescriptorKey(DescriptorKey.fromString(json.descriptorKey));
            return this;
        }

        public build(): PageTemplateSummary {
            return new PageTemplateSummary(this);
        }
    }
}