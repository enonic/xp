import "../../api.ts";
import {StatusSelectionItem} from "./StatusSelectionItem";
import {DependantItemViewer} from "./DependantItemViewer";

import ContentSummary = api.content.ContentSummary;
import ContentIds = api.content.ContentIds;
import ContentId = api.content.ContentId;
import GetDescendantsOfContents = api.content.resource.GetDescendantsOfContentsRequest;
import ContentSummaryAndCompareStatusFetcher = api.content.resource.ContentSummaryAndCompareStatusFetcher;
import ContentSummaryAndCompareStatus = api.content.ContentSummaryAndCompareStatus;
import CompareStatus = api.content.CompareStatus;
import BrowseItem = api.app.browse.BrowseItem;
import SelectionItem = api.app.browse.SelectionItem;
import ListBox = api.ui.selector.list.ListBox;
import LoadMask = api.ui.mask.LoadMask;
import DialogButton = api.ui.dialog.DialogButton;
import ResponsiveRanges = api.ui.responsive.ResponsiveRanges;
import ContentSummaryAndCompareStatusViewer = api.content.ContentSummaryAndCompareStatusViewer;

export class DependantItemsDialog extends api.ui.dialog.ModalDialog {

    protected actionButton: DialogButton;

    private dialogName: string;

    private ignoreItemsChanged: boolean;

    private subTitle: api.dom.H6El;

    private itemList: ListBox<ContentSummaryAndCompareStatus>;

    private dependantsContainer: api.dom.DivEl;

    private dependantsHeader: api.dom.H6El;

    private dependantList: ListBox<ContentSummaryAndCompareStatus>;

    private dependantsHeaderText: string;

    protected loadMask: LoadMask;

    protected autoUpdateTitle: boolean = true;

    protected loading: boolean = false;

    protected loadingRequested: boolean = false;

    protected previousScrollTop: number;

    protected dependantIds: ContentId[] = [];

    constructor(dialogName: string = '', dialogSubName: string = '', dependantsName: string = '') {
        super(dialogName);

        this.addClass('dependant-dialog');

        this.dialogName = dialogName;

        this.subTitle = new api.dom.H6El('sub-title')
            .setHtml(dialogSubName, false);
        this.appendChildToHeader(this.subTitle);

        this.itemList = this.createItemList();
        this.itemList.addClass('item-list');
        this.appendChildToContentPanel(this.itemList);

        let itemsChangedListener = (items) => {
            let count = this.itemList.getItemCount();
            if (this.autoUpdateTitle) {
                this.setTitle(this.dialogName + (count > 1 ? 's' : ''));
            }
        };
        this.itemList.onItemsRemoved(itemsChangedListener);
        this.itemList.onItemsAdded(itemsChangedListener);

        this.dependantsHeaderText = dependantsName;
        this.dependantsHeader = new api.dom.H6El('dependants-header').setHtml(dependantsName, false);

        this.dependantList = this.createDependantList();
        this.dependantList.addClass('dependant-list');

        this.dependantsContainer = new api.dom.DivEl('dependants');
        this.dependantsContainer.appendChildren(this.dependantsHeader, this.dependantList);

        let dependantsChangedListener = (items) => {
            let count = this.dependantList.getItemCount();
            this.dependantsContainer.setVisible(count > 0);
        };
        this.dependantList.onItemsRemoved(dependantsChangedListener);
        this.dependantList.onItemsAdded(dependantsChangedListener);

        this.appendChildToContentPanel(this.dependantsContainer);

        this.initLoadMask();

        this.getContentPanel().onScrolled(() => {
            this.doPostLoad();
        });

        this.getContentPanel().onScroll(() => {
            this.doPostLoad();
        });

    }

    protected updateDependantsHeader(header?: string) {
        this.dependantsHeader.setHtml(header || this.dependantsHeaderText, false);
    }

    private initLoadMask() {
        this.loadMask = new LoadMask(this.getContentPanel());
    }

    protected createItemList(): ListBox<ContentSummaryAndCompareStatus> {
        return new DialogItemList();
    }

    protected createDependantList(): ListBox<ContentSummaryAndCompareStatus> {
        return new DialogDependantList();
    }

    protected getItemList(): ListBox<ContentSummaryAndCompareStatus> {
        return this.itemList;
    }

    protected getDependantList(): ListBox<ContentSummaryAndCompareStatus> {
        return this.dependantList;
    }

    protected isIgnoreItemsChanged(): boolean {
        return this.ignoreItemsChanged;
    }

    protected setIgnoreItemsChanged(value: boolean) {
        this.ignoreItemsChanged = value;
    }

    show(hideLoadMask: boolean = false) {
        api.dom.Body.get().appendChild(this);
        super.show();
        if (!hideLoadMask) {
            this.appendChildToContentPanel(this.loadMask);
            this.loadMask.show();
        }
    }

    close() {
        super.close();
        this.remove();
        this.itemList.clearItems(true);
        this.dependantList.clearItems(true);
        this.dependantsContainer.setVisible(false);
        this.unlockControls();
    }

    setAutoUpdateTitle(value: boolean) {
        this.autoUpdateTitle = value;
    }

    setListItems(items: ContentSummaryAndCompareStatus[]) {
        this.itemList.setItems(items);
    }

    private extendsWindowHeightSize(): boolean {
        if (ResponsiveRanges._540_720.isFitOrBigger(this.getEl().getWidthWithBorder())) {
            let el = this.getEl();
            let bottomPosition: number = (el.getTopPx() || parseFloat(el.getComputedProperty('top')) || 0) +
                                         el.getMarginTop() +
                                         el.getHeightWithBorder() +
                                         el.getMarginBottom();

            if (window.innerHeight < bottomPosition) {
                return true;
            }
        }
        return false;
    }

