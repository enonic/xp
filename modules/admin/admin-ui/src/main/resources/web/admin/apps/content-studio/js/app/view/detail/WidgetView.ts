import "../../../api.ts";

import ViewItem = api.app.view.ViewItem;
import ContentSummaryAndCompareStatus = api.content.ContentSummaryAndCompareStatus;
import RenderingMode = api.rendering.RenderingMode;
import Widget = api.content.Widget;
import {DetailsPanel} from "./DetailsPanel";
import {WidgetItemView} from "./WidgetItemView";

export class WidgetView extends api.dom.DivEl {

    private widgetName: string;

    private widgetItemViews: WidgetItemView[];

    private detailsPanel: DetailsPanel;

    private widget: Widget;

    private containerWidth: number = 0;

    private url: string = "";

    private contentId: string = "";

    private activationListeners: {() : void}[] = [];

    public static debug = false;

    constructor(builder: WidgetViewBuilder) {
        super("widget-view " + (builder.widget ? "external-widget" : "internal-widget"));

        this.detailsPanel = builder.detailsPanel;
        this.widgetName = builder.name;
        this.widgetItemViews = builder.widgetItemViews;
        this.widget = builder.widget;
        if (!this.widgetItemViews.length) {
            this.createDefaultWidgetItemView();
        }

        this.layout();

        this.applyConfig();

        this.onActivated(() => {
            this.updateWidgetItemViews();
        });
    }

    resetContainerWidth() {
        this.containerWidth = 0;
    }

    private applyConfig() {
        if (this.isUrlBased()) {
            var config = this.widget.getConfig();
            if (!!config && config.hasOwnProperty("render-on-resize") && config["render-on-resize"] == "true") {
                this.handleRerenderOnResize();
            }
        }
    }

    private handleRerenderOnResize() {
        var updateWidgetItemViewsHandler = () => {
            var containerWidth = this.detailsPanel.getEl().getWidth();
            if (this.detailsPanel.getItem() && containerWidth !== this.containerWidth) {
                this.updateWidgetItemViews(true);
            }
        }
        this.detailsPanel.onPanelSizeChanged(() => {
            if (this.isActive()) {
                updateWidgetItemViewsHandler();
            } else {
                var onActivatedHandler = () => {
                    updateWidgetItemViewsHandler();
                    this.unActivated(onActivatedHandler);
                }
                this.onActivated(onActivatedHandler);
            }
        });
    }

    private getWidgetUrl() {
        return api.rendering.UriHelper.getAdminUri(this.widget.getUrl(), "/");
    }

    private getFullUrl(url: string) {
        return url + "/" + this.detailsPanel.getEl().getWidth();
    }

    private updateCustomWidgetItemViews(force: boolean = false): wemQ.Promise<any>[] {
        var promises = [];

        this.url = this.getWidgetUrl();
        this.widgetItemViews.forEach((widgetItemView: WidgetItemView) => {
            promises.push(widgetItemView.setUrl(this.getFullUrl(this.url), this.contentId, force));
        });

        return promises;
    }

    public updateWidgetItemViews(force: boolean = false): wemQ.Promise<any> {
        var content = this.detailsPanel.getItem(),
            promises = [];

        if (this.widgetShouldBeUpdated(force)) {
            this.detailsPanel.showLoadMask();
            this.contentId = content.getId();

            if (this.isUrlBased()) {
                promises = promises.concat(this.updateCustomWidgetItemViews(force));
            } else {
                this.widgetItemViews.forEach((widgetItemView: WidgetItemView) => {
                    promises.push(widgetItemView.setContentAndUpdateView(content));
                });
            }
        }

        this.containerWidth = this.detailsPanel.getEl().getWidth();
        return wemQ.all(promises).finally(() => this.detailsPanel.hideLoadMask());
    }

    private widgetShouldBeUpdated(force: boolean = false): boolean {
        var content = this.detailsPanel.getItem();
        return content && this.detailsPanel.isVisibleOrAboutToBeVisible() &&
               (force || this.contentId !== content.getId() || (this.isUrlBased() && this.url !== this.getWidgetUrl()));
    }

    private createDefaultWidgetItemView() {
        this.widgetItemViews.push(new WidgetItemView());
        if (this.detailsPanel.getItem()) {
            this.updateWidgetItemViews();
        }
    }

    private layout(): wemQ.Promise<any> {

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
        this.notifyActivated();
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

    private isUrlBased(): boolean {
        return !!this.widget && !!this.widget.getUrl();
    }

    public getDetailsPanel(): DetailsPanel {
        return this.detailsPanel;
    }

    notifyActivated() {
        this.activationListeners.forEach((listener: ()=> void) => listener());
    }

    onActivated(listener: () => void) {
        this.activationListeners.push(listener);
    }

    unActivated(listener: ()=>void) {
        this.activationListeners = this.activationListeners.filter((currentListener: ()=>void) => {
            return currentListener != listener;
        });
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

    public setWidgetItemViews(widgetItemViews: WidgetItemView[]): WidgetViewBuilder {
        this.widgetItemViews = widgetItemViews;
        return this;
    }

    build(): WidgetView {
        return new WidgetView(this);
    }
}
