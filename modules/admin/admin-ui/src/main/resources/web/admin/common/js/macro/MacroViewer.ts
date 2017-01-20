module api.macro {

    export class MacroViewer extends api.ui.NamesAndIconViewer<MacroDescriptor> {

        constructor() {
            super();
        }

        resolveDisplayName(object: MacroDescriptor): string {
            return object.getDisplayName();
        }

        resolveSubName(object: MacroDescriptor, relativePath: boolean = false): string {
            return object.getDescription();
        }

        resolveIconUrl(object: MacroDescriptor): string {
            return object.getIconUrl();
        }
    }
}
