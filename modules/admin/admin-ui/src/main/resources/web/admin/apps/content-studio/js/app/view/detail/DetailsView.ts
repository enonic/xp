import '../../../api.ts';
import {WidgetView} from './WidgetView';
import {WidgetsSelectionRow} from './WidgetsSelectionRow';
import {VersionsWidgetItemView} from './widget/version/VersionsWidgetItemView';
import {DependenciesWidgetItemView} from './widget/dependency/DependenciesWidgetItemView';
import {StatusWidgetItemView} from './widget/info/StatusWidgetItemView';
import {PropertiesWidgetItemView} from './widget/info/PropertiesWidgetItemView';
import {AttachmentsWidgetItemView} from './widget/info/AttachmentsWidgetItemView';
import {UserAccessWidgetItemView} from './widget/info/UserAccessWidgetItemView';
import {ActiveDetailsPanelManager} from '../../view/detail/ActiveDetailsPanelManager';

import ContentSummaryAndCompareStatus = api.content.ContentSummaryAndCompareStatus;
import Widget = api.content.Widget;
import ContentSummaryViewer = api.content.ContentSummaryViewer;

import ContentVersionSetEvent = api.content.event.ActiveContentVersionSetEvent;
import GetWidgetsByInterfaceRequest = api.content.resource.GetWidgetsByInterfaceRequest;
import ApplicationEvent = api.application.ApplicationEvent;
import ApplicationEventType = api.application.ApplicationEventType;
import AppHelper = api.util.AppHelper;

export class DetailsView extends api.dom.DivEl {

    private widgetViews: WidgetView[] = [];
    private viewer: ContentSummaryViewer;
    private detailsContainer: api.dom.DivEl = new api.dom.DivEl('details-container');
    private widgetsSelectionRow: WidgetsSelectionRow;

    private loadMask: api.ui.mask.LoadMask;
    private divForNoSelection: api.dom.DivEl;

    private item: ContentSummaryAndCompareStatus;

    private activeWidget: WidgetView;
    private defaultWidgetView: WidgetView;

    private alreadyFetchedCustomWidgets: boolean;

    private sizeChangedListeners: {(): void}[] = [];

    public static debug: boolean = false;

    constructor() {
        super('details-panel-view');

        this.appendChild(this.loadMask = new api.ui.mask.LoadMask(this));
        this.loadMask.addClass('details-panel-mask');

        this.initViewer();
        this.initDefaultWidgetView();
        this.initCommonWidgetViews();
        this.initDivForNoSelection();
        this.initWidgetsSelectionRow();

        this.appendChild(this.detailsContainer);
        this.appendChild(this.divForNoSelection);

        this.subscribeOnEvents();

        this.layout();

        this.getCustomWidgetViewsAndUpdateDropdown();

        const handleWidgetsUpdate = AppHelper.debounce((e) => this.handleWindgetsUpdate(e), 1000);
        ApplicationEvent.on(handleWidgetsUpdate);
        this.onRemoved(() => ApplicationEvent.un(handleWidgetsUpdate));
    }

    private subscribeOnEvents() {
        ContentVersionSetEvent.on((event: ContentVersionSetEvent) => {
            if (ActiveDetailsPanelManager.getActiveDetailsPanel().isVisibleOrAboutToBeVisible() && !!this.activeWidget &&
                this.activeWidget.getWidgetName() === 'Version history') {
                this.updateActiveWidget();
            }
        });
    }

    private initDivForNoSelection() {
        this.divForNoSelection = new api.dom.DivEl('no-selection-message');
        this.divForNoSelection.getEl().setInnerHtml(`Select an item - and we'll show you the details!`);
        this.appendChild(this.divForNoSelection);
    }

    private initWidgetsSelectionRow() {
        this.widgetsSelectionRow = new WidgetsSelectionRow(this);
        this.appendChild(this.widgetsSelectionRow);
        this.widgetsSelectionRow.updateState(this.activeWidget);
    }

