module app.view.detail {

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
                    this.widget.setActive();
                } else {
                    this.widget.setInactive();
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