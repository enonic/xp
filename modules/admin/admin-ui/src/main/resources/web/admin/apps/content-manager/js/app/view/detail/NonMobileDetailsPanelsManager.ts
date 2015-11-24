module app.view.detail {

    import ResponsiveRanges = api.ui.responsive.ResponsiveRanges;
    import ResponsiveItem = api.ui.responsive.ResponsiveItem;
    import ActiveDetailsPanelsManager = app.view.detail.ActiveDetailsPanelManager;

    export class NonMobileDetailsPanelsManager {

        private splitPanelWithGridAndDetails: api.ui.panel.SplitPanel;
        private defaultDockedDetailsPanel: DetailsPanel;
        private floatingDetailsPanel: DetailsPanel;
        private resizeEventMonitorLocked: boolean = false;
        private toggleButton: api.dom.DivEl = new api.dom.DivEl("button non-mobile-details-panel-toggle-button");
        private debouncedResizeHandler: () => void = api.util.AppHelper.debounce(this.doHandleResizeEvent, 300, false);

        constructor(builder: NonMobileDetailsPanelsManagerBuilder) {

            this.splitPanelWithGridAndDetails = builder.getSplitPanelWithGridAndDetails();
            this.defaultDockedDetailsPanel = builder.getDefaultDetailsPanel();
            this.floatingDetailsPanel = builder.getFloatingDetailsPanel();

            this.toggleButton.onClicked((event) => {
                this.toggleButton.toggleClass("expanded");

                if (this.requiresAnimation()) {
                    this.doPanelAnimation();
                }
            });

            this.defaultDockedDetailsPanel.onShown(() => {
                this.splitPanelWithGridAndDetails.distribute();
            });
        }

        handleResizeEvent() {
            this.debouncedResizeHandler();
        }

        private doHandleResizeEvent() {
            if (!this.resizeEventMonitorLocked && this.nonMobileDetailsPanelIsActive() && this.contentBrowsePanelIsVisible()) {
                this.resizeEventMonitorLocked = true;
                if (this.needsSwitchToFloatingMode() || this.needsSwitchToDockedMode()) {
                    this.doPanelAnimation();
                } else if (!this.splitPanelWithGridAndDetails.isSecondPanelHidden()) {
                    this.defaultDockedDetailsPanel.notifyPanelSizeChanged();
                }
                setTimeout(() => {
                    this.resizeEventMonitorLocked = false;
                }, 600);
            } else {
                return;
            }
        }

        private contentBrowsePanelIsVisible(): boolean {
            return this.splitPanelWithGridAndDetails.getParentElement().isVisible();
        }

        private nonMobileDetailsPanelIsActive(): boolean {
            return ActiveDetailsPanelsManager.getActiveDetailsPanel() == this.defaultDockedDetailsPanel ||
                   ActiveDetailsPanelsManager.getActiveDetailsPanel() == this.floatingDetailsPanel;
        }

        private doPanelAnimation(canSetActivePanel: boolean = true) {
            if (this.requiresFloatingPanelDueToShortWidth()) {
                if (!this.splitPanelWithGridAndDetails.isSecondPanelHidden()) {
                    this.dockedToFloatingSync();
                }

                if (canSetActivePanel) {
                    ActiveDetailsPanelManager.setActiveDetailsPanel(this.floatingDetailsPanel);
                }

                if (this.isExpanded()) {
                    this.floatingDetailsPanel.resetWidgetsWidth();
                    this.floatingDetailsPanel.slideIn();
                    this.floatingDetailsPanel.notifyPanelSizeChanged();
                } else {
                    this.floatingDetailsPanel.slideOut();
                }
                this.splitPanelWithGridAndDetails.setActiveWidthPxOfSecondPanel(this.floatingDetailsPanel.getActualWidth());

            } else {

                if (this.floatingPanelIsShown()) {
                    this.floatingToDockedSync();
                }

                if (canSetActivePanel) {
                    ActiveDetailsPanelManager.setActiveDetailsPanel(this.defaultDockedDetailsPanel);
                }

                this.defaultDockedDetailsPanel.addClass("left-bordered");

                if (this.isExpanded()) {
                    this.splitPanelWithGridAndDetails.showSecondPanel(false);
                } else if (!this.splitPanelWithGridAndDetails.isSecondPanelHidden()) {
                    this.splitPanelWithGridAndDetails.foldSecondPanel();
                }

                setTimeout(() => {
                    this.defaultDockedDetailsPanel.removeClass("left-bordered");
                    if (this.isExpanded()) {
                        this.splitPanelWithGridAndDetails.showSplitter();
                        this.defaultDockedDetailsPanel.notifyPanelSizeChanged();
                    }
                }, 500);
            }

            this.ensureButtonHasCorrectState();
        }

        hideActivePanel() {
            this.toggleButton.removeClass("expanded");
            this.doPanelAnimation(false);
        }

        hideDockedDetailsPanel() {
            this.splitPanelWithGridAndDetails.foldSecondPanel();
        }

        getToggleButton(): api.dom.DivEl {
            return this.toggleButton;
        }

        getActivePanel(): DetailsPanel {
            return this.requiresFloatingPanelDueToShortWidth() ? this.floatingDetailsPanel : this.defaultDockedDetailsPanel;
        }

        private isExpanded(): boolean {
            return this.toggleButton.hasClass("expanded");
        }

        private dockedToFloatingSync() {
            var activePanelWidth = this.splitPanelWithGridAndDetails.getActiveWidthPxOfSecondPanel();
            this.hideDockedDetailsPanel();
            this.floatingDetailsPanel.setWidthPx(activePanelWidth)
        }

        private floatingToDockedSync() {
            this.floatingDetailsPanel.slideOut();
            var activePanelWidth: number = this.floatingDetailsPanel.getActualWidth();
            this.splitPanelWithGridAndDetails.setActiveWidthPxOfSecondPanel(activePanelWidth);
        }

        private needsSwitchToFloatingMode(): boolean {
            if (this.requiresFloatingPanelDueToShortWidth() && !this.splitPanelWithGridAndDetails.isSecondPanelHidden()) {
                return true;
            }
            return false;
        }

        private needsSwitchToDockedMode(): boolean {
            if (!this.requiresFloatingPanelDueToShortWidth() && this.splitPanelWithGridAndDetails.isSecondPanelHidden() &&
                this.isExpanded()) {
                return true;
            }
            return false;
        }

        private requiresAnimation(): boolean {
            if (this.isExpanded()) {
                if (this.splitPanelWithGridAndDetails.isSecondPanelHidden() && !this.floatingPanelIsShown()) {
                    return true;
                }
            } else {
                if (!this.splitPanelWithGridAndDetails.isSecondPanelHidden() || this.floatingPanelIsShown()) {
                    return true;
                }
            }
            return false;
        }

        private floatingPanelIsShown(): boolean {
            var right = this.floatingDetailsPanel.getHTMLElement().style.right;
            if (right && right.indexOf("px") > -1) {
                right = right.substring(0, right.indexOf("px"));
                return Number(right) >= 0;
            }
            return false;
        }

        private requiresFloatingPanelDueToShortWidth(): boolean {
            var splitPanelWidth = this.splitPanelWithGridAndDetails.getEl().getWidthWithBorder();
            if (this.floatingPanelIsShown()) {
                return ( splitPanelWidth - this.floatingDetailsPanel.getActualWidth() ) < 320;
            } else {
                var defaultDetailsPanelWidth = this.splitPanelWithGridAndDetails.getActiveWidthPxOfSecondPanel();
                return ( splitPanelWidth - defaultDetailsPanelWidth ) < 320;
            }
        }

        requiresCollapsedDetailsPanel(): boolean {
            var splitPanelWidth = this.splitPanelWithGridAndDetails.getEl().getWidthWithBorder();
            return this.requiresFloatingPanelDueToShortWidth() || ResponsiveRanges._1620_1920.isFitOrSmaller(splitPanelWidth);
        }

        ensureButtonHasCorrectState() {
            this.toggleButton.toggleClass("expanded",
                !this.splitPanelWithGridAndDetails.isSecondPanelHidden() || this.floatingPanelIsShown());
        }

        static create(): NonMobileDetailsPanelsManagerBuilder {
            return new NonMobileDetailsPanelsManagerBuilder();
        }
    }


    export class NonMobileDetailsPanelsManagerBuilder {
        private splitPanelWithGridAndDetails: api.ui.panel.SplitPanel;
        private defaultDockedDetailsPanel: DetailsPanel;
        private floatingDetailsPanel: DetailsPanel;

        constructor() {
        }

        setSplitPanelWithGridAndDetails(splitPanelWithGridAndDetails: api.ui.panel.SplitPanel) {
            this.splitPanelWithGridAndDetails = splitPanelWithGridAndDetails;
        }

        setDefaultDetailsPanel(defaultDockedDetailsPanel: DetailsPanel) {
            this.defaultDockedDetailsPanel = defaultDockedDetailsPanel;
        }

        setFloatingDetailsPanel(floatingDetailsPanel: DetailsPanel) {
            this.floatingDetailsPanel = floatingDetailsPanel;
        }

        getSplitPanelWithGridAndDetails(): api.ui.panel.SplitPanel {
            return this.splitPanelWithGridAndDetails;
        }

        getDefaultDetailsPanel(): DetailsPanel {
            return this.defaultDockedDetailsPanel;
        }

        getFloatingDetailsPanel(): DetailsPanel {
            return this.floatingDetailsPanel;
        }

        build(): NonMobileDetailsPanelsManager {
            return new NonMobileDetailsPanelsManager(this);
        }
    }

}