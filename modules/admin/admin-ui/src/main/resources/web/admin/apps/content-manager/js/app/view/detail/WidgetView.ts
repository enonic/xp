module app.view.detail {

    import ViewItem = api.app.view.ViewItem;
    import ContentSummaryAndCompareStatus = api.content.ContentSummaryAndCompareStatus;
    import RenderingMode = api.rendering.RenderingMode;
    import Widget = api.content.Widget;

    export class WidgetView extends api.dom.DivEl {

        private widgetName: string;

        private widgetItemViews: WidgetItemView[];

        private detailsPanel: DetailsPanel;

        private widget: Widget;

        public static debug = false;

        constructor(builder: WidgetViewBuilder) {
            super("widget-view " + (builder.widget ? "external-widget" : "internal-widget"));

            this.detailsPanel = builder.detailsPanel;
            this.widgetName = builder.name;
            this.widgetItemViews = builder.widgetItemViews;
            this.widget = builder.widget;
            if (!this.widgetItemViews.length) {
                this.createWidgetItemView();
            }

            this.layout();
        }

        private setContentForWidgetItemView(widgetItemView: WidgetItemView, content: ContentSummaryAndCompareStatus): wemQ.Promise<any> {
            if (!this.isUrlBased()) {
                return wemQ.resolve(null);
            }
            var path = content.getPath().getFirstElement();
            return widgetItemView.setUrl(this.widget.getUrl(), path);
        }

        public setContent(content: ContentSummaryAndCompareStatus): wemQ.Promise<any> {
            var promises = [];
            this.widgetItemViews.forEach((widgetItemView: WidgetItemView) => {
                promises.push(this.setContentForWidgetItemView(widgetItemView, content));
            });
            return wemQ.all(promises);
        }

        private createWidgetItemView() {
            var widgetItemView = new WidgetItemView();
            if (this.detailsPanel.getItem()) {
                this.setContentForWidgetItemView(widgetItemView, this.detailsPanel.getItem());
            }

            this.widgetItemViews.push(widgetItemView);
        }

        public layout(): wemQ.Promise<any> {

            this.slideOut();

            var layoutTasks: wemQ.Promise<any>[] = [];

            this.widgetItemViews.forEach((itemView: WidgetItemView) => {
                this.appendChild(itemView);
                layoutTasks.push(itemView.layout());
            })

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
            if (this.hasDynamicHeight()) {
                this.redoLayout();
            }
            else {
                this.getEl().setHeightPx(this.calcHeight());
            }
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

        private isActive() {
            return this.detailsPanel.getActiveWidget() == this;
        }

        private hasDynamicHeight(): boolean {
            return (this.getEl().getHeight() == 0 && this.isUrlBased() && this.isActive());
        }

        private redoLayout() {
            var firstItemView = this.widgetItemViews[0];
            if (!firstItemView) {
                return;
            }
            this.getEl().setHeight("");
            firstItemView.hide();
            setTimeout(() => {
                firstItemView.show();
            }, 200);
        }

        public isUrlBased(): boolean {
            return !!this.widget && !!this.widget.getUrl();
        }

        public static create(): WidgetViewBuilder {
            return new WidgetViewBuilder();
        }
    }

    export class WidgetViewBuilder {

        name: string;

        detailsPanel: DetailsPanel;

        widgetItemViews: WidgetItemView[] = [];

        widget: Widget;

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

        public setWidget(widget: Widget): WidgetViewBuilder {
            this.widget = widget;
            return this;
        }

        build(): WidgetView {
            return new WidgetView(this);
        }
    }
}