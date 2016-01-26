module app.installation.view {

    import MarketApplication = api.application.MarketApplication;

    export class MarketAppViewer extends api.ui.Viewer<MarketApplication> {

        private namesAndIconView: api.app.NamesAndIconView;

        constructor(className?: string, size: api.app.NamesAndIconViewSize = api.app.NamesAndIconViewSize.small) {
            super(className);
            this.namesAndIconView = new api.app.NamesAndIconViewBuilder().setSize(size).build();
        }

        setObject(object: MarketApplication, relativePath: boolean = false) {
            super.setObject(object);

            var displayName = this.resolveDisplayName(object),
                subName = this.resolveSubName(object, relativePath),
                subTitle = this.resolveSubTitle(object),
                iconUrl = this.resolveIconUrl(object);

            this.namesAndIconView.getNamesView().setMainName(displayName, false).
                setSubName(subName, subTitle);
            this.namesAndIconView.getEl().setTitle(this.resolveSubTitle(object));
            if (!!iconUrl) {
                this.namesAndIconView.setIconUrl(iconUrl);
            }

            this.render();
        }

        resolveDisplayName(object: MarketApplication): string {
            var appLink = new api.dom.AEl().setUrl(object.getApplicationUrl(), "_blank").setHtml(object.getDisplayName(), false);
            return appLink.toString();
        }

        resolveSubName(object: MarketApplication, relativePath: boolean = false): string {
            return object.getDescription();
        }

        resolveSubTitle(object: MarketApplication): string {
            return object.getDescription();
        }

        resolveIconUrl(object: MarketApplication): string {
            return object.getIconUrl();
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