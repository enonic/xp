import "../../../../api.ts";

import {DetailsPanel} from "../DetailsPanel";

export class InfoWidgetToggleButton extends api.dom.DivEl {

    constructor(detailsPanel: DetailsPanel) {
        super("info-widget-toggle-button");

        this.onClicked((event) => {
            this.setActive();
            detailsPanel.activateDefaultWidget();
        });
    }

    setActive() {
        this.addClass("active");
    }

    setInactive() {
        this.removeClass("active");
    }
}
