module api.application {

    export class ApplicationViewer extends api.ui.NamesAndIconViewer<Application> {

        constructor() {
            super("application-viewer");
        }

        doLayout(object: Application) {
            super.doLayout(object);
            if (object && object.isLocal()) {
                this.getNamesAndIconView().setIconToolTip("Local application");
            }

            if (object && object.getIconUrl()) {
                this.getNamesAndIconView().setIconUrl(object.getIconUrl());
            }
        }

        resolveDisplayName(object: Application): string {
            this.toggleClass("local", object.isLocal());
            return object.getDisplayName();
        }

        resolveSubName(object: Application, relativePath: boolean = false): string {
            if (object.getDescription()) {
                return object.getDescription();
            }
            
            return object.getName();
        }

        resolveIconClass(object: Application): string {
            return "icon-puzzle icon-large";
        }
    }
}