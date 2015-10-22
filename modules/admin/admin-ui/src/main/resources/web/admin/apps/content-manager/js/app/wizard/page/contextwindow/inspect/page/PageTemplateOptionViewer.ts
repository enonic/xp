module app.wizard.page.contextwindow.inspect.page {

    export class PageTemplateOptionViewer extends api.ui.NamesAndIconViewer<PageTemplateOption> {

        constructor() {
            super();
        }

        resolveDisplayName(object: PageTemplateOption): string {
            return !!object.getPageTemplate() ? (object.isCustom() ? PageTemplateOption.displayNameCustom : object.getPageTemplate().getDisplayName()) : PageTemplateOption.displayNameAutomatic;
        }

        resolveSubName(object: PageTemplateOption, relativePath: boolean = false): string {
            if (!!object.getPageTemplate()) {
                if (object.isCustom()) {
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
            var iconClass = !!object.getPageTemplate() ? (object.isCustom() ? "icon-cog" : "icon-newspaper") : "icon-wand";

            return iconClass + " icon-large";
        }

        getPreferredHeight(): number {
            return 50;
        }
    }
}