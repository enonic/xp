module app.publish {

    import ContentIconUrlResolver = api.content.ContentIconUrlResolver;
    import BrowseItem = api.app.browse.BrowseItem;
    import ContentPath = api.content.ContentPath;
    import ContentSummary = api.content.ContentSummary;
    import DialogButton = api.ui.dialog.DialogButton;
    import PublishContentRequest = api.content.PublishContentRequest;
    import ResolvePublishDependenciesResultJson = api.content.json.ResolvePublishRequestedContentsResultJson
    import GetDependantsResultJson = api.content.json.ResolvePublishDependenciesResultJson;
    import CompareStatus = api.content.CompareStatus;
    import ContentId = api.content.ContentId;

    /**
     * ContentPublishDialog manages list of initially checked (initially requested) items resolved via ResolvePublishDependencies command.
     * Resolved items are converted into array of SelectionPublishItem<ContentPublishRequestedItem> items and stored in selectionItems property.
     * ContentPublishRequestedItem contains info for the initially checked item with number of children and dependants items that will get published with it.
     * Dependant items number will change depending on includeChildren checkbox state as
     * resolved dependencies usually differ in that case.
     */
    export class ContentPublishDialog extends api.ui.dialog.ModalDialog {

        private modelName: string;

        private selectionItems: SelectionPublishItem<ContentPublishRequestedItem>[] = [];

        private publishButton: DialogButton;

        private publishAction: api.ui.Action;

        private initialItemsView: PublishDialogItemList = new PublishDialogItemList();

        private dependenciesItemsView: PublishDialogDependantsItemList = new PublishDialogDependantsItemList();

        private includeChildItemsCheck: api.ui.Checkbox;

        private subheaderMessage: api.dom.H6El = new api.dom.H6El("publish-dialog-subheader");

        private selectedContents: ContentSummary[];

        private initialContentsResolvedWithChildren: ContentsResolved<ContentPublishRequestedItem> = new ContentsResolved<ContentPublishRequestedItem>();

        private initialContentsResolvedWithoutChildren: ContentsResolved<ContentPublishRequestedItem> = new ContentsResolved<ContentPublishRequestedItem>();

        private dependenciesContentsResolvedWithChildren: ContentsResolved<ContentPublishDependencyItem> = new ContentsResolved<ContentPublishDependencyItem>();

        private dependenciesContentsResolvedWithoutChildren: ContentsResolved<ContentPublishDependencyItem> = new ContentsResolved<ContentPublishDependencyItem>();

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

            this.runResolveTasks(this.getResolveTasks(), () => {
                this.centerMyself();
            })
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
            return [this.resolvePublishRequestedContentsAndUpdateView(), this.resolveAllDependenciesIfNeededAndUpdateView()];
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

        setSelectedContents(contents: ContentSummary[]) {
            this.selectedContents = contents;
        }

        private renderSelectedContentsWhileItemsGettingResolved() {

            var initiallySelectedContents: ContentPublishItem[] = ContentPublishItem.buildPublishItemsFromContentSummaries(
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

        private sortContentSummariesArrayByPath(arrayToSort: ContentSummary[]): ContentSummary[] {
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

            this.selectionItems.forEach((item: SelectionPublishItem<ContentPublishRequestedItem>)  => {
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
                if ((this.includeChildItemsCheck.isChecked() && !this.initialContentsResolvedWithChildren.isAlreadyResolved()) ||
                    (!this.includeChildItemsCheck.isChecked() && !this.initialContentsResolvedWithoutChildren.isAlreadyResolved())) {
                    resolveTasks.push(this.resolvePublishRequestedContentsAndUpdateView());
                } else {
                    this.renderResolvedPublishItems();
                    this.countItemsToPublishAndUpdateCounterElements();
                }
                resolveTasks.push(this.resolveAllDependenciesIfNeededAndUpdateView());
                this.runResolveTasks(resolveTasks);
            };

            this.includeChildItemsCheck = new api.ui.Checkbox();
            this.includeChildItemsCheck.setChecked(false);
            this.includeChildItemsCheck.addClass('include-child-check');
            this.includeChildItemsCheck.onValueChanged(this.includeChildrenCheckedListener);
            this.includeChildItemsCheck.setLabel('Include child items');
        }

        /**
         * Inits selection items from resolved items returned via resolve request.
         * Method is called from renderResolvedPublishItems() so it also saves and restores unchecked items.
         */
        private initSelectionItems() {

            this.selectionItems = [];

            var pushRequestedItems: ContentPublishRequestedItem[] = this.includeChildItemsCheck.isChecked()
                ? this.initialContentsResolvedWithChildren.getContentsResolved()
                : this.initialContentsResolvedWithoutChildren.getContentsResolved();

            pushRequestedItems.forEach((content: ContentPublishRequestedItem) => {
                var item: SelectionPublishItem<ContentPublishRequestedItem> = new SelectionPublishItemBuilder<ContentPublishRequestedItem>().create().
                    setViewer(new app.publish.ResolvedPublishContentViewer<ContentPublishRequestedItem>()).
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
            });
        }

        private removeSelectionItem(item: SelectionPublishItem<ContentPublishRequestedItem>) {
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

        private indexOf(item: SelectionPublishItem<ContentPublishRequestedItem>): number {
            for (var i = 0; i < this.selectionItems.length; i++) {
                if (item.getBrowseItem().getId() == this.selectionItems[i].getBrowseItem().getId()) {
                    return i;
                }
            }
            return -1;
        }

        private removeFromInitialSelection(item: SelectionPublishItem<ContentPublishRequestedItem>) {
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
                                      publishRequestedSelectionItems: SelectionPublishItem<ContentPublishRequestedItem>[]) {
            publishRequestedSelectionItems.forEach((item: SelectionPublishItem<ContentPublishRequestedItem>)  => {
                if (unCheckedRequestedContentsIds.indexOf(item.getBrowseItem().getId()) > -1) {
                    item.setChecked(false);
                }
            });
        }

        /**
         * Perform request to resolve publish items, init and render results.
         */
        private resolvePublishRequestedContentsAndUpdateView(): wemQ.Promise<any> {

            var resolvePublishRequestedContentsRequest = new api.content.ResolvePublishRequestedContentsRequest(this.getSelectedContentsIds(),
                this.includeChildItemsCheck.isChecked());

            return resolvePublishRequestedContentsRequest.send().then((jsonResponse: api.rest.JsonResponse<ResolvePublishDependenciesResultJson>) => {
                this.initResolvedPublishItems(jsonResponse.getResult());
                this.renderResolvedPublishItems();
            });
        }

        /**
         * Perform request to resolve dependency items of passed item.
         */
        private resolveAllDependenciesIfNeededAndUpdateView(): wemQ.Promise<any> {

            if ((this.includeChildItemsCheck.isChecked() && !this.dependenciesContentsResolvedWithChildren.isAlreadyResolved()) ||
                (!this.includeChildItemsCheck.isChecked() && !this.dependenciesContentsResolvedWithoutChildren.isAlreadyResolved())) {

                var resolveDependenciesRequest = new api.content.ResolvePublishDependenciesRequest(this.getSelectedContentsIds(),
                    this.includeChildItemsCheck.isChecked());

                return resolveDependenciesRequest.send().then((jsonResponse: api.rest.JsonResponse<GetDependantsResultJson>) => {
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

            var dependenciesItems: ContentsResolved<ContentPublishDependencyItem> =
                this.includeChildItemsCheck.isChecked() ?
                this.dependenciesContentsResolvedWithChildren :
                this.dependenciesContentsResolvedWithoutChildren;

            if (dependenciesItems.getContentsResolved().length > 0) {
                var dependenciesHeader: api.dom.H6El = new api.dom.H6El("dependencies-header");
                dependenciesHeader.setHtml("Other items that will be published");
                this.dependenciesItemsView.appendChild(dependenciesHeader);
            }

            // append dependencies to view
            dependenciesItems.getContentsResolved().forEach((dependency: ContentPublishDependencyItem)  => {
                var dependencyView: SelectionPublishItem<ContentPublishItem> = new SelectionPublishItemBuilder<ContentPublishItem>().create().
                    setViewer(new app.publish.ResolvedDependantContentViewer<ContentPublishItem>()).
                    setContent(dependency).
                    setIsCheckBoxEnabled(false).
                    setIsCheckboxHidden(true).
                    setShowStatus(false).
                    build();

                this.dependenciesItemsView.appendDependency(dependencyView);
            });

        }

        /**
         * Inits arrays of properties that store results of performing resolve request.
         */
        private initResolvedPublishItems(json: ResolvePublishDependenciesResultJson) {

            if (this.includeChildItemsCheck.isChecked()) {
                this.initialContentsResolvedWithChildren.setContentsResolved(ContentPublishRequestedItem.getPushRequestedContents(json.pushRequestedContents));
            } else {
                this.initialContentsResolvedWithoutChildren.setContentsResolved(ContentPublishRequestedItem.getPushRequestedContents(json.pushRequestedContents));
            }
        }

        /**
         * Inits arrays of properties that store results of performing resolve request.
         */
        private initResolvedDependenciesItems(json: GetDependantsResultJson) {

            if (this.includeChildItemsCheck.isChecked()) {
                this.dependenciesContentsResolvedWithChildren.setContentsResolved(ContentPublishDependencyItem.getPushDependenciesContents(json.dependenciesContents));
            } else {
                this.dependenciesContentsResolvedWithoutChildren.setContentsResolved(ContentPublishDependencyItem.getPushDependenciesContents(json.dependenciesContents));
            }
        }

        private doPublish() {

            new PublishContentRequest().
                setIncludeChildren(this.includeChildItemsCheck.isChecked()).
                setIds(this.getSelectedContentsIds()).send().done((jsonResponse: api.rest.JsonResponse<api.content.PublishContentResult>) => {
                    this.close();
                    PublishContentRequest.feedback(jsonResponse);
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
            this.selectionItems.forEach((item: SelectionPublishItem<ContentPublishRequestedItem>)  => {
                var model: ContentPublishRequestedItem = item.getBrowseItem().getModel();
                result += model.getChildrenCount() + model.getDependantsCount();
                if (model.getCompareStatus() != api.content.CompareStatus.EQUAL) {
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

            this.publishButton.setLabel("Publish Now (" + count + ")");
            this.publishButton.setEnabled(count > 0 && this.allResolvedItemsAreValid());
        }

        private contentItemsAreValid(contentPublishItems: ContentsResolved<ContentPublishItem>): boolean {
            var result = true;

            contentPublishItems.getContentsResolved().forEach((content: ContentPublishRequestedItem) => {
                var contentName = content.getName(),
                    invalid = !content.isValid() || !content.getDisplayName() || contentName.isUnnamed();
                if (invalid) {
                    result = false;
                    return;
                }
            });

            return result;
        }

        private atLeastOneInitialItemHasChild(): boolean {
            return this.selectedContents.some((obj: ContentSummary) => {
                return obj.hasChildren();
            });
        }

        private allResolvedItemsAreValid(): boolean {
            var includeChildItems = this.includeChildItemsCheck.isChecked();

            if (!this.contentItemsAreValid(includeChildItems
                    ? this.initialContentsResolvedWithChildren
                    : this.initialContentsResolvedWithoutChildren)) {
                return false;
            }

            if (!this.contentItemsAreValid(includeChildItems
                    ? this.dependenciesContentsResolvedWithChildren
                    : this.dependenciesContentsResolvedWithoutChildren)) {
                return false;
            }

            return true;
        }

        private showLoadingSpinnerAtButton() {
            this.publishButton.addClass("spinner");
        }

        private hideLoadingSpinnerAtButton() {
            this.publishButton.removeClass("spinner");
        }

        private cleanPublishButtonText() {
            this.publishButton.setLabel("Publish Now");
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
            super("Publish", "enter");
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