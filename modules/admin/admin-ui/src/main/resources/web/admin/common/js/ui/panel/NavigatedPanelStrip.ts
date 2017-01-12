module api.ui.panel {

    export class NavigatedPanelStrip extends PanelStrip {

        private navigator: Navigator;
        private scrollIndex: number = -1;
        private focusIndex: number = -1;
        private focusVisible: boolean = false;
        private listenToScroll: boolean = true;

        constructor(navigator: Navigator, scrollable?: api.dom.Element, className?: string) {
            super(scrollable, className);
            this.navigator = navigator;

            navigator.onNavigationItemSelected((event: NavigatorEvent) => {
                this.listenToScroll = false;
                this.showPanelByIndex(event.getItem().getIndex());
            });

            this.onPanelShown((event: PanelShownEvent) => {
                this.listenToScroll = true;
            });

            //
            this.getScrollable().onScroll((event: MouseEvent) => {
                if (this.listenToScroll) {
                    this.updateScrolledNavigationItem();
                }
            });

        }

        setListenToScroll(listen: boolean = true) {
            this.listenToScroll = listen;
        }

        private updateScrolledNavigationItem() {
            let scrollTop = this.getScrollable().getHTMLElement().scrollTop;

            let focusVisible = this.isFocusedPanelVisible(scrollTop);
            let scrollIndex = this.getScrolledPanelIndex(scrollTop);
            if (this.scrollIndex != scrollIndex || this.focusVisible != focusVisible) {
                if (focusVisible) {
                    this.navigator.selectNavigationItem(this.focusIndex, true);
                } else {
                    this.navigator.selectNavigationItem(scrollIndex, true);
                }
                this.focusVisible = focusVisible;
                this.scrollIndex = scrollIndex;
            }
        }

        private isFocusedPanelVisible(scrollTop: number): boolean {
            if (this.focusIndex < 0) {
                return false;
            }

            let totalHeight = this.getScrollable().getEl().getHeight();
            let headerHeight = this.getFocusedHeaderHeight(this.focusIndex);
            let panelEl = this.getPanel(this.focusIndex).getEl();
            let panelTop = panelEl.getOffsetToParent().top - this.getScrollOffset() - headerHeight;
            let panelBottom = panelTop + panelEl.getHeight() - headerHeight;
            return panelEl.isVisible() && (( panelTop <= 0 && panelBottom > 0) || (panelTop <= totalHeight && panelBottom > totalHeight));
        }

        private getScrolledPanelIndex(scrollTop: number): number {
            let panelEl;
            let panelTop;
            let panelBottom;
            if (scrollTop == 0) {
                // select first element if we are in the beginning
                return 0;
            }
            for (let i = 0; i < this.getSize(); i++) {
                panelEl = this.getPanel(i).getEl();
                let headerHeight = this.getFocusedHeaderHeight(i);
                if (panelEl.isVisible()) {
                    panelTop = scrollTop + panelEl.getOffsetToParent().top - this.getScrollOffset() - headerHeight;
                    panelBottom = panelTop + panelEl.getHeight();
                    if (scrollTop >= panelTop && scrollTop < panelBottom) {
                        return i;
                    }
                }
            }
            return -1;
        }

        getSelectedNavigationItem(): NavigationItem {
            return this.navigator.getSelectedNavigationItem();
        }

        insertNavigablePanel(item: NavigationItem, panel: Panel, header: string, index: number, select?: boolean): number {
            this.navigator.insertNavigationItem(item, index);
            let panelHeader = select ? null : header;
            super.insertPanel(panel, index, panelHeader);

            // select corresponding step on focus
            panel.onFocus((event: FocusEvent) => {
                this.navigator.selectNavigationItem(item.getIndex(), true);
                this.focusIndex = item.getIndex();
            });
            panel.onBlur((event: FocusEvent) => {
                this.focusIndex = -1;
                // Update navigation item according to scroll position
                this.updateScrolledNavigationItem();
            });
            if (select) {
                this.selectPanel(item);
            }
            return index;
        }

        addNavigablePanel(item: NavigationItem, panel: Panel, header: string, select?: boolean): number {
            return this.insertNavigablePanel(item, panel, header, this.getPanels().length, select);
        }

        selectPanel(item: NavigationItem) {
            this.selectPanelByIndex(item.getIndex());
        }

        selectPanelByIndex(index: number) {
            this.navigator.selectNavigationItem(index);
            // panel will be shown because of the selected navigator listener in constructor
        }

        removeNavigablePanel(panel: Panel, checkCanRemovePanel: boolean = true): number {
            let removedPanelIndex = super.removePanel(panel, checkCanRemovePanel);
            if (removedPanelIndex > -1) {
                let navigationItem: api.ui.NavigationItem = this.navigator.getNavigationItem(removedPanelIndex);
                this.navigator.removeNavigationItem(navigationItem);
            }
            return removedPanelIndex;
        }

        private getFocusedHeaderHeight(curStrip: number): number {
            return this.getHeader(curStrip + 1) ? this.getHeader(curStrip + 1).getEl().getHeightWithBorder() :
                   this.getHeader(curStrip).getEl().getHeightWithBorder();
        }
    }

}
