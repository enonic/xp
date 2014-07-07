module api.ui {

    export class PanelStrip extends Panel {

        private panels: Panel[] = [];

        private scrollable: api.dom.DivEl;

        private panelShown: Panel = null;

        private panelShownListeners: {(event: PanelShownEvent):void}[] = [];

        constructor(scrollable?: api.dom.Element, className?: string) {
            super("panel-strip" + (className ? " " + className : ""));
            if (scrollable) {
                this.scrollable = scrollable;
                this.scrollable.addClass('panel-strip-scrollable');
            } else {
                this.scrollable = this;
            }

            this.onShown(() => {
                this.updateLastPanelHeight();
            })
        }

        addPanel(panel: Panel): number {
            panel.setDoOffset(false);
            this.appendChild(panel);
            var index = this.panels.push(panel) - 1;
            if (this.isVisible()) {
                this.updateLastPanelHeight();
            }
            return index;
        }

        getPanels(): api.ui.Panel[] {
            return this.panels;
        }

        getScrollable(): api.dom.DivEl {
            return this.scrollable;
        }

        private updateLastPanelHeight() {
            if (this.getSize() > 1) {
                // restore the one before last panel's height if needed
                var beforeLastEl = this.getPanel(this.getSize() - 2).getEl();
                var originalHeight = beforeLastEl.getData("originalHeight");
                if (originalHeight) {
                    beforeLastEl.setHeight(originalHeight);
                }
            }
            // set the last panel height equal to that of the scrollable
            var lastEl = this.getPanel(this.getSize() - 1).getEl();
            if (!lastEl.getData("originalHeight")) {
                lastEl.setData("originalHeight", lastEl.getHTMLElement().style.height || "auto");
            }
            lastEl.setHeightPx(this.scrollable.getEl().getHeight());
        }

        removePanel(panelToRemove: Panel, checkCanRemovePanel: boolean = true): number {

            var index: number = this.getPanelIndex(panelToRemove);
            if (index < 0 || checkCanRemovePanel && !this.canRemovePanel(panelToRemove)) {
                return -1;
            }
            this.removeChild(panelToRemove);
            this.panels.splice(index, 1);

            if (this.isEmpty()) {
                this.panelShown = null;
            } else if (panelToRemove == this.getPanelShown()) {
                // show either panel that has the same index now or the last panel
                this.showPanelByIndex(Math.min(index, this.getSize() - 1));
            }

            if (this.isVisible() && index == this.getSize() && !this.isEmpty()) {
                // update if last panel was removed and there are still left
                this.updateLastPanelHeight();
            }

            return index;
        }

        /*
         * Override this method to decide whether given panel at given index can be removed or not. Default is true.
         */
        canRemovePanel(panel: Panel): boolean {
            return true;
        }

        isEmpty(): boolean {
            return this.panels.length == 0;
        }

        getSize(): number {
            return this.panels.length;
        }

        getPanel(index: number): Panel {
            return this.panels[index];
        }

        getPanelShown(): Panel {
            return this.panelShown;
        }

        getPanelShownIndex(): number {
            return this.getPanelIndex(this.panelShown);
        }

        getPanelIndex<T extends api.ui.Panel>(panel: T): number {
            var size = this.getSize();
            for (var i = 0; i < size; i++) {
                if (this.panels[i] === panel) {
                    return i;
                }
            }
            return -1;
        }

        showPanel(panel: Panel) {
            var index = this.getPanelIndex(panel);
            if (index > -1) {
                this.showPanelByIndex(index);
            }
        }

        showPanelByIndex(index: number) {

            var panelToShow = this.getPanel(index);
            if (panelToShow == null) {
                return;
            }

            wemjq(this.scrollable.getHTMLElement()).animate({
                scrollTop: index == 0 ? 0 : this.scrollable.getHTMLElement().scrollTop + panelToShow.getEl().getOffsetToParent().top
            }, {
                duration: 500,
                complete: () => {
                    this.notifyPanelShown(panelToShow, index, this.getPanelShown());
                    this.panelShown = panelToShow;
                }
            });
        }

        onPanelShown(listener: (event: PanelShownEvent)=>void) {
            this.panelShownListeners.push(listener);
        }

        unPanelShown(listener: (event: PanelShownEvent)=>void) {
            this.panelShownListeners = this.panelShownListeners.filter((currentListener: (event: PanelShownEvent) => void) => {
                return  listener != currentListener;
            });
        }

        private notifyPanelShown(panel: Panel, panelIndex: number, previousPanel: Panel) {
            this.panelShownListeners.forEach((listener: (event: PanelShownEvent) => void) => {
                listener.call(this, new PanelShownEvent(panel, panelIndex, previousPanel));
            });
        }

    }

}