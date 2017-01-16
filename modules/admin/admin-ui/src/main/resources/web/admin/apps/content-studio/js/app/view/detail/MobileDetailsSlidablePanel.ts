import "../../../api.ts";
import {SlidablePanel, SlidablePanelBuilder, SLIDE_FROM} from "./SlidablePanel";
import {DetailsView} from "./DetailsView";
import {DETAILS_PANEL_TYPE} from "./DetailsPanel";

import ResponsiveManager = api.ui.responsive.ResponsiveManager;

export class MobileDetailsPanel extends SlidablePanel {

    constructor(detailsView: DetailsView) {
        super(new SlidablePanelBuilder().setSlideFrom(SLIDE_FROM.BOTTOM), detailsView);
        this.addClass('mobile');
    }

    protected slideOutTop() {
        this.getEl().setTopPx(api.BrowserHelper.isIOS() ? -window.innerHeight : -window.outerHeight);
    }

    protected slideOutBottom() {
        this.getEl().setTopPx(api.BrowserHelper.isIOS() ? window.innerHeight : window.outerHeight);
    }

    public getType(): DETAILS_PANEL_TYPE {
        return DETAILS_PANEL_TYPE.MOBILE;
    }
}