    setDependantItems(items: ContentSummaryAndCompareStatus[]) {
        this.dependantList.setItems(items);

        if (this.extendsWindowHeightSize()) {
            this.centerMyself();
        }
    }

    addDependantItems(items: ContentSummaryAndCompareStatus[]) {
        this.dependantList.addItems(items);
    }

    setSubTitle(text: string) {
        this.subTitle.setHtml(text);
    }

    protected updateButtonCount(actionString: string, count: number) {
        this.actionButton.setLabel(count > 1 ? actionString + ' (' + count + ')' : actionString);
    }

    protected loadDescendantIds(filterStatuses?: CompareStatus[]) {
        let contents = this.getItemList().getItems();

        return new api.content.resource.GetDescendantsOfContentsRequest().setContentPaths(
            contents.map(content => content.getContentSummary().getPath())).setFilterStatuses(filterStatuses).sendAndParse()
            .then((result: ContentId[]) => {
                this.dependantIds = result;
            });
    }

    protected loadDescendants(from: number,
                              size: number): wemQ.Promise<ContentSummaryAndCompareStatus[]> {

        let ids = this.getDependantIds().slice(from, from + size);
        return api.content.resource.ContentSummaryAndCompareStatusFetcher.fetchByIds(ids);
    }

    protected countTotal(): number {
        return this.getItemList().getItemCount() + this.getDependantIds().length;
    }

    protected getDependantIds(): ContentId[] {
        return this.dependantIds;
    }

    private doPostLoad() {
        if (this.previousScrollTop !== this.getContentPanel().getEl().getScrollTop()) {
            setTimeout(this.postLoad.bind(this), 100);
        }
    }

    protected postLoad() {
        let lastVisible;

        this.previousScrollTop = this.getContentPanel().getEl().getScrollTop();

        let start = this.getContentPanel().getEl().getOffsetTop();
        let end = this.getContentPanel().getEl().getHeight() + start;

        let items = this.getDependantList().getItemViews();

        let visibleItems = [];

        items.forEach((item) => {
            let position = item.getEl().getOffsetTop();
            if (position >= start && position <= end) {
                visibleItems.push(item);
            }
        });

        lastVisible = items.indexOf(visibleItems[visibleItems.length - 1]);

        let size = this.getDependantList().getItemCount();

        if (!this.loading) {
            if (lastVisible + GetDescendantsOfContents.LOAD_SIZE / 2 >= size && size < this.getDependantIds().length) {

                this.loadMask.show();
                this.loading = true;

                this.loadDescendants(size, GetDescendantsOfContents.LOAD_SIZE).then((newItems) => {

                    this.addDependantItems(newItems);
                    this.loading = false;
                    this.loadMask.hide();
                    if (this.loadingRequested) {
                        this.loadingRequested = false;
                        this.postLoad();
                    }
                });
            }
        } else {
            this.loadingRequested = true;
        }
    }

    protected lockControls() {
        this.addClass('locked');
        this.actionButton.setEnabled(false);
    }

    protected unlockControls() {
        this.removeClass('locked');
        this.actionButton.setEnabled(true);
    }

    protected toggleControls(enable: boolean) {
        if (enable) {
            this.unlockControls();
        } else {
            this.lockControls();
        }
    }

}

export class DialogItemList extends ListBox<ContentSummaryAndCompareStatus> {

    createItemView(item: ContentSummaryAndCompareStatus, readOnly: boolean): StatusSelectionItem {
        let itemViewer = new ContentSummaryAndCompareStatusViewer();

        itemViewer.setObject(item);

        let browseItem = new BrowseItem<ContentSummaryAndCompareStatus>(item).setId(item.getId()).setDisplayName(
            item.getDisplayName()).setPath(item.getPath().toString()).setIconUrl(
            new api.content.util.ContentIconUrlResolver().setContent(item.getContentSummary()).resolve());

        let statusItem = this.createSelectionItem(itemViewer, browseItem);

        statusItem.setIsRemovableFn(() => this.getItemCount() > 1);
        statusItem.setRemoveHandlerFn(() => this.removeItem(item));

        return statusItem;
    }

    protected createSelectionItem(viewer: ContentSummaryAndCompareStatusViewer,
                                  browseItem: BrowseItem<ContentSummaryAndCompareStatus>): StatusSelectionItem {
        return new StatusSelectionItem(viewer, browseItem);
    }

    getItemId(item: ContentSummaryAndCompareStatus): string {
        return item.getContentSummary().getId();
    }
}

export class DialogDependantList extends ListBox<ContentSummaryAndCompareStatus> {

    constructor(className?: string) {
        super(className);
    }

    createItemView(item: ContentSummaryAndCompareStatus, readOnly: boolean): api.dom.Element {

        let dependantViewer = new DependantItemViewer();

        dependantViewer.setObject(item);

        let browseItem = new BrowseItem<ContentSummaryAndCompareStatus>(item).setId(item.getId()).setDisplayName(
            item.getDisplayName()).setPath(item.getPath().toString()).setIconUrl(
            new api.content.util.ContentIconUrlResolver().setContent(item.getContentSummary()).resolve());

        let selectionItem = new StatusSelectionItem(dependantViewer, browseItem);

        return selectionItem;
    }

    getItemId(item: ContentSummaryAndCompareStatus): string {
        return item.getContentSummary().getId();
    }
}
