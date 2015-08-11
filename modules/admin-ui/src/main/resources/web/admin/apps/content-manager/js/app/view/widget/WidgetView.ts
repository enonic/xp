module app.view.widget {

    import Widget = api.content.Widget;

    export class WidgetView extends api.dom.DivEl {

        private widgetToggleButton: WidgetViewToggleButton;
        private widgetName: string;
        private widgetContents: api.dom.DivEl = new api.dom.DivEl("widget-content");
        private animationTimer;

        constructor(name: string) {
            super("widget");

            this.widgetName = name;
            this.initWidgetToggleButton();
            this.widgetContents.setVisible(false);
            this.appendChild(this.widgetContents);
        }

        static fromWidget(widget: Widget): WidgetView {
            var testWidget2 = new WidgetView("Widget Y"),
                testWidgetContent2 = new api.dom.DivEl();

            testWidgetContent2.setHtml("Some test contents");

            testWidget2.setWidgetContents(testWidgetContent2);

            return testWidget2;
        }

        private initWidgetToggleButton() {

            this.widgetToggleButton = new WidgetViewToggleButton(this);
            this.widgetToggleButton.setLabel(this.widgetName);
            this.appendChild(this.widgetToggleButton);
        }

        setWidgetContents(value: api.dom.Element) {
            this.widgetContents.appendChild(value);
        }

        slideOut() {
            this.widgetContents.setVisible(false);
            /*this.widgetContents.getEl().getHTMLElement().style.maxHeight = "0px";
             // there is a 100ms animation so wait until it's finished
             if (this.animationTimer) {
             clearTimeout(this.animationTimer);
             }
             this.animationTimer = setTimeout(() => {
             // this.updateFrameSize();
             this.animationTimer = null;
             }, 100);*/
        }

        slideIn() {
            this.widgetContents.setVisible(true);
            /*this.widgetContents.getEl().getHTMLElement().style.maxHeight = "600px";
             // there is a 100ms animation so wait until it's finished
             if (this.animationTimer) {
             clearTimeout(this.animationTimer);
             }
             this.animationTimer = setTimeout(() => {
             // this.updateFrameSize();
             this.animationTimer = null
             }, 100);*/
        }

    }

    export class WidgetViewToggleButton extends api.dom.DivEl {

        private labelEl: api.dom.SpanEl;
        private widget: WidgetView;

        constructor(widget: WidgetView) {
            super("widget-toggle-button");

            this.widget = widget;

            this.labelEl = new api.dom.SpanEl('label');
            this.appendChild(this.labelEl);

            this.onClicked((event) => {
                this.widget.toggleClass("expanded");

                if (this.widget.hasClass("expanded")) {
                    this.widget.slideIn();
                } else {
                    this.widget.slideOut();
                }
            });
        }

        setLabel(value: string, addTitle: boolean = true) {
            this.labelEl.setHtml(value, true);
            if (addTitle) {
                this.labelEl.getEl().setAttribute('title', value);
            }
        }
    }
}
