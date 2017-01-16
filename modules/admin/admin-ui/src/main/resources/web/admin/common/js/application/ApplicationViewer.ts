module api.application {
    import ApplicationUploadMock = api.application.ApplicationUploadMock;

    export class ApplicationViewer extends api.ui.NamesAndIconViewer<Application> {

        constructor() {
            super("application-viewer");
        }

        doLayout(object: Application | ApplicationUploadMock) {
            super.doLayout(<Application>object);
            if (object && object.isLocal()) {
                this.getNamesAndIconView().setIconToolTip("Local application");
            }

            if (object && object instanceof Application && object.getIconUrl()) {
                this.getNamesAndIconView().setIconUrl(object.getIconUrl());
            }
        }

        resolveDisplayName(object: Application): string {
            this.toggleClass("local", object.isLocal());
            return object.getDisplayName();
        }

        resolveSubName(object: Application | ApplicationUploadMock, relativePath: boolean = false): string {
            if (object instanceof Application && object.getDescription()) {
                return object.getDescription();
            }

            return object.getName();
        }

        resolveIconClass(object: Application): string {
            return "icon-puzzle icon-large";
        }
    }
}
