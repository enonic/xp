module app.view.widget {

    export class WidgetToggleButton extends api.dom.DivEl {

        private labelEl: api.dom.SpanEl;
        private widget: Widget;

        constructor(widget: Widget) {
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
