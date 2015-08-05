module app.view.widget {

    export class Widget extends api.dom.DivEl {

        private widgetToggleButton: WidgetToggleButton;
        private widgetName: string;
        private widgetContents: api.dom.DivEl = new api.dom.DivEl("widget-content");
        private animationTimer;

        constructor(name: string) {
            super("widget");

            this.widgetName = name;
            this.initWidgetToggleButton();
            this.appendChild(this.widgetContents);
        }

        private initWidgetToggleButton() {

            this.widgetToggleButton = new WidgetToggleButton(this);
            this.widgetToggleButton.setLabel(this.widgetName);
            this.appendChild(this.widgetToggleButton);
        }

        setWidgetContents(value: api.dom.Element) {
            this.widgetContents.appendChild(value);
        }

        slideOut() {
            this.widgetContents.getEl().getHTMLElement().style.maxHeight = "0px";
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
            this.widgetContents.getEl().getHTMLElement().style.maxHeight = "300px";
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
