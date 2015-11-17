module app.view.detail {

    import ResponsiveManager = api.ui.responsive.ResponsiveManager;
    import ResponsiveItem = api.ui.responsive.ResponsiveItem;
    import ViewItem = api.app.view.ViewItem;
    import ContentSummary = api.content.ContentSummary;
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
        private divForNoSelection: api.dom.DivEl;

        private actualWidth: number;
        private minWidth: number = 280;
        private parentMinWidth: number = 15;

        private sizeChangedListeners: {() : void}[] = [];

        private item: ContentSummaryAndCompareStatus;

        private useViewer: boolean;
        private useSplitter: boolean;
        private slideInFunction: () => void;
        private slideOutFunction: () => void;

        private activeWidget: WidgetView;
        private defaultWidgetView: WidgetView;

        private alreadyFetchedCustomWidgets: boolean;

        private versionsWidgetItemView: VersionsWidgetItemView;
        private statusWidgetItemView: StatusWidgetItemView;
        private propWidgetItemView: PropertiesWidgetItemView;
        private attachmentsWidgetItemView: AttachmentsWidgetItemView;
        private userAccessWidgetItemView: UserAccessWidgetItemView;

        private static DEFAULT_WIDGET_NAME: string = "Info";

        public static debug = false;

        constructor(builder: Builder) {
            super("details-panel");
            this.setDoOffset(false);
            this.initSlideFunctions(builder.getSlideFrom());
            this.useSplitter = builder.isUseSplitter();

            this.ghostDragger = new api.dom.DivEl("ghost-dragger");

            if (this.useSplitter) {
                this.splitter = new api.dom.DivEl("splitter");
                this.appendChild(this.splitter);
                this.onRendered(() => this.onRenderedHandler());
            }

            this.managePublishEvent();

            this.initViewer(builder.isUseViewer());
            this.initDefaultWidgetView();
            this.initCommonWidgetViews();
            this.initDivForNoSelection();
            this.initWidgetsSelectionRow();

            this.appendChild(this.detailsContainer);
            this.appendChild(this.divForNoSelection);

            this.layout();
        }

        private managePublishEvent() {
            api.content.ContentsPublishedEvent.on((event: api.content.ContentsPublishedEvent) => {
                if (this.getItem()) {
                    // check for item because it can be null after publishing pending for delete item
                    var itemId = this.getItem().getId();
                    var idPublished = event.getContentIds().some((id, index, array) => {
                        return itemId === id.toString();
                    });

                    if (idPublished) {
                        this.versionsWidgetItemView.reloadActivePanel();
                    }
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
        }

        getCustomWidgetViewsAndUpdateDropdown() {
            if (!this.alreadyFetchedCustomWidgets) {
                this.getAndInitCustomWidgetViews().done(() => {
                    this.initWidgetsDropdownForSelectedItem();
                    this.alreadyFetchedCustomWidgets = true;
                });
            }
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
            if (DetailsPanel.DEFAULT_WIDGET_NAME == value) {
                this.defaultWidgetView.setActive();
                return;
            }
            this.widgetViews.forEach((widgetView: WidgetView) => {
                if (widgetView.getWidgetName() == value && widgetView != this.activeWidget) {
                    widgetView.setActive();
                }
            });
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
                    return this.updateWidgetsForItem();
                } else {
                    this.layout();
                }
            }
            return wemQ<any>(null);
        }

        getItem(): ContentSummaryAndCompareStatus {
            return this.item;
        }

        private getWidgetsInterfaceName(): string {
            return "com.enonic.xp.content-manager.context-widget";
        }

        private updateWidgetsForItem(): wemQ.Promise<any> {
            if (DetailsPanel.debug) {
                console.debug('DetailsPanel.updateWidgetsForItem');
            }

            this.updateViewer();

            var defaultPromise = this.updateDefaultWidgetViews();
            var commonPromise = this.updateCommonWidgetViews();
            var customPromise = this.updateCustomWidgetViews();

            return wemQ.all([defaultPromise, commonPromise, customPromise]).then(() => {
                // update active widget's height
                this.activeWidget.slideIn();
            });
        }

        private updateDefaultWidgetViews(): wemQ.Promise<any> {
            var promises = [];
            if (this.item) {
                promises.push(this.statusWidgetItemView.setStatus(this.item.getCompareStatus()));
                promises.push(this.propWidgetItemView.setContent(this.item.getContentSummary()));
                promises.push(this.attachmentsWidgetItemView.setContent(this.item.getContentSummary()));
                promises.push(this.userAccessWidgetItemView.setContentId(this.item.getContentId()));
            }
            return wemQ.all(promises);
        }

        private updateCommonWidgetViews(): wemQ.Promise<any> {
            return this.versionsWidgetItemView.setItem(this.item);
        }

        private updateCustomWidgetViews(): wemQ.Promise<any> {
            var promises = [];
            this.widgetViews.forEach((widgetView: WidgetView) => {
                if (widgetView.isUrlBased()) {
                    promises.push(widgetView.setContent(this.item));
                }
            })

            return wemQ.all(promises);
        }

        private initWidgetsDropdownForSelectedItem() {
            this.widgetsSelectionRow.updateWidgetsDropdown(this.widgetViews);
            this.activateDefaultWidget();
        }

        private initDefaultWidgetView() {
            this.statusWidgetItemView = new StatusWidgetItemView();
            this.propWidgetItemView = new PropertiesWidgetItemView();
            this.attachmentsWidgetItemView = new AttachmentsWidgetItemView();
            this.userAccessWidgetItemView = new UserAccessWidgetItemView();

            this.defaultWidgetView = WidgetView.create().
                setName(DetailsPanel.DEFAULT_WIDGET_NAME).
                setDetailsPanel(this).
                addWidgetItemView(this.statusWidgetItemView).
                addWidgetItemView(this.userAccessWidgetItemView).
                addWidgetItemView(this.propWidgetItemView).
                addWidgetItemView(this.attachmentsWidgetItemView).
                build();

            this.detailsContainer.appendChild(this.defaultWidgetView);
        }

        private initCommonWidgetViews() {

            this.versionsWidgetItemView = new VersionsWidgetItemView();

            var versionsWidgetView = WidgetView.create().
                setName("Version history").
                setDetailsPanel(this).
                addWidgetItemView(this.versionsWidgetItemView).
                build();

            this.addWidgets([versionsWidgetView]);
        }

        private getAndInitCustomWidgetViews(): wemQ.Promise<any> {
            var getWidgetsByInterfaceRequest = new api.content.GetWidgetsByInterfaceRequest(this.getWidgetsInterfaceName());

            return getWidgetsByInterfaceRequest.sendAndParse().then((widgets: Widget[]) => {
                widgets.forEach((widget) => {
                    var widgetView = WidgetView.create().
                        setName(widget.getDisplayName()).
                        setDetailsPanel(this).
                        setWidget(widget).
                        build();

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
                //#
                this.viewer.setObject(this.item.getContentSummary());
            }
        }

        slideIn() {
            this.slideInFunction();
        }

        slideOut() {
            this.slideOutFunction();
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

        private slideInRight() {
            this.getEl().setRightPx(0);
        }

        private slideOutRight() {
            this.getEl().setRightPx(-this.getEl().getWidthWithBorder());
        }

        private slideInLeft() {
            this.getEl().setLeftPx(0);
        }

        private slideOutLeft() {
            this.getEl().setLeftPx(-this.getEl().getWidthWithBorder());
        }

        private slideInTop() {
            this.getEl().setTopPx(36);
        }

        private slideOutTop() {
            this.getEl().setTopPx(-window.outerHeight);
        }

        private slideInBottom() {
            this.getEl().setTopPx(36);
        }

        private slideOutBottom() {
            this.getEl().setTopPx(window.outerHeight);
        }

        notifyPanelSizeChanged() {
            this.sizeChangedListeners.forEach((listener: ()=> void) => listener());
        }

        onPanelSizeChanged(listener: () => void) {
            this.sizeChangedListeners.push(listener);
        }

        static create(): Builder {
            return new Builder();
        }
    }

    export class Builder {

        private useViewer: boolean = true;
        private slideFrom: SLIDE_FROM = SLIDE_FROM.RIGHT;
        private name: string;
        private useSplitter: boolean = true;

        public setUseViewer(value: boolean): Builder {
            this.useViewer = value;
            return this;
        }

        public setSlideFrom(value: app.view.detail.SLIDE_FROM): Builder {
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

        public getSlideFrom(): app.view.detail.SLIDE_FROM {
            return this.slideFrom;
        }

        public getName(): string {
            return this.name;
        }

        public isUseSplitter(): boolean {
            return this.useSplitter;
        }

        public build(): DetailsPanel {
            return new DetailsPanel(this);
        }
    }

    export enum SLIDE_FROM {

        LEFT,
        RIGHT,
        BOTTOM,
        TOP,
    }
}