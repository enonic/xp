module api.ui {

    import i18n = api.util.i18n;

    /**
     * A parent class capable of viewing a given object with names and icon.
     */
    export class NamesAndIconViewer<OBJECT>
        extends api.ui.Viewer<OBJECT> {

        private emptyDisplayName: string = '<' + i18n('field.displayName') + '>';

        private namesAndIconView: api.app.NamesAndIconView;

        private relativePath: boolean;

        private size: api.app.NamesAndIconViewSize;

        public static debug: boolean = false;

        constructor(className?: string, size: api.app.NamesAndIconViewSize = api.app.NamesAndIconViewSize.small) {
            super(className);

            this.size = size;
        }

        setObject(object: OBJECT, relativePath: boolean = false) {
            this.relativePath = relativePath;
            return super.setObject(object);
        }

        doLayout(object: OBJECT) {
            super.doLayout(object);

            if (NamesAndIconViewer.debug) {
                console.debug('NamesAndIconViewer.doLayout');
            }

            if (!this.namesAndIconView) {
                this.namesAndIconView = new api.app.NamesAndIconViewBuilder().setSize(this.size).build();
                this.appendChild(this.namesAndIconView);
            }

            if (object) {
                const displayName = this.resolveDisplayName(object) || this.normalizeDisplayName(this.resolveUnnamedDisplayName(object));
                const subName = this.resolveSubName(object, this.relativePath) || api.content.ContentUnnamed.prettifyUnnamed();
                const subTitle = this.resolveSubTitle(object);

                let iconUrl;
                let iconClass;
                let iconEl = this.resolveIconEl(object);
                let hideIcon = false;
                if (iconEl) {
                    this.namesAndIconView.setIconEl(iconEl);
                } else {
                    iconUrl = this.resolveIconUrl(object);
                    if (!api.util.StringHelper.isBlank(iconUrl)) {
                        this.namesAndIconView.setIconUrl(iconUrl);
                    } else {
                        iconClass = this.resolveIconClass(object);
                        if (!api.util.StringHelper.isBlank(iconClass)) {
                            this.namesAndIconView.setIconClass(iconClass);
                        } else {
                            hideIcon = true;
                        }
                    }
                }
                this.namesAndIconView.toggleClass('no-icon', hideIcon);

                this.namesAndIconView.setMainName(displayName)
                    .setSubName(subName, subTitle);
            }
        }

        private normalizeDisplayName(displayName: string): string {
            if (api.util.StringHelper.isEmpty(displayName)) {
                return this.emptyDisplayName;
            } else {
                return api.content.ContentUnnamed.prettifyUnnamed(displayName);
            }
        }

        resolveDisplayName(object: OBJECT): string {
            return '';
        }

        resolveUnnamedDisplayName(object: OBJECT): string {
            return '';
        }

        resolveSubName(object: OBJECT, relativePath: boolean = false): string {
            return '';
        }

        resolveSubTitle(object: OBJECT): string {
            return '';
        }

        resolveIconClass(object: OBJECT): string {
            return '';
        }

        resolveIconUrl(object: OBJECT): string {
            return '';
        }

        resolveIconEl(object: OBJECT): api.dom.Element {
            return null;
        }

        getPreferredHeight(): number {
            return 50;
        }

        getNamesAndIconView(): api.app.NamesAndIconView {
            return this.namesAndIconView;
        }
    }
}
