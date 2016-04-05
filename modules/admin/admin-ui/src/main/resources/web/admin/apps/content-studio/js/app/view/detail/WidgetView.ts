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

        private containerWidth: number = 0;

        private url: string = "";

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
            if (this.isUrlBased()) {
                this.detailsPanel.onPanelSizeChanged(() => {
                    var containerWidth = this.detailsPanel.getEl().getWidth();
                    if (this.detailsPanel.getItem() && containerWidth !== this.containerWidth) {
                        this.setContent(this.detailsPanel.getItem(), true);
                    }
                })
            }
        }

        resetContainerWidth() {
            this.containerWidth = 0;
        }

        private getWidgetUrl(content: ContentSummaryAndCompareStatus) {
            var path = content.getPath().toString();
            return api.rendering.UriHelper.getAdminUri(this.widget.getUrl(), path);
        }

        private getFullUrl(url: string) {
            return url + "/" + this.detailsPanel.getEl().getWidth();
        }

        private isDetailsPanelVisible(): boolean {
            return this.detailsPanel.getHTMLElement().clientWidth > 0;
        }

        private setContentForWidgetItemView(widgetItemView: WidgetItemView, content: ContentSummaryAndCompareStatus,
                                            force: boolean = false): wemQ.Promise<any> {
            if (!this.isUrlBased() || !this.isDetailsPanelVisible()) {
                return wemQ.resolve(null);
            }
            this.url = this.getWidgetUrl(content);
            return widgetItemView.setUrl(this.getFullUrl(this.url), force);
        }

        public setContent(content: ContentSummaryAndCompareStatus, force: boolean = false): wemQ.Promise<any> {
            var promises = [];
            this.widgetItemViews.forEach((widgetItemView: WidgetItemView) => {
                if (this.isUrlBased() && (force || this.url !== this.getWidgetUrl(content))) {
                    promises.push(this.setContentForWidgetItemView(widgetItemView, content, force));
                }
            });
            this.containerWidth = this.detailsPanel.getEl().getWidth();
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
            });

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
            this.getEl().setMaxHeightPx(this.getEl().getHeight()); // enables transition
            this.getEl().setMaxHeightPx(0);
        }

        slideIn() {
            if (this.hasDynamicHeight()) {
                this.redoLayout();
            }
            else {
                this.getEl().setMaxHeightPx(this.getParentElement().getEl().getHeight());
            }

            setTimeout(() => {
                this.getEl().setMaxHeight("none");
            }, 100);
        }

        setActive() {
            if (WidgetView.debug) {
                console.debug('WidgetView.setActive: ', this.getWidgetName());
            }
            if (this.isActive()) {
                return;
            }
            this.detailsPanel.setActiveWidget(this);
            this.slideIn();
        }

        setInactive() {
            if (WidgetView.debug) {
                console.debug('WidgetView.setInactive: ', this.getWidgetName());
            }
            this.detailsPanel.resetActiveWidget();
            this.slideOut();
        }

        private isActive() {
            return this.detailsPanel.getActiveWidget() == this;
        }

        private hasDynamicHeight(): boolean {
            return this.isUrlBased() && this.isActive();
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