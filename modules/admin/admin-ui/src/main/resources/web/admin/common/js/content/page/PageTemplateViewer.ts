module api.content.page {

    export class PageTemplateViewer extends api.ui.NamesAndIconViewer<PageTemplate> {

        constructor() {
            super();
        }

        resolveDisplayName(object: PageTemplate): string {
            return object.getDisplayName();
        }

        resolveSubName(object: PageTemplate, relativePath: boolean = false): string {
            return object.getController().toString();
        }

        resolveIconClass(object: PageTemplate): string {
            return "icon-newspaper icon-large";
        }
    }
}