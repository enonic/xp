module app.wizard.page {

    import Content = api.content.Content;
    import ContentTypeName = api.schema.content.ContentTypeName;
    import PageTemplate = api.content.page.PageTemplate;
    import PageDescriptor = api.content.page.PageDescriptor;
    import PartDescriptor = api.content.page.region.PartDescriptor;
    import LayoutDescriptor = api.content.page.region.LayoutDescriptor;

    export interface DefaultModelsConfig {

        pageTemplate: PageTemplate;
        
        pageDescriptor: PageDescriptor;

        partDescriptor: PartDescriptor;

        layoutDescriptor: LayoutDescriptor;
    }

    export class DefaultModels {

        private pageTemplate: PageTemplate;
        
        private pageDescriptor: PageDescriptor;

        private partDescriptor: PartDescriptor;

        private layoutDescriptor: LayoutDescriptor;

        constructor(config: DefaultModelsConfig) {
            if (config.pageTemplate) {
                api.util.assert(config.pageTemplate.getType().isPageTemplate(),
                        "given pageTemplate is not a PageTemplate: " + config.pageTemplate.getType().toString());
            }
            this.pageTemplate = config.pageTemplate;
            this.pageDescriptor = config.pageDescriptor;
            this.partDescriptor = config.partDescriptor;
            this.layoutDescriptor = config.layoutDescriptor;
        }

        hasPageTemplate(): boolean {
            return !!this.pageTemplate;
        }

        getPageTemplate(): PageTemplate {
            return this.pageTemplate ? this.pageTemplate.clone() : null;
        }

        getPageDescriptor(): PageDescriptor {
            return this.pageDescriptor;
        }

        getPartDescriptor(): PartDescriptor {
            return this.partDescriptor;
        }

        getLayoutDescriptor(): LayoutDescriptor {
            return this.layoutDescriptor;
        }
    }
}