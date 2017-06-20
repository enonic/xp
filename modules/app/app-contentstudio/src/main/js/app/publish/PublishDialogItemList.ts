import {DialogItemList} from '../dialog/DependantItemsDialog';
import {StatusSelectionItem} from '../dialog/StatusSelectionItem';

import ContentSummaryAndCompareStatusViewer = api.content.ContentSummaryAndCompareStatusViewer;
import BrowseItem = api.app.browse.BrowseItem;
import ContentSummaryAndCompareStatus = api.content.ContentSummaryAndCompareStatus;
import Tooltip = api.ui.Tooltip;
import ObjectHelper = api.ObjectHelper;

export class PublishDialogItemList extends DialogItemList {

    private excludeChildrenIds: ContentId[] = [];

    private excludeChildrenListChangedListeners: {(items: ContentId[]): void}[] = [];

    private canBeEmpty: boolean = false;

    private debounceNotifyListChanged: Function;

    constructor() {
        super('publish-dialog-item-list');

        this.onItemsAdded(this.itemChangedHandler.bind(this));
        this.onItemsRemoved(this.itemChangedHandler.bind(this));

        this.onItemsRemoved(() => {
            this.getItemViews().forEach(view => this.updateRemovableState(view));
        });

        this.debounceNotifyListChanged = api.util.AppHelper.debounce(() => {
            this.notifyExcludeChildrenListChanged(this.excludeChildrenIds);
        }, 100, false);
    }

    public setContainsToggleable(value: boolean) {
        this.toggleClass('contains-toggleable', value);
    }

    public setCanBeEmpty(value: boolean) {
        this.canBeEmpty = value;
    }

    private itemChangedHandler() {
        this.toggleClass('contains-toggleable', this.getItemViews()
            .some(item => item.getBrowseItem().getModel().getContentSummary().hasChildren()));
    }

    createItemView(item: ContentSummaryAndCompareStatus, readOnly: boolean): PublicStatusSelectionItem {
        const itemView = <PublicStatusSelectionItem>super.createItemView(item, readOnly);

        if (this.canBeEmpty) {
            itemView.setIsRemovableFn(() => true);
        }

        this.updateRemovableState(itemView);

        return itemView;
    }

    protected createSelectionItem(viewer: ContentSummaryAndCompareStatusViewer,
                                  browseItem: BrowseItem<ContentSummaryAndCompareStatus>): PublicStatusSelectionItem {

        const item = new PublicStatusSelectionItem(viewer, browseItem);
        item.onItemStateChanged((contentId, enabled) => {

            const exist = ObjectHelper.contains(this.excludeChildrenIds, contentId);
            if (enabled) {
                if (exist) {
                    this.excludeChildrenIds = <ContentId[]>ObjectHelper.filter(this.excludeChildrenIds, contentId);
                }
            } else {
                if (!exist) {
                    this.excludeChildrenIds.push(contentId);
                }
            }
            this.debounceNotifyListChanged();
        });

        if (!ObjectHelper.contains(this.excludeChildrenIds, browseItem.getModel().getContentId())) {
            this.excludeChildrenIds.push(browseItem.getModel().getContentId());
        }

        return item;
    }

    public childTogglersAvailable(): boolean {
        return this.getItemViews().some(
            itemView => !!itemView.getIncludeChildrenToggler()
        );
    }

    public setReadOnly(value: boolean) {
        this.toggleClass('readonly', value);
        this.getItemViews().forEach((item) => {
            item.setReadOnly(value);
        });
    }

    public getItemViews(): PublicStatusSelectionItem[] {
        return <PublicStatusSelectionItem[]>super.getItemViews();
    }

    public getItemViewById(contentId: ContentId): PublicStatusSelectionItem {
        for (const view of <PublicStatusSelectionItem[]>super.getItemViews()) {
            if (view.getContentId().equals(contentId)) {
                return view;
            }
        }
    }

    public setExcludeChildrenIds(ids: ContentId[]) {
        this.excludeChildrenIds = ids;

        this.getItemViews().forEach(itemView => {
            if(itemView.getIncludeChildrenToggler()) {
                itemView.getIncludeChildrenToggler().toggle(ids.indexOf(itemView.getContentId()) < 0, true);
            }
        });

        this.debounceNotifyListChanged();
    }

    public getExcludeChildrenIds(): ContentId[] {
        return this.excludeChildrenIds.slice();
    }

    public clearExcludeChildrenIds() {
        this.excludeChildrenIds = [];
    }

    private updateRemovableState(view: PublicStatusSelectionItem) {
        view.toggleClass('removable', view.isRemovable());
    }

    public onExcludeChildrenListChanged(listener: (items: ContentId[]) => void) {
        this.excludeChildrenListChangedListeners.push(listener);
    }

    public unExcludeChildrenListChanged(listener: (items: ContentId[]) => void) {
        this.excludeChildrenListChangedListeners = this.excludeChildrenListChangedListeners.filter((current) => {
            return current !== listener;
        });
    }

    private notifyExcludeChildrenListChanged(items: ContentId[]) {
        this.excludeChildrenListChangedListeners.forEach((listener) => {
            listener(items);
        });
    }
}

export class PublicStatusSelectionItem extends StatusSelectionItem {

    private itemStateChangedListeners: {(itemId: ContentId, enabled: boolean): void}[] = [];

    private id: ContentId;

    private toggler: IncludeChildrenToggler;

    constructor(viewer: api.ui.Viewer<ContentSummaryAndCompareStatus>, item: BrowseItem<ContentSummaryAndCompareStatus>) {
        super(viewer, item);

        if (item.getModel().getContentSummary().hasChildren()) {
            this.toggler = new IncludeChildrenToggler();
            this.addClass('toggleable');

            this.toggler.onStateChanged((enabled: boolean) => {
                this.notifyItemStateChanged(this.getBrowseItem().getModel().getContentId(), enabled);
            });
        }

        this.id = item.getModel().getContentSummary().getContentId();
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

    getContentId(): ContentId {
        return this.id;
    }

    setTogglerActive(value: boolean) {
        this.toggleClass('toggleable', value);
    }

    public onItemStateChanged(listener: (item: ContentId, enabled: boolean) => void) {
        this.itemStateChangedListeners.push(listener);
    }

    public unItemStateChanged(listener: (item: ContentId, enabled: boolean) => void) {
        this.itemStateChangedListeners = this.itemStateChangedListeners.filter((current) => {
            return current !== listener;
        });
    }

    private notifyItemStateChanged(item: ContentId, enabled: boolean) {
        this.itemStateChangedListeners.forEach((listener) => {
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

        this.tooltip = new Tooltip(this, 'Show child items', 1000);

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

        this.toggleClass('readonly', this.readOnly);
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
