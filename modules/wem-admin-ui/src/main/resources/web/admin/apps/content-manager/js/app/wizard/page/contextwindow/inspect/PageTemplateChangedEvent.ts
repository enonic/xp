module app.wizard.page.contextwindow.inspect {

    export class PageTemplateChangedEvent {

        private pageTemplate: api.content.page.PageTemplate;

        constructor(pageTemplate: api.content.page.PageTemplate) {
            this.pageTemplate = pageTemplate;
        }

        getPageTemplate(): api.content.page.PageTemplate {
            return this.pageTemplate;
        }
    }
}