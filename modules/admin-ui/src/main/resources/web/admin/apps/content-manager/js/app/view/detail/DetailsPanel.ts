module app.view.detail {

    import ResponsiveManager = api.ui.responsive.ResponsiveManager;
    import ViewItem = api.app.view.ViewItem;
    import ContentSummary = api.content.ContentSummary;
    import Widget = api.content.Widget;

    export class DetailsPanel extends api.ui.panel.Panel {

        private widgetViews: WidgetView[] = [];
        private labelEl: api.dom.SpanEl;
        private detailsContainer: api.dom.DivEl = new api.dom.DivEl("details-container");
        private animationTimer;

        private splitter: api.dom.DivEl;
        private ghostDragger: api.dom.DivEl;
        private mask: api.ui.mask.DragMask;

        private actualWidth: number;
        private minWidth: number = 280;
        private parentMinWidth: number = 15;

        private sizeChangedListeners: {() : void}[] = [];

        private versionsPanel: ContentItemVersionsPanel;
        private item: ViewItem<ContentSummary>;

        private useNameLabel: boolean;
        private useSplitter: boolean;
        private slideInFunction: () => void;
        private slideOutFunction: () => void;

        private activeWidget: WidgetView;

        constructor(builder: Builder) {
            super("details-panel");
            this.setDoOffset(false);
            this.initSlideFunctions(builder.getSlideFrom());
            this.useSplitter = builder.getUseSplitter();

            this.versionsPanel = new ContentItemVersionsPanel();
            this.ghostDragger = new api.dom.DivEl("ghost-dragger");

            if (this.useSplitter) {
                this.splitter = new api.dom.DivEl("splitter");
                this.appendChild(this.splitter);
                this.onRendered(() => this.onRenderedHandler());
            }

            this.onShown((event) => {
                if (this.item) {
                    this.resetItem();
                } // this helps to re-init widget view sizes when window size change triggers detail panel to show
            });

            this.initNameLabel(builder.geUseNameLabel(), builder.getName());

            this.appendChild(this.detailsContainer)
        }

        setActiveWidget(widgetView: WidgetView) {
            if (this.activeWidget) {
                this.activeWidget.deactivate();
            }
            this.activeWidget = widgetView;
        }

        deactivateActiveWidget() {
            this.activeWidget = null;
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

        private initNameLabel(useNameLabel: boolean, name: string) {
            this.useNameLabel = useNameLabel;

            if (useNameLabel) {
                this.labelEl = new api.dom.SpanEl("details-panel-label");
                this.labelEl.setVisible(false);
                if (name) {
                    this.labelEl.setHtml(name);
                }
                this.appendChild(this.labelEl);
            }
        }

        public setItem(item: ViewItem<ContentSummary>) {

            if (!this.item || (this.item && !this.item.equals(item))) {
                this.item = item;
                this.initWidgetsForItem();
                this.versionsPanel.setItem(item);
            }
        }

        private getWidgetsInterfaceName(): string {
            return "com.enonic.xp.content-manager.context-widget";
        }

        private initWidgetsForItem() {
            this.removeDetails();

            this.setName(this.item.getDisplayName());

            this.initCommonWidgetsViews();
            this.getAndInitCustomWidgetsViews();

            this.onPanelSizeChanged(() => {
                this.versionsPanel.ReRenderActivePanel();
            });

        }

        private initCommonWidgetsViews() {
            var versionsWidget = new WidgetView("Version history", this);
            versionsWidget.setWidgetContents(this.versionsPanel);
            this.addWidgets([versionsWidget]);
        }

        private getAndInitCustomWidgetsViews() {
            var getWidgetsByInterfaceRequest = new api.content.GetWidgetsByInterfaceRequest(this.getWidgetsInterfaceName());

            return getWidgetsByInterfaceRequest.sendAndParse().then((widgets: api.content.Widget[]) => {
                widgets.forEach((widget) => {
                    this.addWidget(WidgetView.fromWidget(widget, this));
                })
            }).catch((reason: any) => {
                if (reason && reason.message) {
                    //api.notify.showError(reason.message);
                } else {
                    //api.notify.showError('Could not load widget descriptors.');
                }
            }).done();
        }

        private onRenderedHandler() {
            var initialPos = 0;
            var splitterPosition = 0;
            var parent = this.getParentElement();
            this.actualWidth = this.getEl().getWidth();
            this.mask = new api.ui.mask.DragMask(parent);

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

            this.callWithTimeout(() => {
                this.notifyPanelSizeChanged();
            }, 800); //delay is required due to animation time

            this.mask.hide();
            this.mask.unMouseMove(dragListener);
        }

        private removeDetails() {
            this.detailsContainer.removeChildren();
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

        getItem(): ViewItem<ContentSummary> {
            return this.item;
        }

        reset() {
            this.removeDetails();
            if (this.useNameLabel) {
                this.labelEl.setHtml("");
                this.labelEl.setVisible(false);
            }
        }

        resetItem() {
            if (this.item) {
                var temp = this.item;
                this.item = null;
                this.setItem(temp);
            }
        }

        setName(name: string) {
            if (this.useNameLabel) {
                this.labelEl.setHtml(name);
                if (!this.labelEl.isVisible()) {
                    this.labelEl.setVisible(true);
                }
            }
        }

        slideIn() {
            this.slideInFunction();
        }

        slideOut() {
            this.slideOutFunction();
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

        unPanelSizeChanged(listener: () => void) {
            this.sizeChangedListeners.filter((currentListener: () => void) => {
                return listener == currentListener;
            });
        }

        callWithTimeout(callback: () => void, timer: number) {

            if (this.animationTimer) {
                clearTimeout(this.animationTimer);
            }
            this.animationTimer = setTimeout(() => {
                callback();
                this.animationTimer = null
            }, timer);
        }

        static create(): Builder {
            return new Builder();
        }
    }

    export class Builder {

        private useNameLabel: boolean = true;
        private slideFrom: SLIDE_FROM = SLIDE_FROM.RIGHT;
        private name: string;
        private useSplitter: boolean = true;

        public setUseNameLabel(value: boolean): Builder {
            this.useNameLabel = value;
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

        public geUseNameLabel(): boolean {
            return this.useNameLabel;
        }

        public getSlideFrom(): app.view.detail.SLIDE_FROM {
            return this.slideFrom;
        }

        public getName(): string {
            return this.name;
        }

        public getUseSplitter(): boolean {
            return this.useSplitter;
        }

        public build(): DetailsPanel {
            return new DetailsPanel(this);
        }
    }

    export class DetailsPanelToggleButton extends api.ui.button.ActionButton {

        private toggleAction: DetailsPanelToggleAction;

        constructor(action: DetailsPanelToggleAction) {
            super(action);
            this.toggleAction = action;
            this.addClass("details-panel-toggle-button");

            action.onExecuted(() => {
                this.toggleClass("expanded", action.isExpanded());
            });
        }

        disable() {
            this.toggleAction.setEnabled(false);
            this.unExpand();
        }

        unExpand() {
            this.toggleAction.setExpanded(false);
            this.removeClass("expanded");
        }
    }

    export class DetailsPanelToggleAction extends api.ui.Action {

        private detailsPanel: DetailsPanel;
        private expanded: boolean = false;

        constructor(detailsPanel: DetailsPanel) {
            super("");

            this.detailsPanel = detailsPanel;

            this.setEnabled(false);
            this.onExecuted(() => {
                this.expanded = !this.expanded;
                if (this.expanded) {
                    this.detailsPanel.slideIn();
                } else {
                    this.detailsPanel.slideOut();
                }
            });
        }

        isExpanded(): boolean {
            return this.expanded;
        }

        setExpanded(value: boolean) {
            this.expanded = value;
        }
    }

    export class MobileDetailsPanelToggleButton extends api.dom.DivEl {

        private detailsPanel: DetailsPanel;

        constructor(detailsPanel: DetailsPanel) {
            super("mobile-details-panel-toggle-button");

            this.detailsPanel = detailsPanel;

            this.onClicked((event) => {
                this.toggleClass("expanded");
                if (this.hasClass("expanded")) {
                    this.detailsPanel.slideIn();
                } else {
                    this.detailsPanel.slideOut();
                }
            });
        }
    }

    export enum SLIDE_FROM {

        LEFT,
        RIGHT,
        BOTTOM,
        TOP,
    }
}
