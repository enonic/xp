module api.liveedit.layout {

    export class LayoutComponentViewer extends api.ui.NamesAndIconViewer<api.content.page.region.LayoutComponent> {

        constructor() {
            super();
        }

        resolveDisplayName(object: api.content.page.region.LayoutComponent): string {
            return !!object.getName() ? object.getName().toString() : "";
        }

        resolveSubName(object: api.content.page.region.LayoutComponent, relativePath: boolean = false): string {
            return object.getPath().toString();
        }

        resolveIconClass(object: api.content.page.region.LayoutComponent): string {
            return api.liveedit.ItemViewIconClassResolver.resolveByType("layout");
        }
    }

}