    getWidgetsSelectionRow(): WidgetsSelectionRow {
        return this.widgetsSelectionRow;
    }

    public resetWidgetsWidth() {
        this.widgetViews.forEach((widgetView: WidgetView) => {
            widgetView.resetContainerWidth();
        });
    }

    private handleWindgetsUpdate(event: ApplicationEvent) {
        const isWidgetUpdated = [
            ApplicationEventType.INSTALLED,
            ApplicationEventType.UNINSTALLED,
            ApplicationEventType.STARTED,
            ApplicationEventType.STOPPED,
            ApplicationEventType.UPDATED
        ].indexOf(event.getEventType()) > -1;

        if (isWidgetUpdated) {
            const activeWidgetName = this.activeWidget.getWidgetName();

            this.removeAllWidgets();
            this.initCommonWidgetViews();

            this.getAndInitCustomWidgetViews().then(() => {
                this.widgetsSelectionRow.updateWidgetsDropdown(this.widgetViews);

                const activeIndex = this.widgetViews.map(view => view.getWidgetName()).indexOf(activeWidgetName);
                const active = this.widgetViews[activeIndex];

                if (!active && event.getEventType() === ApplicationEventType.STOPPED) {
                    this.activateDefaultWidget();
                    this.updateActiveWidget();
                } else if (active) {
                    this.setActiveWidget(active);
                    this.updateActiveWidget();
                }
            });
        }
    }

