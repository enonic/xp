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
            this.setDescriptorModuleResourceKey(api.module.ModuleResourceKey.fromString(json.descriptorModuleResourceKey));
            return this;
        }

        public build(): PageTemplateSummary {
            return new PageTemplateSummary(this);
        }
    }
}