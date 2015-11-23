module app.view.detail {

    import ViewItem = api.app.view.ViewItem;
    import ContentSummaryAndCompareStatus = api.content.ContentSummaryAndCompareStatus;

    export class ActiveDetailsPanelManager {

        private static activeDetailsPanel: DetailsPanel;

        private static debouncedSetActiveDetailsPanel: (detailsPanel: DetailsPanel) => void = api.util.AppHelper.debounce(ActiveDetailsPanelManager.doSetActiveDetailsPanel,
            300, false);

        constructor() {

        }

        static setActiveDetailsPanel(detailsPanelToMakeActive: DetailsPanel) {
            ActiveDetailsPanelManager.debouncedSetActiveDetailsPanel(detailsPanelToMakeActive);
        }

        static getActiveDetailsPanel(): DetailsPanel {
            return ActiveDetailsPanelManager.activeDetailsPanel;
        }

        private static doSetActiveDetailsPanel(detailsPanelToMakeActive: DetailsPanel) {
            var activeItem: ContentSummaryAndCompareStatus = null,
                currentlyActivePanel = ActiveDetailsPanelManager.getActiveDetailsPanel(),
                currentlyActiveWidget: WidgetView;

            if (currentlyActivePanel == detailsPanelToMakeActive || !detailsPanelToMakeActive) {
                return;
            } else if (currentlyActivePanel) {
                activeItem = currentlyActivePanel.getItem();
                currentlyActiveWidget = currentlyActivePanel.getActiveWidget();
            }
            ActiveDetailsPanelManager.activeDetailsPanel = detailsPanelToMakeActive;
            detailsPanelToMakeActive.getCustomWidgetViewsAndUpdateDropdown();
            detailsPanelToMakeActive.setItem(activeItem).then(() => {
                if (currentlyActiveWidget) {
                    detailsPanelToMakeActive.setActiveWidgetWithName(currentlyActiveWidget.getWidgetName());
                }
            });
        }
    }

}