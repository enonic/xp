import "../../../api.ts";
import {WidgetView} from "./WidgetView";
import {WidgetsSelectionRow} from "./WidgetsSelectionRow";
import {VersionsWidgetItemView} from "./widget/version/VersionsWidgetItemView";
import {DependenciesWidgetItemView} from "./widget/dependency/DependenciesWidgetItemView";
import {StatusWidgetItemView} from "./widget/info/StatusWidgetItemView";
import {PropertiesWidgetItemView} from "./widget/info/PropertiesWidgetItemView";
import {AttachmentsWidgetItemView} from "./widget/info/AttachmentsWidgetItemView";
import {UserAccessWidgetItemView} from "./widget/info/UserAccessWidgetItemView";

import ResponsiveManager = api.ui.responsive.ResponsiveManager;
import ResponsiveItem = api.ui.responsive.ResponsiveItem;
import ViewItem = api.app.view.ViewItem;
import ContentSummaryAndCompareStatus = api.content.ContentSummaryAndCompareStatus;
import CompareStatus = api.content.CompareStatus;
import Widget = api.content.Widget;
import ContentSummaryViewer = api.content.ContentSummaryViewer;

export class DetailsPanel extends api.ui.panel.Panel {

    private widgetViews: WidgetView[] = [];
    private viewer: ContentSummaryViewer;
    private detailsContainer: api.dom.DivEl = new api.dom.DivEl("details-container");
    private widgetsSelectionRow: WidgetsSelectionRow;

    private splitter: api.dom.DivEl;
    private ghostDragger: api.dom.DivEl;
    private mask: api.ui.mask.DragMask;
    private loadMask: api.ui.mask.LoadMask;
    private divForNoSelection: api.dom.DivEl;

    private actualWidth: number;
    private minWidth: number = 280;
    private parentMinWidth: number = 15;

    private sizeChangedListeners: {() : void}[] = [];
    private slidedInListeners: {() : void}[] = [];

    private item: ContentSummaryAndCompareStatus;

    private useViewer: boolean;
    private useSplitter: boolean;
    private slideInFunction: () => void;
    private slideOutFunction: () => void;

    private activeWidget: WidgetView;
    private defaultWidgetView: WidgetView;

    private alreadyFetchedCustomWidgets: boolean;

    private slidedIn: boolean;

    public static debug = false;

    constructor(builder: Builder) {
        super("details-panel");
        this.setDoOffset(false);
        this.initSlideFunctions(builder.getSlideFrom());
        this.useSplitter = builder.isUseSplitter();

        this.appendChild(this.loadMask = new api.ui.mask.LoadMask(this));
        this.loadMask.addClass("details-panel-mask");

        this.ghostDragger = new api.dom.DivEl("ghost-dragger");

        if (this.useSplitter) {
            this.splitter = new api.dom.DivEl("splitter");
            this.appendChild(this.splitter);
            this.onRendered(() => this.onRenderedHandler());
        }

        this.initViewer(builder.isUseViewer());
        this.initDefaultWidgetView();
        this.initCommonWidgetViews();
        this.initDivForNoSelection();
        this.initWidgetsSelectionRow();

        this.appendChild(this.detailsContainer);
        this.appendChild(this.divForNoSelection);

        this.subscribeOnEvents();

        this.layout();
    }

    private subscribeOnEvents() {
        this.onPanelSizeChanged(() => this.setDetailsContainerHeight());

        this.onSlidedIn(() => !!this.item ? this.updateActiveWidget() : null);

        this.onShown(() => {
            if (!!this.item) {
                setTimeout(() =>  this.updateActiveWidget(), 50); // small delay so that isVisiblieOrAboutToBeVisible() check detects width change
            }
        });
    }

    private initDivForNoSelection() {
        this.divForNoSelection = new api.dom.DivEl("no-selection-message");
        this.divForNoSelection.getEl().setInnerHtml("Select an item - and we'll show you the details!");
        this.appendChild(this.divForNoSelection);
    }

    private initWidgetsSelectionRow() {
        this.widgetsSelectionRow = new WidgetsSelectionRow(this);
        this.appendChild(this.widgetsSelectionRow);
        this.widgetsSelectionRow.updateState(this.activeWidget);
    }

