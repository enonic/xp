import "../../../../api.ts";
import {MobileDetailsPanel} from "../MobileDetailsSlidablePanel";

export class MobileDetailsPanelToggleButton extends api.dom.DivEl {

    private detailsPanel: MobileDetailsPanel;

    public static EXPANDED_CLASS: string = "expanded";

    constructor(detailsPanel: MobileDetailsPanel, slideInCallback?: () => void) {
        super("mobile-details-panel-toggle-button");

        this.detailsPanel = detailsPanel;

        this.onClicked((event) => {
            this.toggleClass(MobileDetailsPanelToggleButton.EXPANDED_CLASS);
            if (this.hasClass(MobileDetailsPanelToggleButton.EXPANDED_CLASS)) {
                this.detailsPanel.slideIn();
                if (!!slideInCallback) {
                    slideInCallback();
                }
            } else {
                this.detailsPanel.slideOut();
            }
            event.stopPropagation();
        });
    }
}
