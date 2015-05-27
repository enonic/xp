module app.publish {

    import ContentIconUrlResolver = api.content.ContentIconUrlResolver;
    import BrowseItem = api.app.browse.BrowseItem;
    import ContentPath = api.content.ContentPath;
    import ContentSummary = api.content.ContentSummary;
    import DialogButton = api.ui.dialog.DialogButton;
    import PublishContentRequest = api.content.PublishContentRequest;
    import ResolvePublishDependenciesResultJson = api.content.json.ResolvePublishDependenciesResultJson
    import GetDependantsResultJson = api.content.json.ResolveDependantsResultJson;
    import CompareStatus = api.content.CompareStatus;

    /**
     * ContentPublishDialog manages list of initially checked (initially requested) items resolved via ResolvePublishDependencies command.
     * Resolved items are converted into array of SelectionPublishItem<ContentPublishRequestedItem> items and stored in selectionItems property.
     * ContentPublishRequestedItem contains info for the initially checked item with number of children and dependants items that will get published with it.
     * Dependant items number will change depending on includeChildren checkbox state as
     * resolved dependencies usually differ in that case.
     * Resolved dependant items are stored within their parents ContentPublishRequestedItem object.
     */
    export class ContentPublishDialog extends api.ui.dialog.ModalDialog {

        private modelName: string;

        private selectionItems: SelectionPublishItem<ContentPublishRequestedItem>[] = [];

        private publishButton: DialogButton;

        private publishAction: api.ui.Action;

        private initialItemsView: PublishDialogItemList = new PublishDialogItemList();

        private dependantItemsView: PublishDialogDependantsItemList = new PublishDialogDependantsItemList();

        private includeChildItemsCheck: api.ui.Checkbox;

        private subheaderMessage = new api.dom.H6El();

        private selectedContents: ContentSummary[];

        private initialContentsResolvedWithChildren: ContentsResolved<ContentPublishRequestedItem> = new ContentsResolved<ContentPublishRequestedItem>();

        private initialContentsResolvedWithoutChildren: ContentsResolved<ContentPublishRequestedItem> = new ContentsResolved<ContentPublishRequestedItem>();

        private dependantContentsResolvedWithChildren: ContentsResolved<ContentPublishDependantItem> = new ContentsResolved<ContentPublishDependantItem>();

        private dependantContentsResolvedWithoutChildren: ContentsResolved<ContentPublishDependantItem> = new ContentsResolved<ContentPublishDependantItem>();

        private dependenciesLabel: api.dom.LabelEl;

        constructor() {
            super({
                title: new api.ui.dialog.ModalDialogHeader("Publishing Wizard")
            });

            this.modelName = "item";
            this.initDependenciesLabel();
            this.initIncludeChildrenCheckbox();

            this.getEl().addClass("publish-dialog");
            this.appendChildToContentPanel(this.initialItemsView);
            this.appendChildToContentPanel(this.dependenciesLabel);
            this.appendChildToContentPanel(this.dependantItemsView);

            this.subheaderMessage.addClass("publish-dialog-subheader");
            this.appendChildToTitle(this.subheaderMessage);

            this.publishButton = this.setPublishAction(new ContentPublishDialogAction());

            this.getPublishAction().onExecuted(() => {
                this.doPublish();
            });

            this.addCancelButtonToBottom();

            this.appendChildToContentPanel(this.includeChildItemsCheck);
        }

        initAndOpen() {
            this.resolvePublishRequestedContentsAndUpdateView();
            this.open();
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

        // inits selection items and appends them to display view
        renderResolvedPublishItems() {
            this.initSelectionItems();
            this.initialItemsView.clear();

            this.selectionItems.forEach((item: SelectionPublishItem<ContentPublishRequestedItem>)  => {
                if (!item.isHidden()) {
                    this.initialItemsView.appendChild(item);
                }
            });
            this.centerMyself();
        }

        private initIncludeChildrenCheckbox() {

            this.includeChildItemsCheck = new api.ui.Checkbox();
            this.includeChildItemsCheck.setChecked(true);
            this.includeChildItemsCheck.addClass('include-child-check');
            this.includeChildItemsCheck.onValueChanged(() => {
                if ((this.includeChildItemsCheck.isChecked() && !this.initialContentsResolvedWithChildren.isAlreadyResolved()) ||
                    (!this.includeChildItemsCheck.isChecked() && !this.initialContentsResolvedWithoutChildren.isAlreadyResolved())) {
                    this.resolvePublishRequestedContentsAndUpdateView();
                } else {
                    this.renderResolvedPublishItems();
                    this.countItemsToPublishAndUpdateCounterElements();
                }
                this.resolveAllDependantsIfNeededAndUpdateView();
            });
        }

        private initDependenciesLabel() {

            this.dependenciesLabel = new api.dom.LabelEl("");
            this.dependenciesLabel.addClass("dependencies-toggle-label");

            this.dependenciesLabel.onClicked((event) => {
                this.dependenciesLabel.toggleClass("expanded");
                this.resolveAllDependantsIfNeededAndUpdateView();
                this.updateDependenciesLabel();
            });
        }

        private updateDependenciesLabel() {
            if (this.dependenciesExpanded()) {
                this.dependenciesLabel.setValue("Hide dependencies (" + this.getDependantsEligibleForPublishCount() + ")");
            } else {
                this.dependenciesLabel.setValue("Show dependencies (" + this.getDependantsEligibleForPublishCount() + ")");
            }
        }

        private dependenciesExpanded(): boolean {
            return this.dependenciesLabel.hasClass("expanded");
        }

        /**
         * Inits selection items from resolved items returned via resolve request.
         * Method is called from renderResolvedPublishItems() so it also saves and restores unchecked items.
         */
        private initSelectionItems() {

            this.selectionItems = [];

            var pushRequestedItems: ContentPublishRequestedItem[];

            if (this.includeChildItemsCheck.isChecked()) {
                pushRequestedItems = this.initialContentsResolvedWithChildren.getContentsResolved();
            } else {
                pushRequestedItems = this.initialContentsResolvedWithoutChildren.getContentsResolved();
            }

            pushRequestedItems.forEach((content: ContentPublishRequestedItem) => {
                var item: SelectionPublishItem<ContentPublishRequestedItem> = new SelectionPublishItemBuilder<ContentPublishRequestedItem>().create().
                    setViewer(new app.publish.ResolvedPublishContentViewer<ContentPublishRequestedItem>()).
                    setContent(content).
                    setIsCheckBoxEnabled(false).
                    setRemovable(true).
                    setRemoveCallback(() => {
                        this.removeSelectionItem(item);
                        this.removeFromInitialSelection(item);
                        this.dependantItemsView.removeDependantItems(item);
                        this.initialContentsResolvedWithChildren.removeItemWithId(item.getBrowseItem().getId());
                        this.initialContentsResolvedWithoutChildren.removeItemWithId(item.getBrowseItem().getId());
                        this.dependantContentsResolvedWithChildren.removeItemsThatDependOnId(item.getBrowseItem().getId());
                        this.dependantContentsResolvedWithoutChildren.removeItemsThatDependOnId(item.getBrowseItem().getId());
                        this.countItemsToPublishAndUpdateCounterElements();
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

        private getSelectedContentsIds(checked: boolean): string[] {

            var checkedContentsIds: string[] = [];
            this.selectionItems.forEach((item: SelectionPublishItem<ContentPublishRequestedItem>)  => {
                checkedContentsIds.push(item.getBrowseItem().getId());
            });
            return checkedContentsIds;
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
        private resolvePublishRequestedContentsAndUpdateView() {

            this.showLoadingSpinner();

            var resolvePublishDependenciesRequest = new api.content.ResolvePublishDependenciesRequest(this.selectedContents.map((el) => {
                return new api.content.ContentId(el.getId());
            }), this.includeChildItemsCheck.isChecked());

            resolvePublishDependenciesRequest.send().then((jsonResponse: api.rest.JsonResponse<ResolvePublishDependenciesResultJson>) => {
                this.initResolvedPublishItems(jsonResponse.getResult());
                this.renderResolvedPublishItems();
                this.countItemsToPublishAndUpdateCounterElements();
            }).finally(() => {
                this.hideLoadingSpinner();
            }).done();
        }

        /**
         * Perform request to resolve dependant items of passed item.
         */
        private resolveAllDependantsIfNeededAndUpdateView() {

            if ((this.includeChildItemsCheck.isChecked() && !this.dependantContentsResolvedWithChildren.isAlreadyResolved()) ||
                (!this.includeChildItemsCheck.isChecked() && !this.dependantContentsResolvedWithoutChildren.isAlreadyResolved())) {

                var getDependantsRequest = new api.content.ResolveDependantsRequest(this.selectedContents.map((el) => {
                    return new api.content.ContentId(el.getId());
                }), this.includeChildItemsCheck.isChecked());

                getDependantsRequest.send().then((jsonResponse: api.rest.JsonResponse<GetDependantsResultJson>) => {
                    this.initResolvedDependantItems(jsonResponse.getResult());
                    this.renderResolvedDependantItems();
                }).done();

                this.centerMyself();
            } else {
                this.renderResolvedDependantItems();
            }
        }

        private renderResolvedDependantItems() {

            this.dependantItemsView.clear();

            if (this.dependenciesExpanded()) {
                var dependantItems: ContentsResolved<ContentPublishDependantItem> = this.dependantContentsResolvedWithoutChildren;
                if (this.includeChildItemsCheck.isChecked()) {
                    dependantItems = this.dependantContentsResolvedWithChildren;
                }

                dependantItems.getContentsResolved().forEach((dependant: ContentPublishDependantItem)  => {
                    var dependantView: SelectionPublishItem<ContentPublishDependantItem> = new SelectionPublishItemBuilder<ContentPublishDependantItem>().create().
                        setViewer(new app.publish.ResolvedDependantContentViewer<ContentPublishDependantItem>()).
                        setContent(dependant).
                        setIsCheckBoxEnabled(false).
                        setIsCheckboxHidden(true).
                        setShowStatus(false).
                        build();

                    this.dependantItemsView.appendDependant(dependantView);
                });
            }
            this.centerMyself();
        }

        /**
         * Perform request to resolve dependants of passed item and update view.
         * Use it when you have toggle enabled per each initially selected item.
         */
        private resolveDependantsOfSingleItemAndUpdateView(selectionItem: SelectionPublishItem<ContentPublishRequestedItem>,
                                                           showDependants: boolean) {

            var contentPublishRequestedItem: ContentPublishRequestedItem = <ContentPublishRequestedItem>selectionItem.getBrowseItem().getModel();
            if (showDependants) {
                if (!contentPublishRequestedItem.isDependantsResolved()) {
                    var getDependantsRequest = new api.content.ResolveDependantsRequest([new api.content.ContentId(contentPublishRequestedItem.getId())],
                        this.includeChildItemsCheck.isChecked());
                    getDependantsRequest.send().then((jsonResponse: api.rest.JsonResponse<GetDependantsResultJson>) => {
                        contentPublishRequestedItem.setDependantsResolved(ContentPublishDependantItem.getDependantsResolved(jsonResponse.getResult()));
                        this.initExpandableDependantsView(selectionItem);
                        selectionItem.showDependants();
                    }).done();
                } else {
                    if (!selectionItem.hasDependants()) {
                        this.initExpandableDependantsView(selectionItem);
                    }
                    selectionItem.showDependants();
                }
            } else {
                selectionItem.hideDependants();
            }
            this.centerMyself();
        }

        /**
         * Creates SelectionPublishItem from resolved dependants and appends them as children to SelectionPublishItem of initial object.
         * @param selectionItem
         */
        private initExpandableDependantsView(selectionItem: SelectionPublishItem<ContentPublishRequestedItem>): void {

            var contentPublishRequestedItem: ContentPublishRequestedItem = <ContentPublishRequestedItem>selectionItem.getBrowseItem().getModel();

            contentPublishRequestedItem.getDependantsResolved().forEach((dependant: ContentPublishDependantItem)  => {
                var dependantView: SelectionPublishItem<ContentPublishDependantItem> = new SelectionPublishItemBuilder<ContentPublishDependantItem>().create().
                    setContent(dependant).
                    setIsCheckBoxEnabled(false).
                    setIsHidden(false).
                    setIsChecked(true).
                    setExpandable(false).
                    setChangeCallback(() => {
                    }).
                    setToggleCallback(() => {
                    }).
                    build();

                selectionItem.appendDependant(dependantView);
            });
        }

        /**
         * Inits arrays of properties that store results of performing resolve request.
         */
        private initResolvedPublishItems(json: ResolvePublishDependenciesResultJson) {

            if (this.includeChildItemsCheck.isChecked()) {
                this.initialContentsResolvedWithChildren.setContentsResolved(ContentPublishRequestedItem.getPushRequestedContents(json));
            } else {
                this.initialContentsResolvedWithoutChildren.setContentsResolved(ContentPublishRequestedItem.getPushRequestedContents(json));
            }
        }

        /**
         * Inits arrays of properties that store results of performing resolve request.
         */
        private initResolvedDependantItems(json: GetDependantsResultJson) {

            if (this.includeChildItemsCheck.isChecked()) {
                this.dependantContentsResolvedWithChildren.setContentsResolved(ContentPublishDependantItem.getDependantsResolved(json));
            } else {
                this.dependantContentsResolvedWithoutChildren.setContentsResolved(ContentPublishDependantItem.getDependantsResolved(json));
            }
        }

        private doPublish() {

            new PublishContentRequest().
                setIncludeChildren(this.includeChildItemsCheck.isChecked()).
                setIds(this.getSelectedContentsIds(true).map((id) => {
                    return new api.content.ContentId(id);
                })).send().done((jsonResponse: api.rest.JsonResponse<api.content.PublishContentResult>) => {
                    this.close();
                    PublishContentRequest.feedback(jsonResponse);
                });
        }

        private countItemsToPublishAndUpdateCounterElements() {

            var checkedRequested = this.getSelectedItemsCount(),
                dependantsEligibleForPublish = this.getDependantsEligibleForPublishCount(),
                childrenEligibleForPublish = this.getChildrenEligibleForPublishCount();

            //subheader
            this.subheaderMessage.setHtml("Your changes are ready for publishing");

            // publish button
            this.cleanPublishButtonText();
            this.updatePublishButtonCounter(checkedRequested + dependantsEligibleForPublish + childrenEligibleForPublish);

            // dependants label
            this.updateDependenciesLabel();

            // includeChildren link
            this.includeChildItemsCheck.setLabel('Include child items');
        }

        // counts number of children that can be published with given selections upon pressing publish button
        private getChildrenEligibleForPublishCount(): number {
            var result = 0;
            this.selectionItems.forEach((item: SelectionPublishItem<ContentPublishRequestedItem>)  => {
                result += item.getBrowseItem().getModel().getChildrenCount();
            });
            return result;
        }

        // counts number of dependants that can be published with given selections upon pressing publish button
        private getDependantsEligibleForPublishCount(): number {
            var result = 0;
            this.selectionItems.forEach((item: SelectionPublishItem<ContentPublishRequestedItem>)  => {
                result += item.getBrowseItem().getModel().getDependantsCount();
            });
            return result;
        }

        private getCheckedItemsCount(): number {
            var result = 0;
            this.selectionItems.forEach((item: SelectionPublishItem<ContentPublishRequestedItem>)  => {
                if (item.isChecked()) {
                    result++;
                }
            });
            return result;
        }

        private getSelectedItemsCount(): number {
            return this.selectionItems.length;
        }

        private updatePublishButtonCounter(count: number) {
            this.publishButton.setLabel("Publish Now (" + count + ")");
        }

        private showLoadingSpinner() {
            this.publishButton.addClass("spinner");
        }

        private hideLoadingSpinner() {
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

        private selectionDependantItems: SelectionPublishItem<ContentPublishDependantItem>[] = [];

        constructor() {
            super();
            this.getEl().addClass("item-list");
            this.getEl().addClass("dependant-items");
        }

        appendDependant(dependantView: SelectionPublishItem<ContentPublishDependantItem>) {
            this.appendChild(dependantView);
            this.selectionDependantItems.push(dependantView);
        }

        getDependantsItemList(): SelectionPublishItem<ContentPublishDependantItem>[] {
            return this.selectionDependantItems;
        }

        removeDependantItems(item: SelectionPublishItem<ContentPublishRequestedItem>) {
            for (var i = 0; i < this.selectionDependantItems.length; i++) {
                if (item.getBrowseItem().getId() == this.selectionDependantItems[i].getBrowseItem().getModel().getDependsOnContentId()) {
                    this.selectionDependantItems[i].remove();
                    this.selectionDependantItems.splice(i, 1);
                    i = -1;
                }
            }
        }

        clear() {
            this.selectionDependantItems = [];
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

        removeItemsThatDependOnId(id: String) {
            if (this.alreadyResolved) {
                for (var i = 0; i < this.contentsResolved.length; i++) {
                    if (id == (<ContentPublishDependantItem>this.contentsResolved[i]).getDependsOnContentId()) {
                        this.contentsResolved.splice(i, 1);
                        i = -1;
                    }
                }
            }
        }
    }
}