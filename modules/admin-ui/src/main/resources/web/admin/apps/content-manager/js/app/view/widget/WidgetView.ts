module app.view.widget {

    import Widget = api.content.Widget;
    import ViewItem = api.app.view.ViewItem;
    import ContentSummary = api.content.ContentSummary;
    import RenderingMode = api.rendering.RenderingMode;

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

        static fromWidget(widget: Widget, item?: ViewItem<ContentSummary>): WidgetView {
            var widgetView = new WidgetView(widget.getDisplayName()),
                widgetViewContent : api.dom.Element = item ? new api.dom.IFrameEl() : new api.dom.DivEl();

            if (item) {
                (<api.dom.IFrameEl>widgetViewContent).setSrc(widgetView.getWidgetSrc(item));
            }
            else {
                widgetViewContent.setHtml("Some test contents");
            }

            widgetView.setWidgetContents(widgetViewContent);

            return widgetView;
        }

        private getWidgetSrc(item: ViewItem<ContentSummary>): string {
            var path = item.getModel().isSite() ? item.getPath() : item.getPath().substring(0, item.getPath().indexOf("/", 1));

            return api.rendering.UriHelper.getPortalUri(path, RenderingMode.PREVIEW, api.content.Branch.DRAFT);
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
