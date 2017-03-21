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

    private widgetsUpdateList: {[key: string]: (key: string, type: ApplicationEventType) => void } = {};

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

        const handleWidgetsUpdate = (e) => this.handleWidgetsUpdate(e);
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

    private handleWidgetsUpdate(event: ApplicationEvent) {
        const isWidgetUpdated = [
            ApplicationEventType.INSTALLED,
            ApplicationEventType.UNINSTALLED,
            ApplicationEventType.STARTED,
            ApplicationEventType.STOPPED,
            ApplicationEventType.UPDATED
        ].indexOf(event.getEventType()) > -1;

        if (isWidgetUpdated) {
            const key = event.getApplicationKey().getName();

            if (!this.widgetsUpdateList[key]) {
                this.widgetsUpdateList[key] = AppHelper.debounce((k, type) => this.handleWidgetUpdate(k, type), 1000);
            }
            this.widgetsUpdateList[key](key, event.getEventType());
        }
    }

    private handleWidgetUpdate(key: string, type: ApplicationEventType) {
        let widgetView = this.getWidgetByKey(key);
        const isActive = widgetView && this.activeWidget.getWidgetName() === widgetView.getWidgetName();

        const isRemoved = [
            ApplicationEventType.UNINSTALLED,
            ApplicationEventType.STOPPED
        ].indexOf(type) > -1;

        const isUpdated = !!widgetView;

        const updateView = (useDefault?: boolean) => {
            this.widgetsSelectionRow.updateWidgetsDropdown(this.widgetViews);
            if (useDefault) {
                this.activateDefaultWidget();
            } else {
                this.activeWidget.setActive();
            }
            this.widgetsSelectionRow.updateState(this.activeWidget);
        };

        if (isRemoved) {
            this.removeWidgetByKey(key);

            updateView(isActive);

        } else if (isUpdated) {
            this.fetchWidgetByKey(key).then((widget: Widget) => {
                widgetView = WidgetView.create().setName(widget.getDisplayName()).setDetailsView(this).setWidget(widget).build();
                this.updateWidget(widgetView);

                updateView();
            });
        } else { // newly installed
            this.fetchWidgetByKey(key).then((widget: Widget) => {
                widgetView = WidgetView.create().setName(widget.getDisplayName()).setDetailsView(this).setWidget(widget).build();
                this.addWidget(widgetView);

                updateView();
            });

        }
    }

    getCustomWidgetViewsAndUpdateDropdown(): wemQ.Promise<void> {
        let deferred = wemQ.defer<void>();
        if (!this.alreadyFetchedCustomWidgets) {
            this.fetchAndInitCustomWidgetViews().then(() => {
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

    private fetchCustomWidgetViews(): wemQ.Promise<Widget[]> {
        let getWidgetsByInterfaceRequest = new GetWidgetsByInterfaceRequest(this.getWidgetsInterfaceNames());

        return getWidgetsByInterfaceRequest.sendAndParse();
    }

    private fetchAndInitCustomWidgetViews(): wemQ.Promise<any> {
        return this.fetchCustomWidgetViews().then((widgets: Widget[]) => {
            widgets.forEach((widget) => {
                let widgetView = WidgetView.create().setName(widget.getDisplayName()).setDetailsView(this).setWidget(widget).build();
                this.addWidget(widgetView);
            });
        }).catch((reason: any) => {
            const msg = reason ? reason.message : 'Could not load widget descriptors.';
            api.notify.showError(msg);
        });
    }

    private fetchWidgetByKey(key: string): wemQ.Promise<Widget>  {
        return this.fetchCustomWidgetViews().then((widgets: Widget[]) => {
            for (let i = 0; i < widgets.length; i++) {
                if (widgets[i].getWidgetDescriptorKey().getApplicationKey().getName() === key) {
                    return widgets[i];
                }
            }
            return null;
        }).catch((reason: any) => {
            const msg = reason ? reason.message : 'Could not load widget descriptors.';
            api.notify.showError(msg);
            return null;
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

    private getWidgetByKey(key: string): WidgetView {
        for (let i = 0; i < this.widgetViews.length; i++) {
            if (this.widgetViews[i].getWidgetKey() === key) {
                return this.widgetViews[i];
            }
        }
        return null;
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

    private removeWidgetByKey(key: string) {
        const widget = this.getWidgetByKey(key);
        if (widget) {
            this.widgetViews = this.widgetViews.filter((view) => view !== widget);
            widget.remove();
        }
    }

    private updateWidget(widget: WidgetView) {
        for (let i = 0; i < this.widgetViews.length; i++) {
            if (this.widgetViews[i].getWidgetName() === widget.getWidgetName()) {
                this.widgetViews[i].replaceWith(widget);
                this.widgetViews[i] = widget;
                break;
            }
        }
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
