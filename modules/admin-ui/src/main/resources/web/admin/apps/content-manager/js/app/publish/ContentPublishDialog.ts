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

        private subheaderMessage = new api.dom.H6El();

        private selectedContents: ContentSummary[];

        private initialContentsResolvedWithChildren: ContentsResolved<ContentPublishRequestedItem> = new ContentsResolved<ContentPublishRequestedItem>();

        private initialContentsResolvedWithoutChildren: ContentsResolved<ContentPublishRequestedItem> = new ContentsResolved<ContentPublishRequestedItem>();

        private dependentContentsResolvedWithChildren: ContentsResolved<ContentPublishItem> = new ContentsResolved<ContentPublishItem>();

        private dependentContentsResolvedWithoutChildren: ContentsResolved<ContentPublishItem> = new ContentsResolved<ContentPublishItem>();

        private childrenContentsResolvedWithChildren: ContentsResolved<ContentPublishItem> = new ContentsResolved<ContentPublishItem>();

        private childrenContentsResolvedWithoutChildren: ContentsResolved<ContentPublishItem> = new ContentsResolved<ContentPublishItem>();

        private dependenciesLabel: api.dom.LabelEl;

        private includeChildrenCheckedListener: () => void;

        constructor() {
            super({
                title: new api.ui.dialog.ModalDialogHeader("Publishing Wizard")
            });

            this.modelName = "item";

            this.initDependenciesLabel();
            this.initIncludeChildrenCheckbox();

            this.getEl().addClass("publish-dialog");

            this.appendChildToContentPanel(this.initialItemsView);
            this.appendChildToContentPanel(this.dependenciesItemsView);
            this.renderDependenciesLabel();

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
            this.resolvePublishRequestedContentsAndUpdateView().then(() => {
                if ((this.getChildrenEligibleForPublishCount() + this.getDependantsEligibleForPublishCount()) > 0) {
                    this.dependenciesLabel.setVisible(true); // enable toggle only if there are dependencies available
                } else {
                    // item list won't change if there are no children, so don't trigger anything
                    this.includeChildItemsCheck.unValueChanged(this.includeChildrenCheckedListener);
                }
                this.centerMyself();
            }).done();
            this.open();
        }

        renderDependenciesLabel() {
            this.initialItemsView.appendChild(this.dependenciesLabel);
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

            this.renderDependenciesLabel()
        }

        private initIncludeChildrenCheckbox() {

            this.includeChildrenCheckedListener = () => {
                if ((this.includeChildItemsCheck.isChecked() && !this.initialContentsResolvedWithChildren.isAlreadyResolved()) ||
                    (!this.includeChildItemsCheck.isChecked() && !this.initialContentsResolvedWithoutChildren.isAlreadyResolved())) {
                    this.resolvePublishRequestedContentsAndUpdateView();
                } else {
                    this.renderResolvedPublishItems();
                    this.countItemsToPublishAndUpdateCounterElements();
                }
                this.resolveAllDependenciesIfNeededAndUpdateView();
            };

            this.includeChildItemsCheck = new api.ui.Checkbox();
            this.includeChildItemsCheck.setChecked(true);
            this.includeChildItemsCheck.addClass('include-child-check');
            this.includeChildItemsCheck.onValueChanged(this.includeChildrenCheckedListener);
        }

        private initDependenciesLabel() {

            this.dependenciesLabel = new api.dom.LabelEl("");
            this.dependenciesLabel.addClass("dependencies-toggle-label");
            this.dependenciesLabel.setVisible(false);

            this.dependenciesLabel.onClicked((event) => {
                this.dependenciesLabel.toggleClass("expanded");
                this.resolveAllDependenciesIfNeededAndUpdateView();
                this.updateDependenciesLabel();
            });
        }

        private updateDependenciesLabel() {
            if (this.dependenciesExpanded()) {
                this.dependenciesLabel.setValue("Hide dependents and child items");
            } else {
                this.dependenciesLabel.setValue("Show dependents and child items");
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
                        if (this.selectionItems.length == 1) { // this is the last item in the dialog
                            this.close();
                            return;
                        }
                        this.removeFromInitialSelection(item);
                        this.initialContentsResolvedWithChildren.setAlreadyResolved(false);
                        this.initialContentsResolvedWithoutChildren.setAlreadyResolved(false);
                        this.dependentContentsResolvedWithChildren.setAlreadyResolved(false);
                        this.dependentContentsResolvedWithoutChildren.setAlreadyResolved(false);
                        this.childrenContentsResolvedWithChildren.setAlreadyResolved(false);
                        this.childrenContentsResolvedWithoutChildren.setAlreadyResolved(false);
                        this.resolvePublishRequestedContentsAndUpdateView();
                        this.resolveAllDependenciesIfNeededAndUpdateView();
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
        private resolvePublishRequestedContentsAndUpdateView(): wemQ.Promise<void> {

            this.showLoadingSpinner();

            var resolvePublishRequestedContentsRequest = new api.content.ResolvePublishRequestedContentsRequest(this.selectedContents.map((el) => {
                return new api.content.ContentId(el.getId());
            }), this.includeChildItemsCheck.isChecked());

            return resolvePublishRequestedContentsRequest.send().then((jsonResponse: api.rest.JsonResponse<ResolvePublishDependenciesResultJson>) => {
                this.initResolvedPublishItems(jsonResponse.getResult());
                this.renderResolvedPublishItems();
                this.countItemsToPublishAndUpdateCounterElements();
            }).finally(() => {
                this.hideLoadingSpinner();
            });
        }

        /**
         * Perform request to resolve dependency items of passed item.
         */
        private resolveAllDependenciesIfNeededAndUpdateView() {

            if ((this.includeChildItemsCheck.isChecked() && !this.dependentContentsResolvedWithChildren.isAlreadyResolved()) ||
                (!this.includeChildItemsCheck.isChecked() && !this.dependentContentsResolvedWithoutChildren.isAlreadyResolved())) {

                var resolveDependenciesRequest = new api.content.ResolvePublishDependenciesRequest(this.selectedContents.map((el) => {
                    return new api.content.ContentId(el.getId());
                }), this.includeChildItemsCheck.isChecked());

                resolveDependenciesRequest.send().then((jsonResponse: api.rest.JsonResponse<GetDependantsResultJson>) => {
                    this.initResolvedDependenciesItems(jsonResponse.getResult());
                    this.renderResolvedDependenciesItems();
                }).done();
            } else {
                this.renderResolvedDependenciesItems();
            }
        }

        private renderResolvedDependenciesItems() {

            this.dependenciesItemsView.clear();

            if (this.dependenciesExpanded()) {
                var dependentItems: ContentsResolved<ContentPublishItem> = this.dependentContentsResolvedWithoutChildren,
                    childrenItems: ContentsResolved<ContentPublishItem> = this.childrenContentsResolvedWithoutChildren;

                if (this.includeChildItemsCheck.isChecked()) {
                    dependentItems = this.dependentContentsResolvedWithChildren;
                    childrenItems = this.childrenContentsResolvedWithChildren
                }

                // append dependents to view
                dependentItems.getContentsResolved().forEach((dependency: ContentPublishItem)  => {
                    var dependencyView: SelectionPublishItem<ContentPublishItem> = new SelectionPublishItemBuilder<ContentPublishItem>().create().
                        setViewer(new app.publish.ResolvedDependantContentViewer<ContentPublishItem>()).
                        setContent(dependency).
                        setIsCheckBoxEnabled(false).
                        setIsCheckboxHidden(true).
                        setShowStatus(false).
                        build();

                    this.dependenciesItemsView.appendDependency(dependencyView);
                });

                // append children to view
                childrenItems.getContentsResolved().forEach((dependency: ContentPublishItem)  => {
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
                this.dependentContentsResolvedWithChildren.setContentsResolved(ContentPublishItem.getResolvedContents(json.dependantContents));
                this.childrenContentsResolvedWithChildren.setContentsResolved(ContentPublishItem.getResolvedContents(json.childrenContents));
            } else {
                this.dependentContentsResolvedWithoutChildren.setContentsResolved(ContentPublishItem.getResolvedContents(json.dependantContents));
                this.childrenContentsResolvedWithoutChildren.setContentsResolved(ContentPublishItem.getResolvedContents(json.childrenContents));
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

            var checkedRequested = this.getSelectedItemsEligibleForPublishCount(),
                dependantsEligibleForPublish = this.getDependantsEligibleForPublishCount(),
                childrenEligibleForPublish = this.getChildrenEligibleForPublishCount();

            //subheader
            this.subheaderMessage.setHtml("Your changes are ready for publishing");

            // publish button
            this.cleanPublishButtonText();
            this.updatePublishButton(checkedRequested + dependantsEligibleForPublish + childrenEligibleForPublish);

            // dependencies label
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

        private getSelectedItemsCount(): number {
            return this.selectionItems.length;
        }

        private getSelectedItemsEligibleForPublishCount(): number {
            var result = 0;
            this.selectionItems.forEach((item: SelectionPublishItem<ContentPublishRequestedItem>)  => {
                if (item.getBrowseItem().getModel().getCompareStatus() != api.content.CompareStatus.EQUAL) {
                    result++;
                }
            });
            return result;
        }

        private updatePublishButton(count: number) {
            this.publishButton.setLabel("Publish Now (" + count + ")");
            if (count > 0) {
                this.publishButton.setEnabled(true);
            } else {
                this.publishButton.setEnabled(false);
            }
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