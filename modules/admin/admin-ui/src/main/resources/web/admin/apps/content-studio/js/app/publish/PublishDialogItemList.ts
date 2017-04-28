import {DialogItemList} from '../dialog/DependantItemsDialog';
import {StatusSelectionItem} from '../dialog/StatusSelectionItem';

import ContentSummaryAndCompareStatusViewer = api.content.ContentSummaryAndCompareStatusViewer;
import BrowseItem = api.app.browse.BrowseItem;
import ContentSummaryAndCompareStatus = api.content.ContentSummaryAndCompareStatus;
import Tooltip = api.ui.Tooltip;

export class PublishDialogItemList extends DialogItemList {

    private excludeChildrenIds: ContentId[] = [];

    private chaListeners: {(items: ContentId[]): void}[] = [];

    private debonceNotifyListChanged: Function;

    constructor() {
        super('publish-dialog-item-list');

        this.onItemsAdded(this.itemChangedHandler.bind(this));
        this.onItemsRemoved(this.itemChangedHandler.bind(this));

        this.debonceNotifyListChanged = api.util.AppHelper.debounce(() => {
            this.notifyExcludeChildrenListChanged(this.excludeChildrenIds);
        }, 100, false);
    }

    private itemChangedHandler() {
        this.toggleClass('contains-toggleable', this.getItemViews()
            .some(item => item.getBrowseItem().getModel().getContentSummary().hasChildren()));
    }

    protected createSelectionItem(viewer: ContentSummaryAndCompareStatusViewer,
                                  browseItem: BrowseItem<ContentSummaryAndCompareStatus>): StatusSelectionItem {

        const item = new PublicStatusSelectionItem(viewer, browseItem);
        item.onItemStateChanged((contentId, enabled) => {

            const index = this.excludeChildrenIds.indexOf(contentId);
            if (enabled) {
                if (index >= 0) {
                    this.excludeChildrenIds.splice(index, 1);
                }
            } else {
                if (index < 0) {
                    this.excludeChildrenIds.push(contentId);
                }
            }
            this.debonceNotifyListChanged();
        });

        if(!browseItem.getModel().getContentSummary().hasChildren()) {
            this.excludeChildrenIds.push(browseItem.getModel().getContentId());
        }

        if(item.isRemovable()) {
            item.addClass('removable');
        }

        return item;
    }

    public setReadOnly(value: boolean) {
        this.getItemViews().forEach((item) => {
            item.setReadOnly(value);
        });
    }

    public getItemViews(): PublicStatusSelectionItem[] {
        return <PublicStatusSelectionItem[]>super.getItemViews();
    }

    public getExcludeChildrenIds(): ContentId[] {
        return this.excludeChildrenIds;
    }

    public clearExcludeChildrenIds() {
        this.excludeChildrenIds = [];
    }

    public onExcludeChildrenListChanged(listener: (items: ContentId[]) => void) {
        this.chaListeners.push(listener);
    }

    public unExcludeChildrenListChanged(listener: (items: ContentId[]) => void) {
        this.chaListeners = this.chaListeners.filter((current) => {
            return current !== listener;
        });
    }

    private notifyExcludeChildrenListChanged(items: ContentId[]) {
        this.chaListeners.forEach((listener) => {
            listener(items);
        });
    }
}

export class PublicStatusSelectionItem extends StatusSelectionItem {

    private chaListeners: {(itemId: ContentId, enabled: boolean): void}[] = [];

    private toggler: IncludeChildrenToggler;

    constructor(viewer: api.ui.Viewer<ContentSummaryAndCompareStatus>, item: BrowseItem<ContentSummaryAndCompareStatus>) {
        super(viewer, item);

        if(item.getModel().getContentSummary().hasChildren()) {
            this.addClass('toggleable');

            this.toggler = new IncludeChildrenToggler();

            this.toggler.onStateChanged((enabled: boolean) => {
                this.notifyItemStateChanged(this.getBrowseItem().getModel().getContentId(), enabled);
            });
        }
    }

    public doRender(): wemQ.Promise<boolean> {

        return super.doRender().then((rendered) => {

            if (this.toggler) {
                this.toggler.insertAfterEl(this.removeEl);
            }

            return rendered;
        });
    }

    public setReadOnly(value: boolean) {
        if (this.toggler) {
            this.toggler.setReadOnly(value);
        }
    }

    getIncludeChildrenToggler(): IncludeChildrenToggler {
        return this.toggler;
    }

    public onItemStateChanged(listener: (item: ContentId, enabled: boolean) => void) {
        this.chaListeners.push(listener);
    }

    public unItemStateChanged(listener: (item: ContentId, enabled: boolean) => void) {
        this.chaListeners = this.chaListeners.filter((current) => {
            return current !== listener;
        });
    }

    private notifyItemStateChanged(item: ContentId, enabled: boolean) {
        this.chaListeners.forEach((listener) => {
            listener(item, enabled);
        });
    }
}
class IncludeChildrenToggler extends api.dom.DivEl {

    private stateChangedListeners: {(enabled: boolean): void}[] = [];

    private tooltip: Tooltip;

    private readOnly: boolean;

    constructor() {
        super('icon icon-tree');
        this.addClass('include-children-toggler');

        this.tooltip = new Tooltip(this, '', 1000);

        this.onClicked(() => {
            this.toggle();
        });
    }

    toggle(condition?: boolean, silent?: boolean) {
        if (!this.readOnly) {
            this.toggleClass('on', condition);

            this.tooltip.setText(this.isEnabled() ? 'Exclude child items' : 'Include child items');

            if (!silent) {
                this.notifyStateChanged(this.isEnabled());
            }
        }
    }

    setReadOnly(value: boolean) {
        this.readOnly = value;
        this.tooltip.setActive(!value);
    }

    isEnabled(): boolean {
        return this.hasClass('on');
    }

    public onStateChanged(listener: (enabled: boolean) => void) {
        this.stateChangedListeners.push(listener);
    }

    public unStateChanged(listener: (enabled: boolean) => void) {
        this.stateChangedListeners = this.stateChangedListeners.filter((current) => {
            return current !== listener;
        });
    }

    private notifyStateChanged(enabled: boolean) {
        this.stateChangedListeners.forEach((listener) => {
            listener(enabled);
        });
    }
}
