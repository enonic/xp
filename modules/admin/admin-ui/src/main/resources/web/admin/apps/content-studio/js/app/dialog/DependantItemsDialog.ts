import "../../api.ts";

import {StatusSelectionItem} from "./StatusSelectionItem";
import {DependantView} from "./DependantView";

import ContentIconUrlResolver = api.content.ContentIconUrlResolver;
import ContentSummaryAndCompareStatus = api.content.ContentSummaryAndCompareStatus;
import BrowseItem = api.app.browse.BrowseItem;
import SelectionItem = api.app.browse.SelectionItem;
import ListBox = api.ui.selector.list.ListBox;

export class DependantItemsDialog extends api.ui.dialog.ModalDialog {

    private dialogName: string;

    private subTitle: api.dom.H6El;

    private itemList: ListBox<ContentSummaryAndCompareStatus>;

    private dependantsContainer: api.dom.DivEl;

    private dependantsHeader: api.dom.H6El;

    private dependantList: ListBox<ContentSummaryAndCompareStatus>;

    constructor(dialogName: string, dialogSubName: string, dependantsName: string) {
        super({
            title: new api.ui.dialog.ModalDialogHeader(dialogName)
        });
        this.addClass("dependant-dialog");

        this.dialogName = dialogName;

        this.subTitle = new api.dom.H6El("sub-title")
            .setHtml(dialogSubName);
        this.appendChildToTitle(this.subTitle);

        this.itemList = this.createItemList();
        this.itemList.addClass("item-list");
        this.appendChildToContentPanel(this.itemList);

        this.dependantsHeader = new api.dom.H6El("dependants-header").setHtml(dependantsName);

        this.dependantList = this.createDependantList();
        this.dependantList.addClass("dependant-list");

        this.dependantsContainer = new api.dom.DivEl('dependants');
        this.dependantsContainer.appendChildren(this.dependantsHeader, this.dependantList);

        let itemsChangedListener = (items) => {
            this.dependantsContainer.setVisible(this.dependantList.getItemCount() > 0);
        };
        this.dependantList.onItemsRemoved(itemsChangedListener);
        this.dependantList.onItemsAdded(itemsChangedListener);

        this.appendChildToContentPanel(this.dependantsContainer);

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

    show() {
        api.dom.Body.get().appendChild(this);
        super.show();
    }

    close() {
        super.close();
        this.remove();
    }

    setListItems(items: ContentSummaryAndCompareStatus[]) {
        this.setTitle(this.dialogName + (items.length > 1 ? "s" : ''));
        this.itemList.setItems(items);
    }

    setDependantItems(items: ContentSummaryAndCompareStatus[]) {
        this.dependantList.setItems(items);
    }

    setSubTitle(text: string) {
        this.subTitle.setHtml(text);
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
        let deleteItemViewer = new api.content.ContentSummaryAndCompareStatusViewer();

        deleteItemViewer.setObject(item);

        let browseItem = new BrowseItem<ContentSummaryAndCompareStatus>(item).
            setId(item.getId()).
            setDisplayName(item.getDisplayName()).
            setPath(item.getPath().toString()).
            setIconUrl(new ContentIconUrlResolver().setContent(item.getContentSummary()).resolve());

        return new StatusSelectionItem(deleteItemViewer, browseItem, () => {
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
        return DependantView.create().item(item.getContentSummary()).build();
    }

    getItemId(item: ContentSummaryAndCompareStatus): string {
        return item.getContentSummary().getId();
    }
}

