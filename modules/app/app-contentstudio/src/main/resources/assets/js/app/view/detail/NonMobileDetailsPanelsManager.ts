import '../../../api.ts';
import {DetailsPanel} from './DetailsPanel';
import {FloatingDetailsPanel} from './FloatingDetailsPanel';
import {DockedDetailsPanel} from './DockedDetailsPanel';
import {NonMobileDetailsPanelToggleButton} from './button/NonMobileDetailsPanelToggleButton';
import {ActiveDetailsPanelManager} from './ActiveDetailsPanelManager';

import ResponsiveRanges = api.ui.responsive.ResponsiveRanges;

export class NonMobileDetailsPanelsManager {

    private splitPanelWithGridAndDetails: api.ui.panel.SplitPanel;
    private dockedDetailsPanel: DockedDetailsPanel;
    private floatingDetailsPanel: FloatingDetailsPanel;
    private resizeEventMonitorLocked: boolean = false;
    private toggleButton: api.dom.DivEl = new NonMobileDetailsPanelToggleButton();
    private debouncedResizeHandler: () => void = api.util.AppHelper.debounce(this.doHandleResizeEvent, 300, false);

    constructor(builder: NonMobileDetailsPanelsManagerBuilder) {

        this.splitPanelWithGridAndDetails = builder.getSplitPanelWithGridAndDetails();
        this.dockedDetailsPanel = builder.getDefaultDetailsPanel();
        this.floatingDetailsPanel = builder.getFloatingDetailsPanel();

        this.toggleButton.onClicked((event) => {
            if (this.requiresAnimation()) {
                this.doPanelAnimation();
            }
        });

        this.dockedDetailsPanel.onShown(() => {
            this.splitPanelWithGridAndDetails.distribute();
        });
    }

    handleResizeEvent() {
        this.debouncedResizeHandler();
    }

    private doHandleResizeEvent() {

        if (this.resizeEventMonitorLocked || !this.toggleButton.isRendered()) {
            return;
        }

        if (this.nonMobileDetailsPanelIsActive() && this.contentBrowsePanelIsVisible()) {
            this.resizeEventMonitorLocked = true;
            if (this.needsModeSwitch()) {
                this.doPanelAnimation(true, true);
            } else if (!this.splitPanelWithGridAndDetails.isSecondPanelHidden()) {
                this.dockedDetailsPanel.notifyPanelSizeChanged();
            } else if (this.isFloatingDetailsPanelActive()) {
                this.floatingDetailsPanel.notifyPanelSizeChanged();
            }
            setTimeout(() => {
                this.resizeEventMonitorLocked = false;
            }, 600);

        } else {

            if (this.isExpanded() && !this.isFloatingDetailsPanelActive() && !this.nonMobileDetailsPanelIsActive()) {
                this.toggleButton.removeClass('expanded');
                this.splitPanelWithGridAndDetails.removeClass('details-panel-expanded');
            }

            return;
        }
    }

    private needsModeSwitch() {
        return (this.needsSwitchToFloatingMode() || this.needsSwitchToDockedMode());
    }

    private contentBrowsePanelIsVisible(): boolean {
        return this.splitPanelWithGridAndDetails.getParentElement().isVisible();
    }

    private isFloatingDetailsPanelActive(): boolean {
        return ActiveDetailsPanelManager.getActiveDetailsPanel() === this.floatingDetailsPanel;
    }

    private nonMobileDetailsPanelIsActive(): boolean {
        return ActiveDetailsPanelManager.getActiveDetailsPanel() === this.dockedDetailsPanel ||
               ActiveDetailsPanelManager.getActiveDetailsPanel() === this.floatingDetailsPanel;
    }

    public setActivePanel() {
        if (this.nonMobileDetailsPanelIsActive()) {
            return;
        }

        ActiveDetailsPanelManager.setActiveDetailsPanel(this.requiresFloatingPanelDueToShortWidth() ?
                                                        this.floatingDetailsPanel : this.dockedDetailsPanel);
    }

