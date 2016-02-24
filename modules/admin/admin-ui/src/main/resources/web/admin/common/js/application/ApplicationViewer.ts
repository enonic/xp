module api.application {

    export class ApplicationViewer extends api.ui.NamesAndIconViewer<Application> {

        constructor() {
            super("application-viewer");
        }

        resolveDisplayName(object: Application): string {
            this.toggleClass("local", object.isLocal());
            if (object.isLocal()) {
                this.getNamesAndIconView().setIconToolTip("Local application");
            }
            return object.getDisplayName();
        }

        resolveSubName(object: Application, relativePath: boolean = false): string {
            return object.getName();
        }

        resolveIconClass(object: Application): string {
            return "icon-puzzle icon-large";
        }
    }
}