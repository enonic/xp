module app.view.detail {

    import Widget = api.content.Widget;
    import ViewItem = api.app.view.ViewItem;
    import ContentSummary = api.content.ContentSummary;
    import RenderingMode = api.rendering.RenderingMode;

    export class WidgetView extends api.dom.DivEl {

        private widgetToggleButton: WidgetViewToggleButton;

        private widgetName: string;

        private widgetItemViews: WidgetItemView[];

        private detailsPanel: DetailsPanel;

        private normalHeightOfContent: number;


        constructor(builder: WidgetViewBuilder) {
            super("widget-view");

            this.detailsPanel = builder.detailsPanel;
            this.widgetName = builder.name;
            this.widgetItemViews = builder.widgetItemViews;

            if (builder.useToggleButton) {
                this.initWidgetToggleButton();
            }
            this.layout();
        }

        public layout() {

            this.slideOut();

            if (this.widgetItemViews) {
                this.widgetItemViews.forEach((itemView: WidgetItemView) => {
                    this.appendChild(itemView);
                    itemView.layout();
                })
            }
        }

        private initWidgetToggleButton() {

            this.widgetToggleButton = new WidgetViewToggleButton(this);
            this.widgetToggleButton.setLabel(this.widgetName);
            this.appendChild(this.widgetToggleButton);
        }

        updateNormalHeightSilently() {
            this.setVisible(false);
            var currentHeight = this.getEl().getHeight();
            this.getEl().setHeight("auto");
            this.normalHeightOfContent = this.getEl().getHeightWithBorder();
            this.getEl().setHeightPx(currentHeight);
            this.setVisible(true);
        }

        updateNormalHeight() {
            this.getEl().setHeight("auto");
            this.normalHeightOfContent = this.getEl().getHeightWithBorder();
            this.getEl().setHeightPx(this.normalHeightOfContent);
        }

        getWidgetName(): string {
            return this.widgetName;
        }

        slideOut() {
            this.getEl().setHeightPx(0);
        }

        slideIn() {
            this.getEl().setHeightPx(this.normalHeightOfContent);
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

        public static create(): WidgetViewBuilder {
            return new WidgetViewBuilder();
        }
    }

    export class WidgetViewBuilder {

        name: string;

        detailsPanel: DetailsPanel;

        useToggleButton: boolean = true;

        widgetItemViews: WidgetItemView[] = [];

        public setName(name: string): WidgetViewBuilder {
            this.name = name;
            return this;
        }

        public setDetailsPanel(detailsPanel: DetailsPanel): WidgetViewBuilder {
            this.detailsPanel = detailsPanel;
            return this;
        }

        public setUseToggleButton(useToggleButton: boolean): WidgetViewBuilder {
            this.useToggleButton = useToggleButton;
            return this;
        }

        public addWidgetItemView(widgetItemView: WidgetItemView): WidgetViewBuilder {
            this.widgetItemViews.push(widgetItemView);
            return this;
        }

        public setWidgetItemViews(widgetItemViews: WidgetItemView[]): WidgetViewBuilder {
            this.widgetItemViews = widgetItemViews;
            return this;
        }

        build(): WidgetView {
            return new WidgetView(this);
        }
    }
}