    private doPanelAnimation(canSetActivePanel: boolean = true, onResize: boolean = false) {

        this.splitPanelWithGridAndDetails.addClass('sliding');

        if (this.requiresFloatingPanelDueToShortWidth()) {
            this.switchToFloatingMode(canSetActivePanel, onResize);
        } else {
            this.switchToDockedMode(canSetActivePanel);
        }

        setTimeout(() => {
            this.ensureButtonHasCorrectState();
        }, this.isExpanded() ? 300 : 0);
    }

    private switchToDockedMode(canSetActivePanel: boolean = true) {
        this.toggleButton.removeClass('floating-mode');
        if (this.floatingPanelIsShown()) {
            this.floatingToDockedSync();
        }

        if (canSetActivePanel) {
            ActiveDetailsPanelManager.setActiveDetailsPanel(this.dockedDetailsPanel);
        }

        this.dockedDetailsPanel.addClass('left-bordered');

        if (this.isExpanded()) {
            if (!this.splitPanelWithGridAndDetails.isSecondPanelHidden()) {
                this.splitPanelWithGridAndDetails.foldSecondPanel();
            }
        } else {
            this.splitPanelWithGridAndDetails.showSecondPanel(false);
        }

        setTimeout(() => {
            this.dockedDetailsPanel.removeClass('left-bordered');
            if (this.isExpanded()) {
                this.splitPanelWithGridAndDetails.showSplitter();
                this.dockedDetailsPanel.notifyPanelSizeChanged();
            }
            this.splitPanelWithGridAndDetails.removeClass('sliding');
        }, 600);
    }

    private switchToFloatingMode(canSetActivePanel: boolean = true, onResize: boolean = false) {
        if (!canSetActivePanel && !this.floatingPanelIsShown()) {
            return;
        }

        this.toggleButton.addClass('floating-mode');
        if (!this.splitPanelWithGridAndDetails.isSecondPanelHidden()) {
            this.dockedToFloatingSync();
        }

        if (canSetActivePanel) {
            ActiveDetailsPanelManager.setActiveDetailsPanel(this.floatingDetailsPanel);
        }

        if (!this.isExpanded() || onResize) {
            this.floatingDetailsPanel.resetWidgetsWidth();
            this.floatingDetailsPanel.slideIn();
            this.floatingDetailsPanel.notifyPanelSizeChanged();
        } else {
            this.floatingDetailsPanel.slideOut();
        }
        this.splitPanelWithGridAndDetails.setActiveWidthPxOfSecondPanel(this.floatingDetailsPanel.getActualWidth());
        this.splitPanelWithGridAndDetails.removeClass('sliding');

        setTimeout(() => {
            this.ensureButtonHasCorrectState();
            this.splitPanelWithGridAndDetails.removeClass('details-panel-expanded');
        }, this.isExpanded() ? 300 : 0);
    }

    hideActivePanel(onResize: boolean = false) {
        this.doPanelAnimation(false, onResize);
    }

    hideDockedDetailsPanel() {
        this.splitPanelWithGridAndDetails.foldSecondPanel();
    }

    getToggleButton(): api.dom.DivEl {
        return this.toggleButton;
    }

    getActivePanel(): DetailsPanel {
        return this.requiresFloatingPanelDueToShortWidth() ? this.floatingDetailsPanel : this.dockedDetailsPanel;
    }

    private isExpanded(): boolean {
        return this.toggleButton.hasClass('expanded');
    }

    private dockedToFloatingSync() {
        let activePanelWidth = this.splitPanelWithGridAndDetails.getActiveWidthPxOfSecondPanel();
        this.hideDockedDetailsPanel();
        this.floatingDetailsPanel.setWidthPx(activePanelWidth);
    }

    private floatingToDockedSync() {
        this.floatingDetailsPanel.slideOut();
        let activePanelWidth: number = this.floatingDetailsPanel.getActualWidth();
        this.splitPanelWithGridAndDetails.setActiveWidthPxOfSecondPanel(activePanelWidth);
    }

