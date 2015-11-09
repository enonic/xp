module app.view.detail {

    import ViewItem = api.app.view.ViewItem;
    import ContentSummary = api.content.ContentSummary;
    import RenderingMode = api.rendering.RenderingMode;

    export class WidgetView extends api.dom.DivEl {

        private widgetName: string;

        private widgetItemViews: WidgetItemView[];

        private detailsPanel: DetailsPanel;

        public static debug = false;

        constructor(builder: WidgetViewBuilder) {
            super("widget-view");

            this.detailsPanel = builder.detailsPanel;
            this.widgetName = builder.name;
            this.widgetItemViews = builder.widgetItemViews;

            this.layout();
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

        private calcHeight(): number {
            var originalHeight = this.getEl().getHeight();
            if (originalHeight == 0) {
                // prevent jitter if widget is collapsed
                this.setVisible(false);
            }
            this.getEl().setHeight("auto");
            var height = this.getEl().getHeight();
            this.getEl().setHeightPx(originalHeight);
            if (originalHeight == 0) {
                this.setVisible(true);
            }
            if (WidgetView.debug) {
                console.debug('WidgetView.calcHeight: ', height, 'originalHeight: ', originalHeight);
            }
            return height;
        }

        getWidgetName(): string {
            return this.widgetName;
        }

        slideOut() {
            this.getEl().setHeightPx(0);
        }

        slideIn() {
            this.getEl().setHeightPx(this.calcHeight());
        }

        setActive() {
            if (WidgetView.debug) {
                console.debug('WidgetView.setActive: ', this);
            }
            this.detailsPanel.setActiveWidget(this);
            this.slideIn();
        }

        setInactive() {
            if (WidgetView.debug) {
                console.debug('WidgetView.setInactive: ', this);
            }
            this.detailsPanel.resetActiveWidget();
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

        build(): WidgetView {
            return new WidgetView(this);
        }
    }
}