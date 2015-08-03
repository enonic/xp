module api.application {

    export class ApplicationViewer extends api.ui.NamesAndIconViewer<Application> {

        constructor() {
            super();
        }

        resolveDisplayName(object: Application): string {
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