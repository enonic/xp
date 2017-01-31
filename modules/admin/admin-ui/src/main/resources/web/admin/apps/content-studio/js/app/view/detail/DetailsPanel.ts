import '../../../api.ts';
import {DetailsView} from './DetailsView';
import {WidgetView} from './WidgetView';
import ContentSummaryAndCompareStatus = api.content.ContentSummaryAndCompareStatus;

import ContentVersionSetEvent = api.content.event.ActiveContentVersionSetEvent;

export class DetailsPanel extends api.ui.panel.Panel {

    private sizeChangedListeners: {() : void}[] = [];

    protected detailsView: DetailsView;

    private detailsViewContainer: api.dom.DivEl;

    constructor(detailsView: DetailsView) {
        super('details-panel');
        this.detailsView = detailsView;
        this.setDoOffset(false);
        this.subscribeOnEvents();
        this.appendChild(this.detailsViewContainer = new api.dom.DivEl('details-view-container'));
    }

    public setActive() {
        this.detailsViewContainer.appendChild(this.detailsView);
    }

    protected subscribeOnEvents() {
        // must be implemented by children
    }

    public setItem(item: ContentSummaryAndCompareStatus): wemQ.Promise<any> {
        return this.detailsView.setItem(item);
    }

    public isVisibleOrAboutToBeVisible(): boolean {
        throw new Error('Must be implemented by inheritors');
    }

    public getActiveWidget(): WidgetView {
        return this.detailsView.getActiveWidget();
    }

    getItem(): ContentSummaryAndCompareStatus {
        return this.detailsView.getItem();
    }

    public notifyPanelSizeChanged() {
        this.sizeChangedListeners.forEach((listener: ()=> void) => listener());
        this.detailsView.notifyPanelSizeChanged();
    }

    public onPanelSizeChanged(listener: () => void) {
        this.sizeChangedListeners.push(listener);
    }

    public getType(): DETAILS_PANEL_TYPE {
        throw new Error('Must be implemented by inheritors');
    }

    public isMobile(): boolean {
        return this.getType() === DETAILS_PANEL_TYPE.MOBILE;
    }
}

export enum DETAILS_PANEL_TYPE {

    DOCKED,
    FLOATING,
    MOBILE
}
