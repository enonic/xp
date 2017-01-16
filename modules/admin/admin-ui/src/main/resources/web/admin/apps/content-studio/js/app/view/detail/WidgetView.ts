import "../../../api.ts";
import {DetailsView} from "./DetailsView";
import {WidgetItemView} from "./WidgetItemView";
import {ActiveDetailsPanelManager} from "../../view/detail/ActiveDetailsPanelManager";

import ViewItem = api.app.view.ViewItem;
import ContentSummaryAndCompareStatus = api.content.ContentSummaryAndCompareStatus;
import RenderingMode = api.rendering.RenderingMode;
import Widget = api.content.Widget;

export class WidgetView extends api.dom.DivEl {

    private widgetName: string;

    private widgetItemViews: WidgetItemView[];

    private detailsView: DetailsView;

    private widget: Widget;

    private containerWidth: number = 0;

    private url: string = '';

    private content: ContentSummaryAndCompareStatus;

    private activationListeners: {() : void}[] = [];

    public static debug: boolean = false;

    constructor(builder: WidgetViewBuilder) {
        super('widget-view ' + (builder.widget ? 'external-widget' : 'internal-widget'));

        this.detailsView = builder.detailsView;
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
            let config = this.widget.getConfig();
            if (!!config && config.hasOwnProperty('render-on-resize') && config['render-on-resize'] == 'true') {
                this.handleRerenderOnResize();
            }
        }
    }

    private handleRerenderOnResize() {
        let updateWidgetItemViewsHandler = () => {
            let containerWidth = this.detailsView.getEl().getWidth();
            if (this.detailsView.getItem() && containerWidth !== this.containerWidth) {
                this.updateWidgetItemViews(true);
            }
        };
        this.detailsView.onPanelSizeChanged(() => {
            if (this.isActive()) {
                updateWidgetItemViewsHandler();
            } else {
                let onActivatedHandler = () => {
                    updateWidgetItemViewsHandler();
                    this.unActivated(onActivatedHandler);
                };
                this.onActivated(onActivatedHandler);
            }
        });
    }

    private getWidgetUrl() {
        return api.rendering.UriHelper.getAdminUri(this.widget.getUrl(), '/');
    }

    private getFullUrl(url: string) {
        return url + '/' + this.detailsView.getEl().getWidth();
    }

    private updateCustomWidgetItemViews(force: boolean = false): wemQ.Promise<any>[] {
        let promises = [];

        this.url = this.getWidgetUrl();
        this.widgetItemViews.forEach((widgetItemView: WidgetItemView) => {
            promises.push(widgetItemView.setUrl(this.getFullUrl(this.url), this.content.getContentId().toString(), force));
        });

        return promises;
    }

    public updateWidgetItemViews(force: boolean = false): wemQ.Promise<any> {
        let content = this.detailsView.getItem();
        let promises = [];

        if (this.widgetShouldBeUpdated(force)) {
            this.detailsView.showLoadMask();
            this.content = content;

            if (this.isUrlBased()) {
                promises = promises.concat(this.updateCustomWidgetItemViews(force));
            } else {
                this.widgetItemViews.forEach((widgetItemView: WidgetItemView) => {
                    promises.push(widgetItemView.setContentAndUpdateView(content));
                });
            }
        }

        this.containerWidth = this.detailsView.getEl().getWidth();
        return wemQ.all(promises).finally(() => this.detailsView.hideLoadMask());
    }

    private widgetShouldBeUpdated(force: boolean = false): boolean {
        let content = this.detailsView.getItem();
        return content && ActiveDetailsPanelManager.getActiveDetailsPanel().isVisibleOrAboutToBeVisible() &&
               (force || !api.ObjectHelper.equals(this.content, content) || (this.isUrlBased() && this.url !== this.getWidgetUrl()));
    }

    private createDefaultWidgetItemView() {
        this.widgetItemViews.push(new WidgetItemView());
        if (this.detailsView.getItem()) {
            this.updateWidgetItemViews();
        }
    }

    private layout(): wemQ.Promise<any> {

        this.slideOut();

        let layoutTasks: wemQ.Promise<any>[] = [];

        this.widgetItemViews.forEach((itemView: WidgetItemView) => {
            this.appendChild(itemView);
            layoutTasks.push(itemView.layout());
        });

        return wemQ.all(layoutTasks);
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
        } else {
            this.getEl().setMaxHeightPx(this.getParentElement().getEl().getHeight());
        }

        setTimeout(() => {
            this.getEl().setMaxHeight('none');
        }, 100);
    }

    setActive() {
        if (WidgetView.debug) {
            console.debug('WidgetView.setActive: ', this.getWidgetName());
        }
        if (this.isActive()) {
            return;
        }
        this.detailsView.setActiveWidget(this);
        this.notifyActivated();
        this.slideIn();
    }

    setInactive() {
        if (WidgetView.debug) {
            console.debug('WidgetView.setInactive: ', this.getWidgetName());
        }
        this.detailsView.resetActiveWidget();
        this.slideOut();
    }

    private isActive() {
        return this.detailsView.getActiveWidget() == this;
    }

    private hasDynamicHeight(): boolean {
        return this.isUrlBased() && this.isActive();
    }

    private redoLayout() {
        let firstItemView = this.widgetItemViews[0];
        if (!firstItemView) {
            return;
        }
        this.getEl().setHeight('');
        firstItemView.hide();
        setTimeout(() => {
            firstItemView.show();
        }, 200);
    }

    private isUrlBased(): boolean {
        return !!this.widget && !!this.widget.getUrl();
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

    detailsView: DetailsView;

    widgetItemViews: WidgetItemView[] = [];

    widget: Widget;

    public setName(name: string): WidgetViewBuilder {
        this.name = name;
        return this;
    }

    public setDetailsView(detailsView: DetailsView): WidgetViewBuilder {
        this.detailsView = detailsView;
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
