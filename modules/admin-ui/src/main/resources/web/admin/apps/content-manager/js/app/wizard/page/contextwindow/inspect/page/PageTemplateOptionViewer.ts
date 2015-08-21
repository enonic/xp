module app.wizard.page.contextwindow.inspect.page {

    export class PageTemplateOptionViewer extends api.ui.NamesAndIconViewer<PageTemplateOption> {

        constructor() {
            super();
        }

        resolveDisplayName(object: PageTemplateOption): string {
            return !!object.getPageTemplate() ? object.getPageTemplate().getDisplayName() : "Automatic";
        }

        resolveSubName(object: PageTemplateOption, relativePath: boolean = false): string {
            if (!!object.getPageTemplate()) {
                if (object.getPageTemplate().getDisplayName() == "Customized") {
                    return "Set up your own page";
                }
                else {
                    return object.getPageTemplate().getPath().toString();
                }
            }
            else if (!!object.getPageModel().getDefaultPageTemplate()) {
                return "(" + object.getPageModel().getDefaultPageTemplate().getDisplayName().toString() + ")";
            }
            else {
                return "( no default template found )";
            }
        }

        resolveIconClass(object: PageTemplateOption): string {
            return !!object.getPageTemplate() ? object.getPageTemplate().getDisplayName() == "Customized" ? "icon-cog icon-large" :
                                                    "icon-newspaper icon-large" : "icon-wand icon-large";
        }

        getPreferredHeight(): number {
            return 50;
        }
    }
}