    private needsSwitchToFloatingMode(): boolean {
        if (this.requiresFloatingPanelDueToShortWidth() && !this.splitPanelWithGridAndDetails.isSecondPanelHidden()) {
            return true;
        }
        return false;
    }

    private needsSwitchToDockedMode(): boolean {
        if (!this.requiresFloatingPanelDueToShortWidth() && this.splitPanelWithGridAndDetails.isSecondPanelHidden() && this.isExpanded()) {
            return true;
        }
        return false;
    }

    private requiresAnimation(): boolean {
        return this.isExpanded() ?
               (!this.splitPanelWithGridAndDetails.isSecondPanelHidden() || this.floatingPanelIsShown()) :
               (this.splitPanelWithGridAndDetails.isSecondPanelHidden() && !this.floatingPanelIsShown());
    }

    private floatingPanelIsShown(): boolean {
        if (!this.nonMobileDetailsPanelIsActive()) {
            return false;
        }
        let right = this.floatingDetailsPanel.getHTMLElement().style.right;
        if (right && right.indexOf('px') > -1) {
            right = right.substring(0, right.indexOf('px'));
            return Number(right) >= 0;
        }
        return false;
    }

    private requiresFloatingPanelDueToShortWidth(): boolean {
        let splitPanelWidth = this.splitPanelWithGridAndDetails.getEl().getWidthWithBorder();
        if (this.floatingPanelIsShown()) {
            return ( splitPanelWidth - this.floatingDetailsPanel.getActualWidth() ) < 320;
        } else {
            let defaultDetailsPanelWidth = this.splitPanelWithGridAndDetails.getActiveWidthPxOfSecondPanel();
            return ( splitPanelWidth - defaultDetailsPanelWidth ) < 320;
        }
    }

    requiresCollapsedDetailsPanel(): boolean {
        let splitPanelWidth = this.splitPanelWithGridAndDetails.getEl().getWidthWithBorder();
        return this.requiresFloatingPanelDueToShortWidth() || ResponsiveRanges._1620_1920.isFitOrSmaller(splitPanelWidth);
    }

    ensureButtonHasCorrectState() {
        this.toggleButton.toggleClass('expanded',
            !this.splitPanelWithGridAndDetails.isSecondPanelHidden() || this.floatingPanelIsShown());
        this.splitPanelWithGridAndDetails.toggleClass('details-panel-expanded', this.isExpanded());
    }

    static create(): NonMobileDetailsPanelsManagerBuilder {
        return new NonMobileDetailsPanelsManagerBuilder();
    }
}

export class NonMobileDetailsPanelsManagerBuilder {
    private splitPanelWithGridAndDetails: api.ui.panel.SplitPanel;
    private dockedDetailsPanel: DockedDetailsPanel;
    private floatingDetailsPanel: FloatingDetailsPanel;

    setSplitPanelWithGridAndDetails(splitPanelWithGridAndDetails: api.ui.panel.SplitPanel) {
        this.splitPanelWithGridAndDetails = splitPanelWithGridAndDetails;
    }

    setDefaultDetailsPanel(dockedDetailsPanel: DockedDetailsPanel) {
        this.dockedDetailsPanel = dockedDetailsPanel;
    }

    setFloatingDetailsPanel(floatingDetailsPanel: FloatingDetailsPanel) {
        this.floatingDetailsPanel = floatingDetailsPanel;
    }

    getSplitPanelWithGridAndDetails(): api.ui.panel.SplitPanel {
        return this.splitPanelWithGridAndDetails;
    }

    getDefaultDetailsPanel(): DockedDetailsPanel {
        return this.dockedDetailsPanel;
    }

    getFloatingDetailsPanel(): FloatingDetailsPanel {
        return this.floatingDetailsPanel;
    }

    build(): NonMobileDetailsPanelsManager {
        return new NonMobileDetailsPanelsManager(this);
    }
}
