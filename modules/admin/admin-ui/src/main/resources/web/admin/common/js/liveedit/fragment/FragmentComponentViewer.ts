module api.liveedit.fragment {

    export class FragmentComponentViewer extends api.ui.NamesAndIconViewer<api.content.page.region.FragmentComponent> {

        constructor() {
            super();
        }

        resolveDisplayName(object: api.content.page.region.FragmentComponent): string {
            return !!object.getName() ? object.getName().toString() : "";
        }

        resolveSubName(object: api.content.page.region.FragmentComponent, relativePath: boolean = false): string {
            return object.getPath().toString();
        }

        resolveIconClass(object: api.content.page.region.FragmentComponent): string {
            return api.liveedit.ItemViewIconClassResolver.resolveByType("fragment");
        }

    }

}
