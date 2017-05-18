import {PublishDialogItemList} from "./PublishDialogItemList";
import {PublishDialogDependantList, isContentSummaryValid} from "./PublishDialogDependantList";
import ResolvePublishDependenciesResult = api.content.resource.result.ResolvePublishDependenciesResult;
import ContentSummaryAndCompareStatus = api.content.ContentSummaryAndCompareStatus;
import CompareStatus = api.content.CompareStatus;

export class PublishProcessor {

    private itemList: PublishDialogItemList;

    private dependantList: PublishDialogDependantList;

    private excludedIds: ContentId[] = [];

    private dependantIds: ContentId[] = [];

    private containsInvalid: boolean;

    private allPublishable: boolean;

    private ignoreItemsChanged: boolean;

    private loadingStartedListeners: {(): void}[] = [];

    private loadingFinishedListeners: {(): void}[] = [];

    constructor(itemList: PublishDialogItemList, dependantList: PublishDialogDependantList) {
        this.itemList = itemList;
        this.dependantList = dependantList;

        this.initListeners();
    }

    private initListeners() {
        this.itemList.onItemsRemoved(() => {
            if (!this.ignoreItemsChanged) {
                this.reloadPublishDependencies();
            }
        });

        this.itemList.onExcludeChildrenListChanged(() => {
            this.reloadPublishDependencies(true);
        });

        this.dependantList.onItemClicked((item: ContentSummaryAndCompareStatus) => {
            if (!isContentSummaryValid(item)) {
                new api.content.event.EditContentEvent([item]).fire();
            }
        });

        this.dependantList.onItemRemoveClicked((item: ContentSummaryAndCompareStatus) => {
            this.excludedIds.push(item.getContentId());
            this.reloadPublishDependencies(true);
        });
    }

    reloadPublishDependencies(resetDependantItems?: boolean): wemQ.Promise<void> {

        this.notifyLoadingStarted();

        let ids = this.getContentToPublishIds();

        let resolveDependenciesRequest = api.content.resource.ResolvePublishDependenciesRequest.create().setIds(ids).setExcludedIds(
            this.excludedIds).setExcludeChildrenIds(this.itemList.getExcludeChildrenIds()).build();

        return resolveDependenciesRequest.sendAndParse().then((result: ResolvePublishDependenciesResult) => {

            this.dependantIds = result.getDependants().slice();

            this.dependantList.setRequiredIds(result.getRequired());

            this.containsInvalid = result.isContainsInvalid();
            this.allPublishable = result.isAllPublishable();


            return this.loadDescendants(0, 20).then((dependants: ContentSummaryAndCompareStatus[]) => {
                if (resetDependantItems) { // just opened or first time loading children
                    this.dependantList.setItems(dependants);
                } else {
                    this.filterDependantItems(dependants);
                }
                this.notifyLoadingFinished();

            });
        }).catch(() => {
            this.notifyLoadingFinished();
        });
    }

    public reset() {
        this.itemList.setExcludeChildrenIds([]);
        this.itemList.setItems([]);
        this.itemList.setReadOnly(false);

        this.dependantList.setRequiredIds([]);
        this.dependantList.setItems([]);
        this.dependantList.setReadOnly(false);
    }

    public getContentToPublishIds(): ContentId[] {
        return this.itemList.getItems().map(item => {
            return item.getContentId();
        });
    }

    public countTotal(): number {
        return this.countToPublish(this.itemList.getItems()) + this.dependantIds.length;
    }


    public isAllPublishable() {
        return this.allPublishable;
    }

    public isContainsInvalid() {
        return this.containsInvalid;
    }

    public getDependantIds(): ContentId[] {
        return this.dependantIds;
    }

    public getExcludedIds(): ContentId[] {
        return this.excludedIds;
    }

    public setExcludedIds(ids: ContentId[]) {
        this.excludedIds = !!ids ? ids : [];
    }

    public resetExcludedIds() {
        this.excludedIds = [];
    }

    public setIgnoreItemsChanged(value: boolean) {
        this.ignoreItemsChanged = value;
    }

    private countToPublish(summaries: ContentSummaryAndCompareStatus[]): number {
        return summaries.reduce((count, summary: ContentSummaryAndCompareStatus) => {
            return summary.getCompareStatus() !== CompareStatus.EQUAL ? ++count : count;
        }, 0);
    }

    private loadDescendants(from: number,
                            size: number): wemQ.Promise<ContentSummaryAndCompareStatus[]> {

        let ids = this.dependantIds.slice(from, from + size);
        return api.content.resource.ContentSummaryAndCompareStatusFetcher.fetchByIds(ids);
    }

    private filterDependantItems(dependants: ContentSummaryAndCompareStatus[]) {
        let itemsToRemove = this.dependantList.getItems().filter(
            (oldDependantItem: ContentSummaryAndCompareStatus) => !dependants.some(
                (newDependantItem) => oldDependantItem.equals(newDependantItem)));
        this.dependantList.removeItems(itemsToRemove);

    }

    onLoadingStarted(listener: () => void) {
        this.loadingStartedListeners.push(listener);
    }

    unLoadingStarted(listener: () => void) {
        this.loadingStartedListeners = this.loadingStartedListeners.filter((curr) => {
            return listener !== curr;
        });
    }

    private notifyLoadingStarted() {
        this.loadingStartedListeners.forEach((listener) => {
            listener();
        });
    }

    onLoadingFinished(listener: () => void) {
        this.loadingFinishedListeners.push(listener);
    }

    unLoadingFinished(listener: () => void) {
        this.loadingFinishedListeners = this.loadingFinishedListeners.filter((curr) => {
            return listener !== curr;
        });
    }

    private notifyLoadingFinished() {
        this.loadingFinishedListeners.forEach((listener) => {
            listener();
        });
    }

}

