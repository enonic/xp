module app.wizard.page.contextwindow.inspect.page {

    import PageModel = api.content.page.PageModel;
    import PageTemplate = api.content.page.PageTemplate;

    export class PageTemplateOption {

        private template: PageTemplate;

        private pageModel: PageModel;

        constructor(template: PageTemplate, pageModel: PageModel) {
            this.template = template;
            this.pageModel = pageModel;
        }

        getPageTemplate(): PageTemplate {
            return this.template;
        }

        getPageModel(): PageModel {
            return this.pageModel;
        }

        isCustom(): boolean {
            var pageTemplateDisplayName = api.content.page.PageTemplateDisplayName;

            return this.template && this.template.getDisplayName() == pageTemplateDisplayName[pageTemplateDisplayName.Custom];
        }
    }
}