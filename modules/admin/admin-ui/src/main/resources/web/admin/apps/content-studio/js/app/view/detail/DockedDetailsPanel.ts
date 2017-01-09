import "../../../api.ts";
import {DetailsPanel, DETAILS_PANEL_TYPE} from "./DetailsPanel";
import {DetailsView} from "./DetailsView";

import ResponsiveManager = api.ui.responsive.ResponsiveManager;

export class DockedDetailsPanel extends DetailsPanel {

    constructor(detailsView: DetailsView) {
        super(detailsView);
        this.setDoOffset(false);
        this.addClass("docked-details-panel");
    }

    protected subscribeOnEvents() {
        this.onPanelSizeChanged(() => this.detailsView.setDetailsContainerHeight());

        this.onShown(() => {
            if (this.getItem()) {
                // small delay so that isVisibleOrAboutToBeVisible() check detects width change
                setTimeout(() => this.detailsView.updateActiveWidget(), 250);
            }
        });
    }

    public isVisibleOrAboutToBeVisible(): boolean {
        return this.getHTMLElement().clientWidth > 0;
    }

    public getType(): DETAILS_PANEL_TYPE {
        return DETAILS_PANEL_TYPE.DOCKED;
    }
}