    getCustomWidgetViewsAndUpdateDropdown(): wemQ.Promise<void> {
        let deferred = wemQ.defer<void>();
        if (!this.alreadyFetchedCustomWidgets) {
            this.getAndInitCustomWidgetViews().then(() => {
                this.widgetsSelectionRow.updateWidgetsDropdown(this.widgetViews);
                // this.updateActiveWidget();
                this.alreadyFetchedCustomWidgets = true;
                deferred.resolve(null);
            });
        } else {
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

    /*setActiveWidgetWithName(value: string) {
     if (this.activeWidget && value === this.activeWidget.getWidgetName()) {
     return;
     }

     if (this.activeWidget) {
     this.activeWidget.setInactive();
     }

     var widgetFound = false;
     this.widgetViews.forEach((widgetView: WidgetView) => {
     if (widgetView.getWidgetName() === value) {
     widgetView.setActive();
     widgetFound = true;
     }
     });

     if (!widgetFound) {
     this.activateDefaultWidget();
     }
     }*/

    resetActiveWidget() {
        this.activeWidget = null;
    }

    activateDefaultWidget() {
        let defaultWidget = this.getDefaultWidget();
        if (defaultWidget) {
            defaultWidget.setActive();
        }
    }

    isDefaultWidget(widgetView: WidgetView): boolean {
        return widgetView === this.defaultWidgetView;
    }

    getDefaultWidget(): WidgetView {
        return this.defaultWidgetView;
    }

    private initViewer() {
        this.viewer = new ContentSummaryViewer();
        this.viewer.addClass('details-panel-label');

        this.appendChild(this.viewer);
    }

    public setItem(item: ContentSummaryAndCompareStatus): wemQ.Promise<any> {
        if (DetailsView.debug) {
            console.debug('DetailsView.setItem: ', item);
        }

        if (!api.ObjectHelper.equals(item, this.item)) {
            this.item = item;
            if (item) {
                this.layout(false);
                if (ActiveDetailsPanelManager.getActiveDetailsPanel().isVisibleOrAboutToBeVisible() && !!this.activeWidget) {
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

    private getWidgetsInterfaceNames(): string[] {
        return ['com.enonic.xp.content-manager.context-widget', 'contentstudio.detailpanel'];
    }

    updateActiveWidget(): wemQ.Promise<any> {
        if (DetailsView.debug) {
            console.debug('DetailsView.updateWidgetsForItem');
        }

        if (!this.activeWidget) {
            return wemQ<any>(null);
        }

        this.updateViewer();

        return this.activeWidget.updateWidgetItemViews().then(() => {
            // update active widget's height
            setTimeout(() => {
                this.setDetailsContainerHeight();
            }, 400);

            this.activeWidget.slideIn();
        });
    }

    public showLoadMask() {
        this.loadMask.show();
    }

    public hideLoadMask() {
        this.loadMask.hide();
    }

    private initDefaultWidgetView() {
        let builder = WidgetView.create()
            .setName('Info')
            .setDetailsView(this)
            .setWidgetItemViews([
                new StatusWidgetItemView(),
                new UserAccessWidgetItemView(),
                new PropertiesWidgetItemView(),
                new AttachmentsWidgetItemView()
            ]);

        this.detailsContainer.appendChild(this.activeWidget = this.defaultWidgetView = builder.build());
    }

    private initCommonWidgetViews() {

        let versionsWidgetView = WidgetView.create().setName('Version history').setDetailsView(this)
            .addWidgetItemView(new VersionsWidgetItemView()).build();

        let dependenciesWidgetView = WidgetView.create().setName('Dependencies').setDetailsView(this)
            .addWidgetItemView(new DependenciesWidgetItemView()).build();

        dependenciesWidgetView.addClass('dependency-widget');

        this.addWidgets([versionsWidgetView, dependenciesWidgetView]);
    }

    private getAndInitCustomWidgetViews(): wemQ.Promise<any> {
        let getWidgetsByInterfaceRequest = new GetWidgetsByInterfaceRequest(this.getWidgetsInterfaceNames());

        return getWidgetsByInterfaceRequest.sendAndParse().then((widgets: Widget[]) => {
            widgets.forEach((widget) => {
                let widgetView = WidgetView.create().setName(widget.getDisplayName()).setDetailsView(this).setWidget(widget).build();

                this.addWidget(widgetView);
            });
        }).catch((reason: any) => {
            if (reason && reason.message) {
                api.notify.showError(reason.message);
            } else {
                api.notify.showError('Could not load widget descriptors.');
            }
        });
    }

    setDetailsContainerHeight() {
        let panelHeight = ActiveDetailsPanelManager.getActiveDetailsPanel().getEl().getHeight();
        let panelOffset = ActiveDetailsPanelManager.getActiveDetailsPanel().getEl().getOffsetToParent();
        let containerHeight = this.detailsContainer.getEl().getHeight();
        let containerOffset = this.detailsContainer.getEl().getOffsetToParent();

        if (containerOffset.top > 0 && containerHeight !== (panelHeight - panelOffset.top - containerOffset.top)) {
            this.detailsContainer.getEl().setHeightPx(panelHeight - panelOffset.top - containerOffset.top);
        }
    }

    private addWidget(widget: WidgetView) {
        this.widgetViews.push(widget);
        this.detailsContainer.appendChild(widget);
    }

    private addWidgets(widgetViews: WidgetView[]) {
        widgetViews.forEach((widget) => {
            this.addWidget(widget);
        });
    }

    private removeAllWidgets() {
        this.widgetViews.forEach(widget => widget.remove());
        this.widgetViews = [];
    }

    updateViewer() {
        if (this.item) {
            this.viewer.setObject(this.item.getContentSummary());
        }
    }

    private layout(empty: boolean = true) {
        if (this.widgetsSelectionRow) {
            this.widgetsSelectionRow.setVisible(!empty);
        }
        if (this.viewer) {
            this.viewer.setVisible(!empty);
        }
        this.detailsContainer.setVisible(!empty);
        this.toggleClass('no-selection', empty);
    }

    onPanelSizeChanged(listener: () => void) {
        this.sizeChangedListeners.push(listener);
    }

    notifyPanelSizeChanged() {
        this.sizeChangedListeners.forEach((listener: ()=> void) => listener());
    }
}
