module api.module {

    export class ModuleViewer extends api.ui.NamesAndIconViewer<Module> {

        constructor() {
            super();
        }

        resolveDisplayName(object: Module): string {
            return object.getDisplayName();
        }

        resolveSubName(object: Module, relativePath: boolean = false): string {
            return object.getName();
        }

        resolveIconClass(object: Module): string {
            return "icon-puzzle icon-large";
        }
    }
}