    getCustomWidgetViewsAndUpdateDropdown(): wemQ.Promise<void> {
        var deferred = wemQ.defer<void>();
        if (!this.alreadyFetchedCustomWidgets) {
            this.getAndInitCustomWidgetViews().done(() => {
                this.widgetsSelectionRow.updateWidgetsDropdown(this.widgetViews);
                this.updateActiveWidget();
                this.alreadyFetchedCustomWidgets = true;
                deferred.resolve(null);
            });
        }
        else {
            deferred.resolve(null);
        }
        return deferred.promise;
    }

    setActiveWidget(widgetView: WidgetView) {
        if (this.activeWidget) {
            this.activeWidget.setInactive();
        }

        this.activeWidget = widgetView;

        this.widgetsSelectionRow.updateState(this.activeWidget);
    }

    getActiveWidget(): WidgetView {
        return this.activeWidget;
    }

    setActiveWidgetWithName(value: string) {
        if (this.activeWidget && value == this.activeWidget.getWidgetName()) {
            return;
        }

        if (this.activeWidget) {
            this.activeWidget.setInactive();
        }

        var widgetFound = false;
        this.widgetViews.forEach((widgetView: WidgetView) => {
            if (widgetView.getWidgetName() == value) {
                widgetView.setActive();
                widgetFound = true;
            }
        });

        if (!widgetFound) {
            this.activateDefaultWidget();
        }
    }

    resetActiveWidget() {
        this.activeWidget = null;
    }

    activateDefaultWidget() {
        var defaultWidget = this.getDefaultWidget();
        if (defaultWidget) {
            defaultWidget.setActive();
        }
    }

    isDefaultWidget(widgetView: WidgetView): boolean {
        return widgetView == this.defaultWidgetView;
    }

    getDefaultWidget(): WidgetView {
        return this.defaultWidgetView;
    }

    private initSlideFunctions(slideFrom: SLIDE_FROM) {
        switch (slideFrom) {
        case SLIDE_FROM.RIGHT:
            this.slideInFunction = this.slideInRight;
            this.slideOutFunction = this.slideOutRight;
            break;
        case SLIDE_FROM.LEFT:
            this.slideInFunction = this.slideInLeft;
            this.slideOutFunction = this.slideOutLeft;
            break;
        case SLIDE_FROM.TOP:
            this.slideInFunction = this.slideInTop;
            this.slideOutFunction = this.slideOutTop;
            break;
        case SLIDE_FROM.BOTTOM:
            this.slideInFunction = this.slideInBottom;
            this.slideOutFunction = this.slideOutBottom;
            break;
        default:
            this.slideInFunction = this.slideInRight;
            this.slideOutFunction = this.slideOutRight;
        }
    }

    private initViewer(useViewer: boolean) {
        this.useViewer = useViewer;

        if (useViewer) {

            this.viewer = new ContentSummaryViewer();
            this.viewer.addClass("details-panel-label");

            this.appendChild(this.viewer);
        }
    }

    public setItem(item: ContentSummaryAndCompareStatus): wemQ.Promise<any> {
        if (DetailsPanel.debug) {
            console.debug('DetailsPanel.setItem: ', item);
        }

        if (!api.ObjectHelper.equals(item, this.item)) {
            this.item = item;
            if (item) {
                this.layout(false);
                if (this.isVisibleOrAboutToBeVisible() && !!this.activeWidget) {
                    return this.updateActiveWidget();
                }
            } else {
                this.layout();
            }
        }
        return wemQ<any>(null);
    }

    getItem(): ContentSummaryAndCompareStatus {
        return this.item;
    }

    public isVisibleOrAboutToBeVisible(): boolean {
        return this.isSlidedIn() || (this.isVisible() && this.getHTMLElement().clientWidth > 0);
    }

    private getWidgetsInterfaceNames(): string[] {
        return ["com.enonic.xp.content-manager.context-widget", "contentstudio.detailpanel"];
    }

    private updateActiveWidget(): wemQ.Promise<any> {
        if (DetailsPanel.debug) {
            console.debug('DetailsPanel.updateWidgetsForItem');
        }

        if (!this.activeWidget) {
            return wemQ<any>(null);
        }

        this.updateViewer();

        this.showLoadMask();

        return this.activeWidget.updateWidgetItemViews().then(() => {
            // update active widget's height
            this.setDetailsContainerHeight();
            this.activeWidget.slideIn();
        }).finally(() => this.hideLoadMask());
    }

    public showLoadMask() {
        this.loadMask.show();
    }

