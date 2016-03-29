module app.publish {

    import ContentIconUrlResolver = api.content.ContentIconUrlResolver;
    import BrowseItem = api.app.browse.BrowseItem;
    import ContentPath = api.content.ContentPath;
    import ContentSummaryAndCompareStatus = api.content.ContentSummaryAndCompareStatus;
    import DialogButton = api.ui.dialog.DialogButton;
    import PublishContentRequest = api.content.PublishContentRequest;
    import ResolvePublishContentResultJson = api.content.json.ResolvePublishContentResultJson;
    import CompareStatus = api.content.CompareStatus;
    import ContentId = api.content.ContentId;

    /**
     * ContentPublishDialog manages list of initially checked (initially requested) items resolved via ResolvePublishDependencies command.
     * Resolved items are converted into array of SelectionPublishItem<ContentPublishItem> items and stored in selectionItems property.
     * ContentPublishItem contains info for the initially checked item with number of children and dependants items that will get published with it.
     * Dependant items number will change depending on includeChildren checkbox state as
     * resolved dependencies usually differ in that case.
     */
    export class ContentPublishDialog extends api.ui.dialog.ModalDialog {

        private modelName: string;

        private selectionItems: SelectionPublishItem<ContentPublishItem>[] = [];

        private publishButton: DialogButton;

        private publishAction: api.ui.Action;

        private initialItemsView: PublishDialogItemList = new PublishDialogItemList();

        private dependenciesItemsView: PublishDialogDependantsItemList = new PublishDialogDependantsItemList();

        private includeChildItemsCheck: api.ui.Checkbox;

        private subheaderMessage: api.dom.H6El = new api.dom.H6El("publish-dialog-subheader");

        private selectedContents: ContentSummaryAndCompareStatus[];

        private initialContentsResolvedWithChildren: ContentsResolved<ContentPublishItem> = new ContentsResolved<ContentPublishItem>();

        private initialContentsResolvedWithoutChildren: ContentsResolved<ContentPublishItem> = new ContentsResolved<ContentPublishItem>();

        private dependenciesContentsResolvedWithChildren: ContentsResolved<ContentPublishItem> = new ContentsResolved<ContentPublishItem>();

        private dependenciesContentsResolvedWithoutChildren: ContentsResolved<ContentPublishItem> = new ContentsResolved<ContentPublishItem>();

        private includeChildrenCheckedListener: () => void;

        constructor() {
            super({
                title: new api.ui.dialog.ModalDialogHeader("Publishing Wizard")
            });

            this.modelName = "item";

            this.initIncludeChildrenCheckbox();

            this.getEl().addClass("publish-dialog");

            this.appendChildToContentPanel(this.initialItemsView);
            this.appendChildToContentPanel(this.dependenciesItemsView);

            this.initSubheaderMessage();

            this.publishButton = this.setPublishAction(new ContentPublishDialogAction());
            this.publishButton.setEnabled(false);

            this.getPublishAction().onExecuted(() => {
                this.doPublish();
            });

            this.addCancelButtonToBottom();

            this.appendChildToContentPanel(this.includeChildItemsCheck);
        }

        initAndOpen() {
            this.renderSelectedContentsWhileItemsGettingResolved();
            if (!this.atLeastOneInitialItemHasChild()) {
                this.includeChildItemsCheck.setVisible(false);
                this.getButtonRow().addClass("no-checkbox");
            }

            this.runResolveTasks(this.getResolveTasks(), () => this.centerMyself());
            this.open();
        }

        private runResolveTasks(resolveTasks: wemQ.Promise<any>[], doneCallback?: () => void) {

            this.showLoadingSpinnerAtButton();
            this.publishButton.setEnabled(false);

            wemQ.all(resolveTasks).done(() => {
                if (doneCallback) {
                    doneCallback();
                }
                this.hideLoadingSpinnerAtButton();
                this.countItemsToPublishAndUpdateCounterElements();
            });
        }

        private getResolveTasks(): wemQ.Promise<any>[] {
            return [this.resolvePublishDependenciesIfNeededAndUpdateView()];
        }

        show() {
            api.dom.Body.get().appendChild(this);
            super.show();
        }

        close() {
            super.close();
            this.remove();
        }

        setPublishAction(action: api.ui.Action): DialogButton {
            this.publishAction = action;
            return this.addAction(action, true, true);
        }

        getPublishAction(): api.ui.Action {
            return this.publishAction;
        }

        setSelectedContents(contents: ContentSummaryAndCompareStatus[]) {
            this.selectedContents = contents;
        }

        private renderSelectedContentsWhileItemsGettingResolved() {

            var initiallySelectedContents: ContentPublishItem[] = ContentPublishItem.buildPublishItemsFromContentSummaryAndCompareStatuses(
                this.sortContentSummariesArrayByPath(this.selectedContents).slice(0, 15));

            initiallySelectedContents.forEach((content: ContentPublishItem) => {
                var item: SelectionPublishItem<ContentPublishItem> = new SelectionPublishItemBuilder<ContentPublishItem>().create().
                    setViewer(new app.publish.ResolvedPublishContentViewer<ContentPublishItem>()).
                    setContent(content).
                    setIsCheckBoxEnabled(false).
                    setRemovable(true).
                    build();
                this.initialItemsView.appendChild(item);
            });
        }

        private sortContentSummariesArrayByPath(arrayToSort: ContentSummaryAndCompareStatus[]): ContentSummaryAndCompareStatus[] {
            arrayToSort.sort((contentA, contentB) => {
                var pathA = contentA.getPath().toString(),
                    pathB = contentB.getPath().toString();
                return (pathA < pathB) ? -1 : (pathA > pathB) ? 1 : 0;
            });
            return arrayToSort;
        }

        // inits selection items and appends them to display view
        renderResolvedPublishItems() {
            this.initSelectionItems();
            this.initialItemsView.clear();

            this.selectionItems.forEach((item: SelectionPublishItem<ContentPublishItem>)  => {
                if (!item.isHidden()) {
                    this.initialItemsView.appendChild(item);
                }
            });
        }

        private initSubheaderMessage() {
            this.appendChildToTitle(this.subheaderMessage);
            this.subheaderMessage.setHtml("Resolving items...");
        }

        private initIncludeChildrenCheckbox() {

            var resolveTasks: wemQ.Promise<any>[] = [];

            this.includeChildrenCheckedListener = () => {
                resolveTasks.push(this.resolvePublishDependenciesIfNeededAndUpdateView());
                this.runResolveTasks(resolveTasks);
            };

            this.includeChildItemsCheck = new api.ui.Checkbox();
            this.includeChildItemsCheck.addClass('include-child-check');
            this.includeChildItemsCheck.onValueChanged(this.includeChildrenCheckedListener);
            this.includeChildItemsCheck.setLabel('Include child items');

            this.overwriteDefaultArrows(this.includeChildItemsCheck);
        }

        /**
         * Inits selection items from resolved items returned via resolve request.
         * Method is called from renderResolvedPublishItems() so it also saves and restores unchecked items.
         */
        private initSelectionItems() {

            this.selectionItems = [];

            var pushRequestedItems: ContentPublishItem[] = this.includeChildItemsCheck.isChecked()
                ? this.initialContentsResolvedWithChildren.getContentsResolved()
                : this.initialContentsResolvedWithoutChildren.getContentsResolved();

            pushRequestedItems.forEach((content: ContentPublishItem) => {
                var item: SelectionPublishItem<ContentPublishItem> = new SelectionPublishItemBuilder<ContentPublishItem>().create().
                    setViewer(new app.publish.ResolvedPublishContentViewer<ContentPublishItem>()).
                    setContent(content).
                    setIsCheckBoxEnabled(false).
                    setRemovable(true).
                    setRemoveCallback(() => {
                        if (this.selectionItems.length == 1) { // this is the last item in the dialog
                            this.close();
                            return;
                        }
                        this.removeFromInitialSelection(item);
                        this.initialContentsResolvedWithChildren.setAlreadyResolved(false);
                        this.initialContentsResolvedWithoutChildren.setAlreadyResolved(false);
                        this.dependenciesContentsResolvedWithChildren.setAlreadyResolved(false);
                        this.dependenciesContentsResolvedWithoutChildren.setAlreadyResolved(false);
                        this.runResolveTasks(this.getResolveTasks());
                    }).
                    build();
                this.selectionItems.push(item);

                if(this.isInvalidContent(content)) {
                    this.addOnClickedListener(item);
                    item.addClass("invalid");
                }
            });
        }

        private removeSelectionItem(item: SelectionPublishItem<ContentPublishItem>) {
            var index = this.indexOf(item);
            if (index < 0) {
                return;
            }

            this.selectionItems[index].remove();
            this.selectionItems.splice(index, 1);

            if (this.selectionItems.length == 0) {
                this.close();
            }
        }

        private indexOf(item: SelectionPublishItem<ContentPublishItem>): number {
            for (var i = 0; i < this.selectionItems.length; i++) {
                if (item.getBrowseItem().getId() == this.selectionItems[i].getBrowseItem().getId()) {
                    return i;
                }
            }
            return -1;
        }

        private removeFromInitialSelection(item: SelectionPublishItem<ContentPublishItem>) {
            for (var i = 0; i < this.selectedContents.length; i++) {
                if (item.getBrowseItem().getId() == this.selectedContents[i].getId()) {
                    this.selectedContents.splice(i, 1);
                    break;
                }
            }
        }

        private getSelectedContentsIds(): ContentId[] {

            return this.selectedContents.
                filter((el) => {
                    return (typeof el !== "undefined");
                }).
                map((el) => {
                    return new ContentId(el.getId());
                })
        }

        /**
         * Ensures that contents that were unchecked in the dialog are restored after re-render.
         * @param unCheckedRequestedContentsIds
         * @param publishRequestedSelectionItems
         */
        private restoreUncheckedState(unCheckedRequestedContentsIds: string[],
                                      publishRequestedSelectionItems: SelectionPublishItem<ContentPublishItem>[]) {
            publishRequestedSelectionItems.forEach((item: SelectionPublishItem<ContentPublishItem>)  => {
                if (unCheckedRequestedContentsIds.indexOf(item.getBrowseItem().getId()) > -1) {
                    item.setChecked(false);
                }
            });
        }

        /**
         * Perform request to resolve dependency items of passed item.
         */
        private resolvePublishDependenciesIfNeededAndUpdateView(): wemQ.Promise<any> {

            if ((this.includeChildItemsCheck.isChecked() && !this.dependenciesContentsResolvedWithChildren.isAlreadyResolved()) ||
                (!this.includeChildItemsCheck.isChecked() && !this.dependenciesContentsResolvedWithoutChildren.isAlreadyResolved())) {

                var resolveDependenciesRequest = new api.content.ResolvePublishDependenciesRequest(this.getSelectedContentsIds(),
                    this.includeChildItemsCheck.isChecked());

                return resolveDependenciesRequest.send().then((jsonResponse: api.rest.JsonResponse<ResolvePublishContentResultJson>) => {
                    this.initResolvedPublishItems(jsonResponse.getResult());
                    this.renderResolvedPublishItems();
                    this.initResolvedDependenciesItems(jsonResponse.getResult());
                    this.renderResolvedDependenciesItems();
                });
            } else {
                this.renderResolvedDependenciesItems();
                return wemQ<any>(null);
            }
        }

        private renderResolvedDependenciesItems() {
            this.dependenciesItemsView.clear();

            var dependenciesItems: ContentsResolved<ContentPublishItem> =
                this.includeChildItemsCheck.isChecked() ?
                this.dependenciesContentsResolvedWithChildren :
                this.dependenciesContentsResolvedWithoutChildren;

            if (dependenciesItems.getContentsResolved().length > 0) {
                var dependenciesHeader: api.dom.H6El = new api.dom.H6El("dependencies-header");
                dependenciesHeader.setHtml("Other items that will be published");
                this.dependenciesItemsView.appendChild(dependenciesHeader);
            }

            // append dependencies to view
            dependenciesItems.getContentsResolved().forEach((dependency: ContentPublishItem)  => {
                var dependencyView: SelectionPublishItem<ContentPublishItem> = new SelectionPublishItemBuilder<ContentPublishItem>().create().
                    setViewer(new app.publish.ResolvedDependantContentViewer<ContentPublishItem>()).
                    setContent(dependency).
                    setIsCheckBoxEnabled(false).
                    setIsCheckboxHidden(true).
                    setShowStatus(false).
                    build();

                this.dependenciesItemsView.appendDependency(dependencyView);

                if(this.isInvalidContent(dependency)) {
                    this.addOnClickedListener(dependencyView);
                    dependencyView.addClass("invalid");
                }
            });

            if (this.extendsWindowHeightSize()) {
                this.centerMyself();
            }
        }

        private addOnClickedListener(dependencyView: SelectionPublishItem<ContentPublishItem>) {
            dependencyView.onClicked(() => {
                var contentId = new api.content.ContentId(dependencyView.getBrowseItem().getId());
                api.content.ContentSummaryAndCompareStatusFetcher.fetch(contentId).then((contentSummary: ContentSummaryAndCompareStatus) => {
                    this.close();
                    new api.content.event.EditContentEvent([contentSummary]).fire();
                });
            });
        }

        private isInvalidContent(item: ContentPublishItem): boolean {
            return !item.isValid() || !item.getDisplayName() || item.getName().isUnnamed();
        }
        private extendsWindowHeightSize(): boolean {
            if (this.getResponsiveItem().isInRangeOrBigger(api.ui.responsive.ResponsiveRanges._540_720)) {
                var el = this.getEl(),
                    bottomPosition: number = (el.getTopPx() || parseFloat(el.getComputedProperty('top')) || 0) +
                                             el.getMarginTop() +
                                             el.getHeightWithBorder() +
                                             el.getMarginBottom();

                if (window.innerHeight < bottomPosition) {
                    return true;
                }
            }
            return false;
        }

        /**
         * Inits arrays of properties that store results of performing resolve request.
         */
        private initResolvedPublishItems(json: ResolvePublishContentResultJson) {

            if (this.includeChildItemsCheck.isChecked()) {
                this.initialContentsResolvedWithChildren.setContentsResolved(ContentPublishItem.fromNewContentPublishItems(json.requestedContents));
            } else {
                this.initialContentsResolvedWithoutChildren.setContentsResolved(ContentPublishItem.fromNewContentPublishItems(json.requestedContents));
            }
        }

        /**
         * Inits arrays of properties that store results of performing resolve request.
         */
        private initResolvedDependenciesItems(json: ResolvePublishContentResultJson) {

            if (this.includeChildItemsCheck.isChecked()) {
                this.dependenciesContentsResolvedWithChildren.setContentsResolved(ContentPublishItem.fromNewContentPublishItems(json.dependentContents));
            } else {
                this.dependenciesContentsResolvedWithoutChildren.setContentsResolved(ContentPublishItem.fromNewContentPublishItems(json.dependentContents));
            }
        }

        private doPublish() {

            this.showLoadingSpinnerAtButton();
            this.publishButton.setEnabled(false);

            var selectedIds = this.getSelectedContentsIds();
            new PublishContentRequest().
                setIncludeChildren(this.includeChildItemsCheck.isChecked()).
                setIds(selectedIds).send().done((jsonResponse: api.rest.JsonResponse<api.content.PublishContentResult>) => {
                    this.close();
                    PublishContentRequest.feedback(jsonResponse);
                    new api.content.event.ContentsPublishedEvent(selectedIds).fire();
                });
        }

        private countItemsToPublishAndUpdateCounterElements() {

            var totalCountToPublish = this.getTotalCountToPublish();

            //subheader
            this.updateSubheaderMessage(totalCountToPublish);

            // publish button
            this.updatePublishButton(totalCountToPublish);
        }

        private getTotalCountToPublish(): number {
            var result = 0;

            result += this.getCountToPublish(this.includeChildItemsCheck.isChecked()
                ? this.initialContentsResolvedWithChildren
                : this.initialContentsResolvedWithoutChildren);

            result += this.getCountToPublish(this.includeChildItemsCheck.isChecked()
                ? this.dependenciesContentsResolvedWithChildren
                : this.dependenciesContentsResolvedWithoutChildren);

            return result;
        }

        private getCountToPublish(contentsResolved: ContentsResolved<ContentPublishItem>): number {
            var result = 0;
            contentsResolved.getContentsResolved().forEach((contentPublishItem: ContentPublishItem) => {
                if (contentPublishItem.getCompareStatus() != api.content.CompareStatus.EQUAL) {
                    result++;
                }
            });
            return result;
        }

        private updateSubheaderMessage(count: number) {
            var allValid = this.allResolvedItemsAreValid();
            this.subheaderMessage.setHtml(count == 0 ? "No items to publish" :
                                          allValid ? "Your changes are ready for publishing" : "Invalid content(s) prevent publish");
            this.subheaderMessage.toggleClass("invalid", !allValid);
        }

        private updatePublishButton(count: number) {

            this.cleanPublishButtonText();

            this.publishButton.setLabel(count > 0 ? "Publish (" + count + ")" : "Publish");
            let canPublish = count > 0 && this.allResolvedItemsAreValid();
            this.publishButton.setEnabled(canPublish);
            if (canPublish) {
                this.getButtonRow().focusDefaultAction();
                this.updateTabbable();
            }
        }

        private contentItemsAreValid(contentPublishItems: ContentsResolved<ContentPublishItem>): boolean {

            return contentPublishItems.getContentsResolved().every((content: ContentPublishItem) => {
                return content.getCompareStatus() == CompareStatus.PENDING_DELETE ||
                       (content.isValid() && !api.util.StringHelper.isBlank(content.getDisplayName()) && !content.getName().isUnnamed());
            });

        }

        private atLeastOneInitialItemHasChild(): boolean {
            return this.selectedContents.some((obj: ContentSummaryAndCompareStatus) => {
                return obj.hasChildren();
            });
        }

        private allResolvedItemsAreValid(): boolean {
            var includeChildItems = this.includeChildItemsCheck.isChecked();

            var initialValid = this.contentItemsAreValid(includeChildItems
                ? this.initialContentsResolvedWithChildren
                : this.initialContentsResolvedWithoutChildren);

            var dependenciesValid = this.contentItemsAreValid(includeChildItems
                ? this.dependenciesContentsResolvedWithChildren
                : this.dependenciesContentsResolvedWithoutChildren);

            return initialValid && dependenciesValid;
        }

        private showLoadingSpinnerAtButton() {
            this.publishButton.addClass("spinner");
        }

        private hideLoadingSpinnerAtButton() {
            this.publishButton.removeClass("spinner");
        }

        private cleanPublishButtonText() {
            this.publishButton.setLabel("Publish");
        }
    }

    export class PublishDialogItemList extends api.dom.DivEl {
        constructor() {
            super();
            this.getEl().addClass("item-list");
            this.getEl().addClass("initial-items");
        }

        clear() {
            this.removeChildren();
        }
    }

    export class PublishDialogDependantsItemList extends api.dom.DivEl {

        private selectionDependenciesItems: SelectionPublishItem<ContentPublishItem>[] = [];

        constructor() {
            super();
            this.getEl().addClass("item-list");
            this.getEl().addClass("dependant-items");
        }

        appendDependency(dependencyView: SelectionPublishItem<ContentPublishItem>) {
            this.appendChild(dependencyView);
            this.selectionDependenciesItems.push(dependencyView);
        }

        clear() {
            this.selectionDependenciesItems = [];
            this.removeChildren();
        }
    }

    export class ContentPublishDialogAction extends api.ui.Action {
        constructor() {
            super("Publish");
            this.setIconClass("publish-action");
        }
    }

    class ContentsResolved<M extends ContentPublishItem> {

        private alreadyResolved: boolean = false;

        private contentsResolved: M[] = [];

        isAlreadyResolved(): boolean {
            return this.alreadyResolved;
        }

        setAlreadyResolved(value: boolean) {
            this.alreadyResolved = value;
        }

        setContentsResolved(contentsResolvedWithChildren: M[]) {
            this.contentsResolved = contentsResolvedWithChildren;
            this.alreadyResolved = true;
        }

        getContentsResolved(): M[] {
            return this.contentsResolved;
        }

        removeItemWithId(id: String) {
            if (this.alreadyResolved) {
                for (var i = 0; i < this.contentsResolved.length; i++) {
                    if (id == this.contentsResolved[i].getId()) {
                        this.contentsResolved.splice(i, 1);
                        break;
                    }
                }
            }
        }
    }
}
