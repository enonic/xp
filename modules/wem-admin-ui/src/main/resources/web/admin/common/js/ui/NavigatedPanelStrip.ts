module api.ui {

    export class NavigatedPanelStrip extends PanelStrip {

        navigator: Navigator;

        constructor(navigator: Navigator, className?: string) {
            super(className);
            this.navigator = navigator;
            var listenToScroll = true;

            navigator.onNavigationItemSelected((event: NavigatorEvent) => {
                listenToScroll = false;
                this.showPanelByIndex(event.getItem().getIndex());
            });

            this.onPanelShown((event: PanelShownEvent) => {
                listenToScroll = true;
            });

            jQuery(this.getHTMLElement()).scroll((event: JQueryEventObject) => {
                if (listenToScroll) {
                    var index = this.getScrolledPanelIndex((<Element> event.target).scrollTop);
                    if (index >= 0 && index < this.navigator.getSize()) {
                        this.navigator.selectNavigationItem(index, true);
                    }
                }
            });
        }

        private getScrolledPanelIndex(scrollTop: number): number {
            var panelEl, panelOffset, panelHeight;
            if (scrollTop == 0) {
                // select first element if we are in the beginning
                return 0;
            } else if (scrollTop + this.getEl().getHeight() == this.getHTMLElement().scrollHeight) {
                // select last element if we are in the very end
                return this.getSize() - 1;
            }
            for (var i = 0; i < this.getSize(); i++) {
                panelEl = this.getPanel(i).getEl();
                panelOffset = scrollTop + panelEl.getOffsetToParent().top;
                panelHeight = panelEl.getHeight();
                if (scrollTop >= panelOffset && scrollTop < (panelOffset + panelHeight)) {
                    return i;
                }
            }
            return -1;
        }

        getSelectedNavigationItem(): NavigationItem {
            return this.navigator.getSelectedNavigationItem();
        }

        addNavigablePanel(item: NavigationItem, panel: Panel, select?: boolean): number {
            this.navigator.addNavigationItem(item);
            var index = super.addPanel(panel);

            if (select) {
                this.selectPanel(item);
            }
            return index;
        }

        selectPanel(item: NavigationItem) {
            this.selectPanelByIndex(item.getIndex());
        }

        selectPanelByIndex(index: number) {
            this.navigator.selectNavigationItem(index);
            // panel will be shown because of the selected navigator listener in constructor
        }

        removeNavigablePanel(panel: Panel, checkCanRemovePanel: boolean = true): number {
            var removedPanelIndex = super.removePanel(panel, checkCanRemovePanel);
            if (removedPanelIndex > -1) {
                var navigationItem: api.ui.NavigationItem = this.navigator.getNavigationItem(removedPanelIndex);
                this.navigator.removeNavigationItem(navigationItem);
            }
            return removedPanelIndex;
        }
    }

}