    public hideLoadMask() {
        this.loadMask.hide();
    }

    private initDefaultWidgetView() {
        var builder = WidgetView.create()
            .setName("Info")
            .setDetailsPanel(this)
            .setWidgetItemViews([
                new StatusWidgetItemView(),
                new UserAccessWidgetItemView(),
                new PropertiesWidgetItemView(),
                new AttachmentsWidgetItemView()
            ]);

        this.detailsContainer.appendChild(this.activeWidget = this.defaultWidgetView = builder.build());
    }

    private initCommonWidgetViews() {

        var versionsWidgetView = WidgetView.create().setName("Version history").setDetailsPanel(this)
            .addWidgetItemView(new VersionsWidgetItemView()).build();

        var dependenciesWidgetView = WidgetView.create().setName("Dependencies").setDetailsPanel(this)
            .addWidgetItemView(new DependenciesWidgetItemView()).build();

        dependenciesWidgetView.addClass("dependency-widget");

        this.addWidgets([versionsWidgetView, dependenciesWidgetView]);
    }

    private getAndInitCustomWidgetViews(): wemQ.Promise<any> {
        var getWidgetsByInterfaceRequest = new api.content.resource.GetWidgetsByInterfaceRequest(this.getWidgetsInterfaceNames());

        return getWidgetsByInterfaceRequest.sendAndParse().then((widgets: Widget[]) => {
            widgets.forEach((widget) => {
                var widgetView = WidgetView.create().setName(widget.getDisplayName()).setDetailsPanel(this).setWidget(widget).build();

                this.addWidget(widgetView);
            })
        }).catch((reason: any) => {
            if (reason && reason.message) {
                //api.notify.showError(reason.message);
            } else {
                //api.notify.showError('Could not load widget descriptors.');
            }
        });
    }

    private onRenderedHandler() {
        var initialPos = 0;
        var splitterPosition = 0;
        this.actualWidth = this.getEl().getWidth();
        this.mask = new api.ui.mask.DragMask(this.getParentElement());

        var dragListener = (e: MouseEvent) => {
            if (this.splitterWithinBoundaries(initialPos - e.clientX)) {
                splitterPosition = e.clientX;
                this.ghostDragger.getEl().setLeftPx(e.clientX - this.getEl().getOffsetLeft());
            }
        };

        this.splitter.onMouseDown((e: MouseEvent) => {
            e.preventDefault();
            initialPos = e.clientX;
            splitterPosition = e.clientX;
            this.ghostDragger.insertBeforeEl(this.splitter);
            this.startDrag(dragListener);
        });

        this.mask.onMouseUp((e: MouseEvent) => {
            if (this.ghostDragger.getHTMLElement().parentNode) {
                this.actualWidth = this.getEl().getWidth() + initialPos - splitterPosition;
                this.stopDrag(dragListener);
                this.removeChild(this.ghostDragger);
                ResponsiveManager.fireResizeEvent();
            }
        });
    }

    private setDetailsContainerHeight() {
        var panelHeight = this.getEl().getHeight(),
            panelOffset = this.getEl().getOffsetToParent(),
            containerHeight = this.detailsContainer.getEl().getHeight(),
            containerOffset = this.detailsContainer.getEl().getOffsetToParent();

        if (containerOffset.top > 0 && containerHeight !== (panelHeight - panelOffset.top - containerOffset.top)) {
            this.detailsContainer.getEl().setHeightPx(panelHeight - panelOffset.top - containerOffset.top);
        }
    }

    private splitterWithinBoundaries(offset: number) {
        var newWidth = this.actualWidth + offset;
        return (newWidth >= this.minWidth) && (newWidth <= this.getParentElement().getEl().getWidth() - this.parentMinWidth);
    }

    private startDrag(dragListener: {(e: MouseEvent):void}) {
        this.mask.show();
        this.addClass("dragging");
        this.mask.onMouseMove(dragListener);
        this.ghostDragger.getEl().setLeftPx(this.splitter.getEl().getOffsetLeftRelativeToParent()).setTop(null);
    }

    private stopDrag(dragListener: {(e: MouseEvent):void}) {
        this.getEl().setWidthPx(this.actualWidth);
        this.removeClass("dragging");

        setTimeout(() => {
            this.notifyPanelSizeChanged();
            this.widgetsSelectionRow.render()
        }, 800); //delay is required due to animation time

        this.mask.hide();
        this.mask.unMouseMove(dragListener);
    }

