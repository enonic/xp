module api_content_page {

    export class PageTemplateSummary extends TemplateSummary<PageTemplateKey,PageTemplateName> {

        constructor(builder: PageTemplateSummaryBuilder) {
            super(builder);
        }
    }

    export class PageTemplateSummaryBuilder extends TemplateSummaryBuilder<PageTemplateKey,PageTemplateName> {

        fromJson(json: api_content_page_json.PageTemplateSummaryJson): PageTemplateSummaryBuilder {

            this.setKey(PageTemplateKey.fromString(json.key));
            this.setName(new PageTemplateName(json.name));
            this.setDisplayName(json.displayName);
            this.setDescriptorModuleResourceKey(api_module.ModuleResourceKey.fromString(json.descriptorModuleResourceKey));
            return this;
        }

        public build(): PageTemplateSummary {
            return new PageTemplateSummary(this);
        }
    }
}