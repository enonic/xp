module app.view.widget {

    export class WidgetsPanel extends api.ui.panel.Panel {

        private widgets: Widget[] = [];
        private labelEl: api.dom.SpanEl;
        private widgetsContainer: api.dom.DivEl = new api.dom.DivEl("widgets-container");
        private animationTimer;

        constructor(name?: string) {
            super("widgets-panel");
            this.setDoOffset(false);

            api.ui.responsive.ResponsiveManager.onAvailableSizeChanged(this, (item: api.ui.responsive.ResponsiveItem) => {

            });

            this.onShown((event) => {

            });

            this.labelEl = new api.dom.SpanEl("widgets-panel-label");
            if (name) {
                this.labelEl.setHtml(name);
            }
            this.appendChild(this.labelEl);
            this.appendChild(this.widgetsContainer)
        }

        removeWidgets() {
            this.widgetsContainer.removeChildren();
        }

        addWidget(widget: Widget) {
            this.widgets.push(widget);
            this.widgetsContainer.appendChild(widget);
        }

        setName(name: string) {
            this.labelEl.setHtml(name);
        }

        slideOut() {
            this.getEl().setRightPx(-this.getEl().getWidthWithBorder());
            // there is a 100ms animation so wait until it's finished
            if (this.animationTimer) {
                clearTimeout(this.animationTimer);
            }
            this.animationTimer = setTimeout(() => {
                // this.updateFrameSize();
                this.animationTimer = null;
            }, 100);
        }

        slideIn() {
            this.getEl().setRightPx(0);
            // there is a 100ms animation so wait until it's finished
            if (this.animationTimer) {
                clearTimeout(this.animationTimer);
            }
            this.animationTimer = setTimeout(() => {
                // this.updateFrameSize();
                this.animationTimer = null
            }, 100);
        }
    }
}
