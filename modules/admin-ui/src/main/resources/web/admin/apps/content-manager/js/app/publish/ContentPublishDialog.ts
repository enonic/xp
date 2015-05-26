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
     * SelectionPublishItem has checked/unchecked state which determines if item's id will be included into a list of contentIds of Publish request.
     * If dependant items are selected to be shown - their number will change depending on includeChildren checkbox state as
     * resolved dependencies usually differ in that case. Resolved dependant items are stored within their parents ContentPublishRequestedItem object.
     */
    export class ContentPublishDialog extends api.ui.dialog.ModalDialog {

        private modelName: string;

        private selectionItems: SelectionPublishItem<ContentPublishRequestedItem>[] = [];

        private publishButton: DialogButton;

        private publishAction: api.ui.Action;

        private publishDialogItemList: PublishDialogItemList = new PublishDialogItemList();

        private includeChildItemsCheck: api.ui.Checkbox;

        private subheaderMessage = new api.dom.H6El();

        private selectedContents: ContentSummary[];

        private pushRequestedContentsWithChildren: ContentPublishRequestedItem[] = [];

        private pushRequestedContentsWithoutChildren: ContentPublishRequestedItem[] = [];

        private alreadyResolvedWithChildren: boolean = false;

        constructor() {
            super({
                title: new api.ui.dialog.ModalDialogHeader("Publishing Wizard")
            });

            this.modelName = "item";


            this.getEl().addClass("publish-dialog");
            this.appendChildToContentPanel(this.publishDialogItemList);

            this.subheaderMessage.addClass("publish-dialog-subheader");
            this.appendChildToTitle(this.subheaderMessage);

            this.publishButton = this.setPublishAction(new ContentPublishDialogAction());

            this.getPublishAction().onExecuted(() => {
                this.doPublish();
            });

            this.addCancelButtonToBottom();

            this.includeChildItemsCheck = new api.ui.Checkbox();
            this.includeChildItemsCheck.addClass('include-child-check');
            this.includeChildItemsCheck.onValueChanged(() => {
                if (this.includeChildItemsCheck.isChecked() && !this.alreadyResolvedWithChildren) {
                    this.resolvePublishRequestedContentsAndUpdateView();
                } else {
                    this.renderResolvedPublishItems();
                    this.countItemsToPublishAndUpdateCounterElements();
                }
            });
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
            this.publishDialogItemList.clear();

            this.selectionItems.forEach((item: SelectionPublishItem<ContentPublishRequestedItem>)  => {
                if (!item.isHidden()) {
                    this.publishDialogItemList.appendChild(item);
                }
            });
            this.centerMyself();
        }

        /**
         * Inits selection items from resolved items returned via resolve request.
         * Method is called from renderResolvedPublishItems() so it also saves and restores unchecked items.
         */
        private initSelectionItems() {
            var unCheckedRequestedContentsIds: string[] = this.getCheckedContentsIds(false); // store unchecked publish requested values before re-init
            var publishRequestedSelectionItems: SelectionPublishItem<ContentPublishRequestedItem>[] = []; // store selection items of publish requested values during init

            this.selectionItems = [];

            var pushRequestedItems: ContentPublishRequestedItem[];

            if (this.includeChildItemsCheck.isChecked()) {
                pushRequestedItems = this.pushRequestedContentsWithChildren;
            } else {
                pushRequestedItems = this.pushRequestedContentsWithoutChildren;
            }

            pushRequestedItems.forEach((content: ContentPublishRequestedItem) => {
                var item: SelectionPublishItem<ContentPublishRequestedItem>;
                item = new SelectionPublishItemBuilder<ContentPublishRequestedItem>().create().
                    setContent(content).
                    setIsCheckBoxEnabled(true).
                    setIsHidden(false).
                    setIsChecked(true).
                    setExpandable(true).
                    setChangeCallback(() => {
                        this.countItemsToPublishAndUpdateCounterElements();
                    }).
                    setToggleCallback((isExpand: boolean) => {
                        this.resolveDependantsAndUpdateView(item, isExpand);
                    }).
                    build();
                this.selectionItems.push(item);
                publishRequestedSelectionItems.push(item);
            });

            this.restoreUncheckedState(unCheckedRequestedContentsIds, publishRequestedSelectionItems);
        }

        // get array of contents ids that were initially requested to publish and have given checked value
        private getCheckedContentsIds(checked: boolean): string[] {

            var checkedContentsIds: string[] = [];
            this.selectionItems.forEach((item: SelectionPublishItem<ContentPublishRequestedItem>)  => {
                if (item.isChecked() == checked) {
                    checkedContentsIds.push(item.getBrowseItem().getId());
                }
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
        private resolveDependantsAndUpdateView(selectionItem: SelectionPublishItem<ContentPublishRequestedItem>, showDependants: boolean) {

            var contentPublishRequestedItem: ContentPublishRequestedItem = <ContentPublishRequestedItem>selectionItem.getBrowseItem().getModel();
            if (showDependants) {
                if (!contentPublishRequestedItem.isDependantsResolved()) {
                    var getDependantsRequest = new api.content.ResolveDependantsRequest(contentPublishRequestedItem.getId(),
                        this.includeChildItemsCheck.isChecked());
                    getDependantsRequest.send().then((jsonResponse: api.rest.JsonResponse<GetDependantsResultJson>) => {
                        contentPublishRequestedItem.setDependantsResolved(ContentPublishDependantItem.getDependantsResolved(jsonResponse.getResult()));
                        this.initDependantsView(selectionItem);
                        selectionItem.showDependants();
                    }).done();
                } else {
                    if (!selectionItem.hasDependants()) {
                        this.initDependantsView(selectionItem);
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
        private initDependantsView(selectionItem: SelectionPublishItem<ContentPublishRequestedItem>): void {

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
                this.pushRequestedContentsWithChildren = ContentPublishRequestedItem.getPushRequestedContents(json);
                this.alreadyResolvedWithChildren = true;
            } else {
                this.pushRequestedContentsWithoutChildren = ContentPublishRequestedItem.getPushRequestedContents(json);
            }
        }

        private doPublish() {

            new PublishContentRequest().
                setIncludeChildren(this.includeChildItemsCheck.isChecked()).
                setIds(this.getCheckedContentsIds(true).map((id) => {
                    return new api.content.ContentId(id);
                })).send().done((jsonResponse: api.rest.JsonResponse<api.content.PublishContentResult>) => {
                    this.close();
                    PublishContentRequest.feedback(jsonResponse);
                });
        }

        private countItemsToPublishAndUpdateCounterElements() {

            var checkedRequested = this.getCheckedItemsCount(),
                dependantsEligibleForPublish = this.getDependantsEligibleForPublishCount(),
                childrenEligibleForPublish = this.getChildrenEligibleForPublishCount();

            //subheader
            this.subheaderMessage.setHtml("Based on your <b>selection</b> - we found <b>" +
                                          dependantsEligibleForPublish + " dependent</b> changes");

            // publish button
            this.cleanPublishButtonText();
            this.updatePublishButtonCounter(checkedRequested + dependantsEligibleForPublish + childrenEligibleForPublish);

            // includeChildren link
            var childrenEligibleForPublish = this.getChildrenEligibleForPublishCount();
            if (childrenEligibleForPublish > 0) {
                this.includeChildItemsCheck.setLabel('Include child items (+' + childrenEligibleForPublish + ')');
            } else {
                this.includeChildItemsCheck.setLabel('Include child items');
            }
        }

        // counts number of children that can be published with given selections upon pressing publish button
        private getChildrenEligibleForPublishCount(): number {
            var result = 0;
            this.selectionItems.forEach((item: SelectionPublishItem<ContentPublishRequestedItem>)  => {
                if (item.isChecked()) {
                    result += item.getBrowseItem().getModel().getChildrenCount();
                }
            });
            return result;
        }

        // counts number of dependants that can be published with given selections upon pressing publish button
        private getDependantsEligibleForPublishCount(): number {
            var result = 0;
            this.selectionItems.forEach((item: SelectionPublishItem<ContentPublishRequestedItem>)  => {
                if (item.isChecked()) {
                    result += item.getBrowseItem().getModel().getDependantsCount();
                }
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
        }

        clear() {
            this.removeChildren();
        }
    }

    export class ContentPublishDialogAction extends api.ui.Action {
        constructor() {
            super("Publish", "enter");
            this.setIconClass("publish-action");
        }
    }
}