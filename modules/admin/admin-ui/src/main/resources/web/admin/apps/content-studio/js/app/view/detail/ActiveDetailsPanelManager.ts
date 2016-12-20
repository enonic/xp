import "../../../api.ts";
import {DetailsPanel} from "./DetailsPanel";

import ViewItem = api.app.view.ViewItem;
import ContentSummaryAndCompareStatus = api.content.ContentSummaryAndCompareStatus;

export class ActiveDetailsPanelManager {

    private static activeDetailsPanel: DetailsPanel;

    private static debouncedSetActiveDetailsPanel: (detailsPanel: DetailsPanel) => void = api.util.AppHelper.debounce(
        ActiveDetailsPanelManager.doSetActiveDetailsPanel,
        300, false);

    static setActiveDetailsPanel(detailsPanelToMakeActive: DetailsPanel) {
        ActiveDetailsPanelManager.debouncedSetActiveDetailsPanel(detailsPanelToMakeActive);
    }

    static getActiveDetailsPanel(): DetailsPanel {
        return ActiveDetailsPanelManager.activeDetailsPanel;
    }

    private static doSetActiveDetailsPanel(detailsPanelToMakeActive: DetailsPanel) {
        var currentlyActivePanel = ActiveDetailsPanelManager.getActiveDetailsPanel();
        if (currentlyActivePanel == detailsPanelToMakeActive || !detailsPanelToMakeActive) {
            return;
        }

        ActiveDetailsPanelManager.activeDetailsPanel = detailsPanelToMakeActive;
        ActiveDetailsPanelManager.activeDetailsPanel.setActive();
    }
}
