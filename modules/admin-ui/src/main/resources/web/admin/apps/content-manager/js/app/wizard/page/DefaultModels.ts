module app.wizard.page {

    import PageTemplate = api.content.page.PageTemplate;
    import PageDescriptor = api.content.page.PageDescriptor;

    export class DefaultModels {

        private pageTemplate: PageTemplate;

        private pageDescriptor: PageDescriptor;

        constructor(pageTemplate: PageTemplate, pageDescriptor: PageDescriptor) {
            this.pageTemplate = pageTemplate;
            this.pageDescriptor = pageDescriptor;
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
    }
}