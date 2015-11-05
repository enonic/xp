module app.view.detail {

    import ViewItem = api.app.view.ViewItem;
    import ContentSummary = api.content.ContentSummary;
    import RenderingMode = api.rendering.RenderingMode;

    export class WidgetView extends api.dom.DivEl {

        private widgetName: string;

        private widgetItemViews: WidgetItemView[];

        private detailsPanel: DetailsPanel;

        private normalHeightOfContent: number;

        private layoutCallbackFunction: () => void;

        constructor(builder: WidgetViewBuilder) {
            super("widget-view");

            this.detailsPanel = builder.detailsPanel;
            this.widgetName = builder.name;
            this.widgetItemViews = builder.widgetItemViews;
            this.layoutCallbackFunction = builder.layoutCallbackFunction;

            this.layout().done(() => {
                if (this.layoutCallbackFunction) {
                    this.layoutCallbackFunction();
                }
            });
        }

        public layout(): wemQ.Promise<any> {

            this.slideOut();

            var layoutTasks: wemQ.Promise<any>[] = [];

            if (this.widgetItemViews) {
                this.widgetItemViews.forEach((itemView: WidgetItemView) => {
                    this.appendChild(itemView);
                    layoutTasks.push(itemView.layout());
                })
            }

            return wemQ.all(layoutTasks);
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
        }

        public static create(): WidgetViewBuilder {
            return new WidgetViewBuilder();
        }
    }

    export class WidgetViewBuilder {

        name: string;

        detailsPanel: DetailsPanel;

        widgetItemViews: WidgetItemView[] = [];

        layoutCallbackFunction: () => void;

        public setName(name: string): WidgetViewBuilder {
            this.name = name;
            return this;
        }

        public setDetailsPanel(detailsPanel: DetailsPanel): WidgetViewBuilder {
            this.detailsPanel = detailsPanel;
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

        public setLayoutCallbackFunction(layoutCallbackFunction: () => void): WidgetViewBuilder {
            this.layoutCallbackFunction = layoutCallbackFunction;
            return this;
        }

        build(): WidgetView {
            return new WidgetView(this);
        }
    }
}