module api.liveedit.text {

    export class TextComponentViewer extends api.ui.NamesAndIconViewer<api.content.page.region.TextComponent> {

        constructor() {
            super();
        }

        resolveDisplayName(object: api.content.page.region.TextComponent): string {
            return object.getText();
        }

        resolveSubName(object: api.content.page.region.TextComponent, relativePath: boolean = false): string {
            return object.getPath().toString();
        }

        resolveIconClass(object: api.content.page.region.TextComponent): string {
            return "live-edit-font-icon-text";
        }
    }

}
