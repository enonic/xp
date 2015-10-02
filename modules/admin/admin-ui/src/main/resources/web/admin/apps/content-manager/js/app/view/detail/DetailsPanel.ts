module app.view.detail {

    import ResponsiveManager = api.ui.responsive.ResponsiveManager;
    import ResponsiveItem = api.ui.responsive.ResponsiveItem;
    import ViewItem = api.app.view.ViewItem;
    import ContentSummary = api.content.ContentSummary;
    import CompareStatus = api.content.CompareStatus;
    import Widget = api.content.Widget;
    import Dropdown = api.ui.selector.dropdown.Dropdown;
    import DropdownConfig = api.ui.selector.dropdown.DropdownConfig;
    import Option = api.ui.selector.Option;
    import OptionSelectedEvent = api.ui.selector.OptionSelectedEvent;

    export class DetailsPanel extends api.ui.panel.Panel {

        private widgetViews: WidgetView[] = [];
        private nameAndIconView: api.app.NamesAndIconView;
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
        private contentStatusChangedListeners: {() : void}[] = [];

        private versionsPanel: ContentItemVersionsPanel;
        private item: ViewItem<ContentSummary>;
        private contentStatus: CompareStatus;

        private useNameAndIconView: boolean;
        private useSplitter: boolean;
        private slideInFunction: () => void;
        private slideOutFunction: () => void;

        private activeWidget: WidgetView;
        private defaultWidgetView: WidgetView;
        private previousActiveWidget: WidgetView;

        private static DEFAULT_WIDGET_NAME: string = "Info";

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

            this.onPanelSizeChanged(() => {
                this.versionsPanel.ReRenderActivePanel();
            });

            ResponsiveManager.onAvailableSizeChanged(this, (item: ResponsiveItem) => {
                if (this.item) {
                    this.resetItem();
                }
            });

            this.initNameAndIconView(builder.getUseNameAndIconView());
            this.initDefaultWidget();
            this.initCommonWidgetsViews();
            this.initDivForNoSelection();

            this.getAndInitCustomWidgetsViews().done(() => {
                this.initWidgetsSelectionRow();
            });
            this.appendChild(this.detailsContainer)
            this.appendChild(this.divForNoSelection);
        }

        private initDivForNoSelection() {
            this.divForNoSelection = new api.dom.DivEl("no-selection-message");
            this.divForNoSelection.getEl().setInnerHtml("You are wasting this space - select something!");
            this.appendChild(this.divForNoSelection);
        }

        private initWidgetsSelectionRow() {
            this.widgetsSelectionRow = new WidgetsSelectionRow(this);
            this.appendChild(this.widgetsSelectionRow);
            this.initWidgetsDropdownForSelectedItem();
        }

        setActiveWidget(widgetView: WidgetView) {
            if (this.activeWidget) {
                this.activeWidget.deactivate();
            }

            if (!this.isDefaultWidget(this.activeWidget)) {
                this.previousActiveWidget = this.activeWidget;
            }

            this.activeWidget = widgetView;

            this.widgetsSelectionRow.updateState(this.activeWidget);
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

        activatePreviousWidget() {
            if (this.previousActiveWidget) {
                this.previousActiveWidget.setActive();
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

        private initNameAndIconView(useNameAndIconView: boolean) {
            this.useNameAndIconView = useNameAndIconView;

            if (useNameAndIconView) {

                this.nameAndIconView = new api.app.NamesAndIconView(new api.app.NamesAndIconViewBuilder().
                    setSize(api.app.NamesAndIconViewSize.small));

                this.nameAndIconView.addClass("details-panel-label");

                this.appendChild(this.nameAndIconView);
            }
        }

        public setItem(item: ViewItem<ContentSummary>) {

            if (!this.item || (this.item && !this.item.equals(item))) {
                this.item = item;
                this.updateWidgetsForItem();
            }
        }

        public setContentStatus(status: CompareStatus) {
            this.contentStatus = status;
            this.notifyContentStatusChanged();
        }

        private getWidgetsInterfaceName(): string {
            return "com.enonic.xp.content-manager.context-widget";
        }

        private updateWidgetsForItem() {

            this.updateNameAndIconView();

            this.updateCommonWidgets();
            this.updateCustomWidgets();
            setTimeout(() => {
                this.updateWidgetsHeights();
            }, 400);
        }

        private updateWidgetsHeights() {
            this.widgetViews.forEach((widgetView: WidgetView) => {
                if (widgetView != this.activeWidget) {
                    widgetView.updateNormalHeightSilently();
                } else {
                    widgetView.updateNormalHeight();
                }
            });
            if (this.defaultWidgetView) {
                if (this.defaultWidgetView != this.activeWidget) {
                    this.defaultWidgetView.updateNormalHeightSilently();
                } else {
                    this.defaultWidgetView.updateNormalHeight();
                }
            }
        }

        private updateCommonWidgets() {
            this.versionsPanel.setItem(this.item);
            this.setDefaultWidget();
        }

        private updateCustomWidgets() {
        }

        private initWidgetsDropdownForSelectedItem() {
            this.widgetsSelectionRow.updateWidgetsDropdown(this.widgetViews);
            this.activateDefaultWidget();
        }


        private setDefaultWidget() {
            var widgetItemView = new StatusWidgetItemView();
            var propWidgetItemView = new PropertiesWidgetItemView();

            if (this.item) {
                api.content.ContentSummaryAndCompareStatusFetcher.fetch(this.item.getModel().getContentId()).then((contentSummaryAndCompareStatus) => {

                    this.contentStatus = contentSummaryAndCompareStatus.getCompareStatus();

                    if (this.defaultWidgetView) {
                        this.detailsContainer.removeChild(this.defaultWidgetView);
                    }

                    widgetItemView.setStatus(this.contentStatus);

                    this.onContentStatusChanged(() => {
                        widgetItemView.setStatus(this.contentStatus);
                        widgetItemView.layout();
                    });

                    propWidgetItemView.setContent(this.item.getModel());

                    this.defaultWidgetView = WidgetView.create().
                        setName(DetailsPanel.DEFAULT_WIDGET_NAME).
                        setDetailsPanel(this).
                        setUseToggleButton(false).
                        addWidgetItemView(widgetItemView).
                        addWidgetItemView(propWidgetItemView).
                        build();

                    this.detailsContainer.appendChild(this.defaultWidgetView);

                    if (DetailsPanel.DEFAULT_WIDGET_NAME == this.activeWidget.getWidgetName()) {
                        this.setActiveWidget(this.defaultWidgetView);
                    }
                    this.updateWidgetsHeights();

                }).done();
            }
        }

        private initDefaultWidget() {
            this.defaultWidgetView = WidgetView.create().
                setName(DetailsPanel.DEFAULT_WIDGET_NAME).
                setDetailsPanel(this).
                setUseToggleButton(false).
                build();
            this.detailsContainer.appendChild(this.defaultWidgetView);
        }

        private initCommonWidgetsViews() {

            var widgetItemView = new WidgetItemView();
            widgetItemView.setItem(this.versionsPanel);

            var versionsWidgetView = WidgetView.create().
                setName("Version history").
                setDetailsPanel(this).
                setUseToggleButton(false).
                addWidgetItemView(widgetItemView).
                build();

            this.addWidgets([versionsWidgetView]);
        }

        private getAndInitCustomWidgetsViews(): wemQ.Promise<any> {
            var getWidgetsByInterfaceRequest = new api.content.GetWidgetsByInterfaceRequest(this.getWidgetsInterfaceName());

            return getWidgetsByInterfaceRequest.sendAndParse().then((widgets: api.content.Widget[]) => {
                widgets.forEach((widget) => {
                    var widgetView = WidgetView.create().
                        setName(widget.getDisplayName()).
                        setDetailsPanel(this).
                        setUseToggleButton(false).
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

        getItem(): ViewItem<ContentSummary> {
            return this.item;
        }

        resetItem() {
            if (this.item) {
                var temp = this.item;
                this.item = null;
                this.setItem(temp);
            }
        }

        updateNameAndIconView() {
            if (this.useNameAndIconView && this.item) {
                this.nameAndIconView.setMainName(this.item.getDisplayName());
                this.nameAndIconView.setSubName(this.item.getPath());
                this.nameAndIconView.setIconUrl(this.item.getIconUrl());
            }
        }

        slideIn() {
            this.slideInFunction();
        }

        slideOut() {
            this.slideOutFunction();
        }

        makeLookEmpty() {
            if (this.widgetsSelectionRow) {
                this.widgetsSelectionRow.setVisible(false);
            }
            this.detailsContainer.setVisible(false);
            this.nameAndIconView.setVisible(false);
            this.addClass("no-selection");
        }

        unMakeLookEmpty() {
            if (this.widgetsSelectionRow) {
                this.widgetsSelectionRow.setVisible(true);
            }
            this.detailsContainer.setVisible(true);
            this.nameAndIconView.setVisible(true);
            this.removeClass("no-selection");
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

        notifyContentStatusChanged() {
            this.contentStatusChangedListeners.forEach((listener: ()=> void) => listener());
        }

        onContentStatusChanged(listener: () => void) {
            this.contentStatusChangedListeners.push(listener);
        }

        unContentStatusChanged(listener: () => void) {
            this.contentStatusChangedListeners.filter((currentListener: () => void) => {
                return listener == currentListener;
            });
        }

        static create(): Builder {
            return new Builder();
        }
    }

    export class Builder {

        private useNameAndIconView: boolean = true;
        private slideFrom: SLIDE_FROM = SLIDE_FROM.RIGHT;
        private name: string;
        private useSplitter: boolean = true;

        public setUseNameAndIconView(value: boolean): Builder {
            this.useNameAndIconView = value;
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

        public getUseNameAndIconView(): boolean {
            return this.useNameAndIconView;
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

    export class LargeDetailsPanelToggleButton extends api.dom.DivEl {

        private splitPanelWithGridAndDetails: api.ui.panel.SplitPanel;
        private detailsPanel: DetailsPanel;

        constructor(splitPanel: api.ui.panel.SplitPanel, detailsPanel: DetailsPanel) {
            super("button large-details-panel-toggle-button");

            this.splitPanelWithGridAndDetails = splitPanel;
            this.detailsPanel = detailsPanel;

            this.onClicked((event) => {
                this.detailsPanel.addClass("left-bordered");
                if (this.toggleClass("expanded").hasClass("expanded")) {
                    this.splitPanelWithGridAndDetails.showSecondPanel(false);
                } else {
                    this.splitPanelWithGridAndDetails.foldSecondPanel();
                }

                setTimeout(() => {
                    this.detailsPanel.removeClass("left-bordered");
                    if (this.hasClass("expanded")) {
                        this.splitPanelWithGridAndDetails.showSplitter();
                    }
                    this.splitPanelWithGridAndDetails.distribute();
                }, 500);
            });
        }

        ensureButtonHasCorrectState() {
            this.toggleClass("expanded", !this.splitPanelWithGridAndDetails.isSecondPanelHidden());
        }

        isExpanded(): boolean {
            return this.hasClass("expanded");
        }
    }

    export class InfoWidgetToggleButton extends api.dom.DivEl {

        private detailsPanel: DetailsPanel;

        constructor(detailsPanel: DetailsPanel) {
            super("info-widget-toggle-button");

            this.detailsPanel = detailsPanel;

            this.onClicked((event) => {
                this.addClass("active");
                detailsPanel.activateDefaultWidget();
                /*if (this.hasClass("active")) {
                 detailsPanel.activateDefaultWidget();
                 } else {
                 detailsPanel.activatePreviousWidget();
                 }*/
            });
        }

        setActive() {
            this.addClass("active");
        }

        setInactive() {
            this.removeClass("active");
        }
    }

    export enum SLIDE_FROM {

        LEFT,
        RIGHT,
        BOTTOM,
        TOP,
    }

    export class WidgetViewOption {

        private widgetView: WidgetView;

        constructor(widgetView: WidgetView) {
            this.widgetView = widgetView;
        }

        getWidgetView(): WidgetView {
            return this.widgetView;
        }

        toString(): string {
            return this.widgetView.getWidgetName();
        }

    }

    export class WidgetsSelectionRow extends api.dom.DivEl {

        private detailsPanel: DetailsPanel;

        private widgetSelectorDropdown: WidgetSelectorDropdown;
        private infoWidgetToggleButton: InfoWidgetToggleButton;

        constructor(detailsPanel: DetailsPanel) {
            super("widgets-selection-row");

            this.detailsPanel = detailsPanel;

            this.infoWidgetToggleButton = new InfoWidgetToggleButton(detailsPanel);

            this.widgetSelectorDropdown = new WidgetSelectorDropdown();

            this.widgetSelectorDropdown.addClass("widget-selector");
            this.widgetSelectorDropdown.getInput().getEl().setDisabled(true);
            this.widgetSelectorDropdown.getInput().setPlaceholder("");

            this.widgetSelectorDropdown.onOptionSelected((event: OptionSelectedEvent<WidgetViewOption>) => {
                var widgetView = event.getOption().displayValue.getWidgetView();
                widgetView.setActive();
            });

            this.appendChild(this.infoWidgetToggleButton);
            this.appendChild(this.widgetSelectorDropdown);
        }

        updateState(widgetView: WidgetView) {
            if (this.detailsPanel.isDefaultWidget(widgetView)) {
                this.infoWidgetToggleButton.setActive();
                this.widgetSelectorDropdown.removeClass("non-default-selected");
            } else {
                this.widgetSelectorDropdown.addClass("non-default-selected");
                this.infoWidgetToggleButton.setInactive();
            }
        }

        updateWidgetsDropdown(widgetViews: WidgetView[]) {
            this.widgetSelectorDropdown.removeAllOptions();

            widgetViews.forEach((view: WidgetView) => {

                var option = {
                    value: view.getWidgetName(),
                    displayValue: new WidgetViewOption(view)
                };

                this.widgetSelectorDropdown.addOption(option);
            });

            if (this.widgetSelectorDropdown.getOptionCount() < 2) {
                this.widgetSelectorDropdown.addClass("single-optioned")
            }

            this.setVisible(false);
            this.widgetSelectorDropdown.selectRow(0, true);
            this.setVisible(true);
        }
    }

    export class WidgetSelectorDropdown extends Dropdown<WidgetViewOption> {

        constructor() {
            super("widgetSelector", <DropdownConfig<WidgetViewOption>>{});

            this.onClicked((event) => {
                if (!this.isDropdownHandle(event.target)) {
                    if (this.getSelectedOption()) {
                        var widgetView = this.getSelectedOption().displayValue.getWidgetView();
                        widgetView.setActive();
                        this.hideDropdown();
                    }
                }
            });
        }

        private isDropdownHandle(object: Object) {
            if (object && object["id"] && object["id"].toString().indexOf("DropdownHandle") > 0) {
                return true;
            }
            return false;
        }

    }
}