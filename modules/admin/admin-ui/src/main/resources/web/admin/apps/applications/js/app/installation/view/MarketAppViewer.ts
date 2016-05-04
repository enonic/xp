import "../../../api.ts";

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

        this.namesAndIconView.getNamesView().setMainName(displayName, false).setSubName(subName, subTitle);
        if (!!subTitle) {
            this.namesAndIconView.getEl().setTitle(subTitle);
        } else if (!!subName) {
            this.namesAndIconView.getEl().setTitle(subName);
        }
        if (!!iconUrl) {
            this.namesAndIconView.setIconUrl(iconUrl);
        }
        this.namesAndIconView.getIconImageEl().onError(() => {
            this.namesAndIconView.setIconClass("icon-puzzle icon-large");
            this.namesAndIconView.getIconImageEl().setSrc("");
        });

        this.render();
    }

    resolveDisplayName(object: MarketApplication): string {
        var appLink = new api.dom.AEl().setUrl(object.getUrl(), "_blank").setHtml(object.getDisplayName(), false);
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
