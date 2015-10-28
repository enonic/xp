module api.ui {

    /**
     * A parent class capable of viewing a given object with names and icon.
     */
    export class NamesAndIconViewer<OBJECT> extends api.ui.Viewer<OBJECT> {

        static EMPTY_DISPLAY_NAME: string = "<Display Name>";

        private namesAndIconView: api.app.NamesAndIconView;

        constructor(className?: string, size: api.app.NamesAndIconViewSize = api.app.NamesAndIconViewSize.small) {
            super(className);
            this.namesAndIconView = new api.app.NamesAndIconViewBuilder().setSize(size).build();
        }

        setObject(object: OBJECT, relativePath: boolean = false) {
            super.setObject(object);

            var displayName = this.resolveDisplayName(object) || this.normalizeDisplayName(this.resolveUnnamedDisplayName(object)),
                subName = this.resolveSubName(object, relativePath) || api.content.ContentUnnamed.prettifyUnnamed(),
                subTitle    = this.resolveSubTitle(object),
                iconClass   = this.resolveIconClass(object),
                iconUrl     = this.resolveIconUrl(object);

            this.namesAndIconView.setMainName(displayName).
                                  setSubName(subName, subTitle).
                                  setIconClass(iconClass);
            if (!!iconUrl) {
                this.namesAndIconView.setIconUrl(iconUrl);
            }

            this.render();
        }

        private normalizeDisplayName(displayName: string): string {
            if (api.util.StringHelper.isEmpty(displayName)) {
                return NamesAndIconViewer.EMPTY_DISPLAY_NAME;
            } else {
                return api.content.ContentUnnamed.prettifyUnnamed(displayName);
            }
        }

        resolveDisplayName(object: OBJECT): string {
            return "";
        }

        resolveUnnamedDisplayName(object: OBJECT): string {
            return "";
        }

        resolveSubName(object: OBJECT, relativePath: boolean = false): string {
            return "";
        }

        resolveSubTitle(object: OBJECT): string {
            return "";
        }

        resolveIconClass(object: OBJECT): string {
            return "";
        }

        resolveIconUrl(object: OBJECT): string {
            return "";
        }

        getPreferredHeight(): number {
            return 50;
        }

        doRender() {
            this.removeChildren();
            this.appendChild(this.namesAndIconView);
            return true;
        }
    }
}