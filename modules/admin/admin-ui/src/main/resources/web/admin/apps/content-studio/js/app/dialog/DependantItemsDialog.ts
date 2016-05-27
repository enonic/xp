import "../../api.ts";
import {StatusSelectionItem} from "./StatusSelectionItem";
import {DependantItemViewer} from "./DependantItemViewer";

import ContentIconUrlResolver = api.content.ContentIconUrlResolver;
import ContentSummary = api.content.ContentSummary;
import ContentSummaryAndCompareStatusFetcher = api.content.ContentSummaryAndCompareStatusFetcher;
import ContentSummaryAndCompareStatus = api.content.ContentSummaryAndCompareStatus;
import CompareStatus = api.content.CompareStatus;
import BrowseItem = api.app.browse.BrowseItem;
import SelectionItem = api.app.browse.SelectionItem;
import ListBox = api.ui.selector.list.ListBox;
import LoadMask = api.ui.mask.LoadMask;

export class DependantItemsDialog extends api.ui.dialog.ModalDialog {

    private dialogName: string;

    private autoUpdateTitle: boolean = true;

    private ignoreItemsChanged: boolean;

    private subTitle: api.dom.H6El;

    private itemList: ListBox<ContentSummaryAndCompareStatus>;

    private dependantsContainer: api.dom.DivEl;

    private dependantsHeader: api.dom.H6El;

    private dependantList: ListBox<ContentSummaryAndCompareStatus>;

    protected loadMask: LoadMask;

    constructor(dialogName: string, dialogSubName: string, dependantsName: string) {
        super({
            title: new api.ui.dialog.ModalDialogHeader(dialogName)
        });
        this.addClass("dependant-dialog");

        this.dialogName = dialogName;

        this.subTitle = new api.dom.H6El("sub-title")
            .setHtml(dialogSubName, false);
        this.appendChildToTitle(this.subTitle);

        this.itemList = this.createItemList();
        this.itemList.addClass("item-list");
        this.appendChildToContentPanel(this.itemList);

        let itemsChangedListener = (items) => {
            let count = this.itemList.getItemCount();
            if (this.autoUpdateTitle) {
                this.setTitle(this.dialogName + (count > 1 ? "s" : ''));
            }

            this.toggleClass("contains-removable", (count > 1));
        };
        this.itemList.onItemsRemoved(itemsChangedListener);
        this.itemList.onItemsAdded(itemsChangedListener);

        this.dependantsHeader = new api.dom.H6El("dependants-header").setHtml(dependantsName, false);

        this.dependantList = this.createDependantList();
        this.dependantList.addClass("dependant-list");

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

    show() {
        api.dom.Body.get().appendChild(this);
        super.show();
        this.appendChildToContentPanel(this.loadMask);
        this.loadMask.show();
    }

    close() {
        super.close();
        this.remove();
        this.itemList.clearItems();
        this.dependantList.clearItems();
    }

    setAutoUpdateTitle(value: boolean) {
        this.autoUpdateTitle = value;
    }

    setListItems(items: ContentSummaryAndCompareStatus[]) {
        this.itemList.setItems(items);
        if (items.length == 1) {
            (<StatusSelectionItem>this.getItemList().getItemView(items[0])).hideRemoveButton();
        }
    }

    setDependantItems(items: ContentSummaryAndCompareStatus[]) {
        this.dependantList.setItems(items);
    }

    setSubTitle(text: string) {
        this.subTitle.setHtml(text);
    }

    protected loadDescendants(summaries: ContentSummaryAndCompareStatus[]): wemQ.Promise<ContentSummaryAndCompareStatus[]> {
        return new api.content.GetDescendantsOfContents()
            .setContentPaths(summaries.map(summary => summary.getContentSummary().getPath())).sendAndParse()
            .then((result: api.content.ContentResponse<ContentSummary>) => {

                return api.content.CompareContentRequest.fromContentSummaries(result.getContents()).sendAndParse()
                    .then((compareContentResults: api.content.CompareContentResults) => {

                        return ContentSummaryAndCompareStatusFetcher
                            .updateCompareStatus(result.getContents(), compareContentResults);
                    });
            });
    }

}

export class DialogItemList extends ListBox<ContentSummaryAndCompareStatus> {

    constructor(className?: string) {
        super(className);

        this.onItemsRemoved((items: ContentSummaryAndCompareStatus[]) => {
            if (this.getItemCount() == 1) {
                (<StatusSelectionItem>this.getItemViews()[0]).hideRemoveButton();
            }
        });
    }

    createItemView(item: ContentSummaryAndCompareStatus, readOnly: boolean): api.dom.Element {
        let itemViewer = new api.content.ContentSummaryAndCompareStatusViewer();

        itemViewer.setObject(item);

        let browseItem = new BrowseItem<ContentSummaryAndCompareStatus>(item).
            setId(item.getId()).
            setDisplayName(item.getDisplayName()).
            setPath(item.getPath().toString()).
            setIconUrl(new ContentIconUrlResolver().setContent(item.getContentSummary()).resolve());

        return new StatusSelectionItem(itemViewer, browseItem, () => {
            this.removeItem(item);
        });
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

        let browseItem = new BrowseItem<ContentSummaryAndCompareStatus>(item).
            setId(item.getId()).
            setDisplayName(item.getDisplayName()).
            setPath(item.getPath().toString()).
            setIconUrl(new ContentIconUrlResolver().setContent(item.getContentSummary()).resolve());

        let selectionItem = new StatusSelectionItem(dependantViewer, browseItem);

        return selectionItem;
    }

    getItemId(item: ContentSummaryAndCompareStatus): string {
        return item.getContentSummary().getId();
    }
}

