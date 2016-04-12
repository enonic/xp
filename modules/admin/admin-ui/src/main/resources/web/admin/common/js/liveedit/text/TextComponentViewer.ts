module api.liveedit.text {

    import TextComponentView = api.liveedit.text.TextComponentView;

    export class TextComponentViewer extends api.ui.NamesAndIconViewer<api.content.page.region.TextComponent> {

        constructor() {
            super();
        }

        resolveDisplayName(object: api.content.page.region.TextComponent, componentView?: api.liveedit.text.TextComponentView): string {
            if (componentView) {
                return this.extractTextFromTextComponentView(componentView) || componentView.getName();
            }
            else {
                return object.getText();
            }
        }

        resolveSubName(object: api.content.page.region.TextComponent, relativePath: boolean = false): string {
            return object.getPath().toString();
        }

        resolveIconClass(object: api.content.page.region.TextComponent): string {
            return api.liveedit.ItemViewIconClassResolver.resolveByType("text");
        }

        private extractTextFromTextComponentView(object: ItemView): string {
            return wemjq(object.getHTMLElement()).text().trim();
        }
    }

}
