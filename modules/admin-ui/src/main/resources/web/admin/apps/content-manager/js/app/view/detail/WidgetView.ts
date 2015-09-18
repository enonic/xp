module app.view.detail {

    import Widget = api.content.Widget;
    import ViewItem = api.app.view.ViewItem;
    import ContentSummary = api.content.ContentSummary;
    import RenderingMode = api.rendering.RenderingMode;

    export class WidgetView extends api.dom.DivEl {

        private widgetToggleButton: WidgetViewToggleButton;
        private widgetName: string;
        private widgetContents: api.dom.DivEl = new api.dom.DivEl("widget-content");
        private animationTimer;
        private detailsPanel: DetailsPanel;
        private normalHeightOfContent: number;

        constructor(name: string, detailsPanel: DetailsPanel, useToggleButton: boolean = true) {
            super("widget");
            this.detailsPanel = detailsPanel;

            this.widgetName = name;
            if (useToggleButton) {
                this.initWidgetToggleButton();
            }

            this.slideOut();
            this.appendChild(this.widgetContents);
        }

        static fromWidget(widget: Widget, detailsPanel: DetailsPanel, useToggleButton: boolean = true): WidgetView {

            var item: ViewItem<ContentSummary> = detailsPanel.getItem();

            var widgetView = new WidgetView(widget.getDisplayName(), detailsPanel, useToggleButton),
                widgetViewContent: api.dom.Element = item ? new api.dom.IFrameEl() : new api.dom.DivEl();

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

        updateNormalHeightSilently() {
            this.widgetContents.setVisible(false);
            var currentHeight = this.widgetContents.getEl().getHeight();
            this.widgetContents.getEl().setHeight("auto");
            this.normalHeightOfContent = this.widgetContents.getEl().getHeightWithBorder();
            this.widgetContents.getEl().setHeightPx(currentHeight);
            this.widgetContents.setVisible(true);
        }

        updateNormalHeight() {
            this.widgetContents.getEl().setHeight("auto");
            this.normalHeightOfContent = this.widgetContents.getEl().getHeightWithBorder();
            this.widgetContents.getEl().setHeightPx(this.normalHeightOfContent);
        }

        getWidgetName(): string {
            return this.widgetName;
        }

        setWidgetContents(value: api.dom.Element) {
            this.widgetContents.appendChild(value);
        }

        slideOut() {
            this.widgetContents.getEl().setHeightPx(0);
        }

        slideIn() {
            this.widgetContents.getEl().setHeightPx(this.normalHeightOfContent);
        }

        setActive() {
            this.detailsPanel.setActiveWidget(this);
            this.slideIn();
        }

        setInactive() {
            this.detailsPanel.resetActiveWidget();
            this.deactivate();
        }

        deactivate() {
            this.slideOut();
            this.removeClass("expanded");
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