    private addWidget(widget: WidgetView) {
        this.widgetViews.push(widget);
        this.detailsContainer.appendChild(widget);
    }

    private addWidgets(widgetViews: WidgetView[]) {
        widgetViews.forEach((widget) => {
            this.addWidget(widget);
        })
    }

    setWidthPx(value: number) {
        this.getEl().setWidthPx(value);
        this.actualWidth = value;
    }

    getActualWidth(): number {
        return this.actualWidth;
    }

    updateViewer() {
        if (this.useViewer && this.item) {
            this.viewer.setObject(this.item.getContentSummary());
        }
    }

    slideIn() {
        this.slideInFunction();
        this.slidedIn = true;
        this.notifySlidedIn();
    }

    slideOut() {
        this.slideOutFunction();
        this.slidedIn = false;
    }

    public isSlidedIn(): boolean {
        return this.slidedIn;
    }

    public resetWidgetsWidth() {
        this.widgetViews.forEach((widgetView: WidgetView) => {
            widgetView.resetContainerWidth();
        })
    }

    private layout(empty: boolean = true) {
        if (this.widgetsSelectionRow) {
            this.widgetsSelectionRow.setVisible(!empty);
        }
        if (this.viewer) {
            this.viewer.setVisible(!empty);
        }
        this.detailsContainer.setVisible(!empty);
        this.toggleClass("no-selection", empty);
    }

    protected slideInRight() {
        this.getEl().setRightPx(0);
    }

    protected slideOutRight() {
        this.getEl().setRightPx(-this.getEl().getWidthWithBorder());
    }

    protected slideInLeft() {
        this.getEl().setLeftPx(0);
    }

    protected slideOutLeft() {
        this.getEl().setLeftPx(-this.getEl().getWidthWithBorder());
    }

    protected slideInTop() {
        this.getEl().setTopPx(36);
    }

    protected slideOutTop() {
        this.getEl().setTopPx(-window.outerHeight);
    }

    protected slideInBottom() {
        this.getEl().setTopPx(36);
    }

    protected slideOutBottom() {
        this.getEl().setTopPx(window.outerHeight);
    }

    notifyPanelSizeChanged() {
        this.sizeChangedListeners.forEach((listener: ()=> void) => listener());
    }

    onPanelSizeChanged(listener: () => void) {
        this.sizeChangedListeners.push(listener);
    }

    notifySlidedIn() {
        this.slidedInListeners.forEach((listener: ()=> void) => listener());
    }

    onSlidedIn(listener: () => void) {
        this.slidedInListeners.push(listener);
    }

    static create(): Builder {
        return new Builder();
    }
}

export class MobileDetailsPanel extends DetailsPanel {

    protected slideOutTop() {
        this.getEl().setTopPx(api.BrowserHelper.isIOS() ? -window.innerHeight : -window.outerHeight);
    }

    protected slideOutBottom() {
        this.getEl().setTopPx(api.BrowserHelper.isIOS() ? window.innerHeight : window.outerHeight);
    }
}

export class Builder {

    private useViewer: boolean = true;
    private slideFrom: SLIDE_FROM = SLIDE_FROM.RIGHT;
    private name: string;
    private useSplitter: boolean = true;
    private isMobile: boolean = false;

    public setUseViewer(value: boolean): Builder {
        this.useViewer = value;
        return this;
    }

    public setSlideFrom(value: SLIDE_FROM): Builder {
        this.slideFrom = value;
        return this;
    }

    public setName(value: string): Builder {
        this.name = value;
        return this;
    }

    public setUseSplitter(value: boolean): Builder {
        this.useSplitter = value;
        return this;
    }

    public isUseViewer(): boolean {
        return this.useViewer;
    }

    public getSlideFrom(): SLIDE_FROM {
        return this.slideFrom;
    }

    public getName(): string {
        return this.name;
    }

    public isUseSplitter(): boolean {
        return this.useSplitter;
    }

    public setIsMobile(value: boolean): Builder {
        this.isMobile = value;
        return this;
    }

    public build(): DetailsPanel {
        return this.isMobile ? new MobileDetailsPanel(this) : new DetailsPanel(this);
    }
}

export enum SLIDE_FROM {

    LEFT,
    RIGHT,
    BOTTOM,
    TOP,
}
