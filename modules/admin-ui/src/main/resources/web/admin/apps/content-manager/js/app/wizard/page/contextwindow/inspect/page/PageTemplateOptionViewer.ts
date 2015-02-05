module app.wizard.page.contextwindow.inspect.page {

    export class PageTemplateOptionViewer extends api.ui.NamesAndIconViewer<PageTemplateOption> {

        constructor() {
            super();
        }

        resolveDisplayName(object: PageTemplateOption): string {
            return !!object.getPageTemplate() ? object.getPageTemplate().getDisplayName() : "Automatic";
        }

        resolveSubName(object: PageTemplateOption, relativePath: boolean = false): string {
            return !!object.getPageTemplate() ? object.getPageTemplate().getPath().toString() :
                                                "(" + object.getPageModel().getDefaultPageTemplate().getDisplayName().toString() + ")";
        }

        resolveIconClass(object: PageTemplateOption): string {
            return !!object.getPageTemplate() ? "icon-newspaper icon-large" : "icon-wand icon-large";
        }

        getPreferredHeight(): number {
            return 50;
        }
    }
}