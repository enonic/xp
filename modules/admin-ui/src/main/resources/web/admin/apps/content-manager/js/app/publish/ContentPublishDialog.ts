module app.publish {

    import ContentIconUrlResolver = api.content.ContentIconUrlResolver;
    import BrowseItem = api.app.browse.BrowseItem;
    import ContentPath = api.content.ContentPath;
    import ContentSummary = api.content.ContentSummary;
    import DialogButton = api.ui.dialog.DialogButton;
    import PublishContentRequest = api.content.PublishContentRequest;
    import ResolvePublishDependenciesResultJson = api.content.json.ResolvePublishDependenciesResultJson
    import CompareStatus = api.content.CompareStatus;

    export class ContentPublishDialog extends api.ui.dialog.ModalDialog {

        private modelName: string;

        private selectionItems: SelectionPublishItem<ContentPublishItem>[] = [];

        private publishButton: DialogButton;

        private publishAction: api.ui.Action;

        private publishDialogItemList: PublishDialogItemList = new PublishDialogItemList();

        private includeChildItemsCheck: api.ui.Checkbox;

        private subheaderMessage = new api.dom.H6El();

        private selectedContents: ContentSummary[];

        private dependantsResolvedWithChildrenIncluded: ContentPublishItem[];

        private dependantsResolvedWithoutChildrenIncluded: ContentPublishItem[];

        private childrenResolved: ContentPublishItem[];

        private pushRequestedContents: ContentPublishItem[];

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

                // re-render required as among the dependant items might be ones resolved via children references (rare case though)
                this.renderResolvedPublishItems();

                this.countItemsToPublishAndUpdateCounterElements();
            });
            this.appendChildToContentPanel(this.includeChildItemsCheck);
        }

        initAndOpen() {
            this.getPublishDependantsAndUpdateView();
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

            this.selectionItems.forEach((item: SelectionPublishItem<ContentPublishItem>)  => {
                if (!item.isHidden()) {
                    this.publishDialogItemList.appendChild(item);
                }
            });
        }

        /**
         * Inits selection items from resolved items returned via resolve request.
         * Method is called from renderResolvedPublishItems() so it also saves and restores unchecked items.
         */
        private initSelectionItems() {
            var unCheckedRequestedContentsIds: string[] = this.getUnCheckedRequestedContentsIds(); // store unchecked publish requested values before re-init
            var publishRequestedSelectionItems: SelectionPublishItem<ContentPublishItem>[] = []; // store selection items of publish requested values during init

            this.selectionItems = [];

            //initially requested
            this.pushRequestedContents.forEach((content: ContentPublishItem) => {
                var item: SelectionPublishItem<ContentPublishItem> = this.createSelectionPublishItemFromResolvedContent(content, true,
                    ResolvedPublishContext.REQUESTED);
                this.selectionItems.push(item);
                publishRequestedSelectionItems.push(item);
            });

            //dependants and children resolved with parameter includeChildren=true
            if (this.includeChildItemsCheck.isChecked()) {
                this.dependantsResolvedWithChildrenIncluded.forEach((content: ContentPublishItem) => {
                    this.selectionItems.push(this.createSelectionPublishItemFromResolvedContent(content, false,
                        ResolvedPublishContext.DEPENDANT));
                });
                this.childrenResolved.forEach((content: ContentPublishItem) => {
                    this.selectionItems.push(this.createSelectionPublishItemFromResolvedContent(content, false,
                        ResolvedPublishContext.CHILD, true));
                });
            } else {//dependants and children resolved with parameter includeChildren=false
                this.dependantsResolvedWithoutChildrenIncluded.forEach((content: ContentPublishItem) => {
                    this.selectionItems.push(this.createSelectionPublishItemFromResolvedContent(content, false,
                        ResolvedPublishContext.DEPENDANT));
                });
                this.childrenResolved.forEach((content: ContentPublishItem) => {
                    this.selectionItems.push(this.createSelectionPublishItemFromResolvedContent(content, false,
                        ResolvedPublishContext.CHILD, true, false));
                });
            }

            this.restoreUncheckedState(unCheckedRequestedContentsIds, publishRequestedSelectionItems);
        }

        // get array of contents ids that were initially requested to publish but then were unchecked in the dialog
        private getUnCheckedRequestedContentsIds(): string[] {
            var unCheckedRequestedContentsIds: string[] = [];
            this.selectionItems.forEach((item: SelectionPublishItem<ContentPublishItem>)  => {
                if (!item.isChecked()) {
                    switch (item.getResolvedContext()) {
                    case ResolvedPublishContext.REQUESTED:
                        unCheckedRequestedContentsIds.push(item.getBrowseItem().getId());
                        break;
                    }
                }
            });
            return unCheckedRequestedContentsIds;
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
         * Creates selection item from passed ContentPublishItem object.
         * @param content
         */
        private createSelectionPublishItemFromResolvedContent(content: ContentPublishItem, isCheckBoxEnabled: boolean,
                                                              resolvedPublishContext: ResolvedPublishContext,
                                                              isHidden: boolean = false,
                                                              isChecked: boolean = true): SelectionPublishItem<ContentPublishItem> {

            var publishItemViewer = new app.publish.ResolvedPublishContentViewer();
            publishItemViewer.setObject(content);

            var browseItem = new BrowseItem<ContentPublishItem>(content).
                setId(content.getId()).
                setDisplayName(content.getDisplayName()).
                setPath(content.getPath().toString()).
                setIconUrl(content.getIconUrl());

            var selectionPublishItem = new SelectionPublishItem(publishItemViewer, browseItem, CompareStatus[content.getCompareStatus()],
                resolvedPublishContext,
                () => {
                    if (isCheckBoxEnabled) {
                        if (selectionPublishItem.isChecked()) {
                            this.setCheckedForItemsWithGivenReason(selectionPublishItem, true);
                        } else {
                            this.setCheckedForItemsWithGivenReason(selectionPublishItem, false);
                        }
                        this.countItemsToPublishAndUpdateCounterElements();
                    }
                }, isCheckBoxEnabled, isHidden, isChecked);

            return selectionPublishItem;
        }

        /**
         * Sets checked state to all contents that are target for publish via passed selectionPublishItem (referenced via idOfContentThatTriggeredPublishForMe).
         * @param selectionPublishItem
         * @param checked
         */
        private setCheckedForItemsWithGivenReason(selectionPublishItem: SelectionPublishItem<ContentPublishItem>, checked: boolean) {
            this.selectionItems.forEach((item: SelectionPublishItem<ContentPublishItem>)  => {
                var iteratedPublishItem: ContentPublishItem = <ContentPublishItem>item.getBrowseItem().getModel(),
                    checkedPublishItem: ContentPublishItem = <ContentPublishItem>selectionPublishItem.getBrowseItem().getModel();
                if (iteratedPublishItem.getId() != checkedPublishItem.getId()
                    && iteratedPublishItem.getIdOfContentThatTriggeredPublishForMe() == checkedPublishItem.getId()) {
                    item.setChecked(checked);
                }
            });
        }

        /**
         * Perform request to resolve publish items, init and render results.
         */
        private getPublishDependantsAndUpdateView() {

            this.showLoadingSpinner();

            var getPublishContentDependantsRequest = new api.content.ResolvePublishDependenciesRequest(this.selectedContents.map((el) => {
                return new api.content.ContentId(el.getId());
            }));

            getPublishContentDependantsRequest.send().then((jsonResponse: api.rest.JsonResponse<ResolvePublishDependenciesResultJson>) => {
                this.initResolvedPublishItems(jsonResponse.getResult());
                this.renderResolvedPublishItems();
                this.countItemsToPublishAndUpdateCounterElements();
            }).finally(() => {
                this.hideLoadingSpinner();
            }).done();
        }

        /**
         * Inits arrays of properties that store results of performing resolve request.
         */
        private initResolvedPublishItems(json: ResolvePublishDependenciesResultJson) {

            this.pushRequestedContents = ContentPublishItem.getPushRequestedContents(json);

            this.dependantsResolvedWithChildrenIncluded = ContentPublishItem.getDependantsResolvedWithChildrenIncluded(json);

            this.dependantsResolvedWithoutChildrenIncluded = ContentPublishItem.getDependantsResolvedWithoutChildrenIncluded(json);

            this.childrenResolved = ContentPublishItem.getResolvedChildren(json);
        }

        private doPublish() {
            new PublishContentRequest().setIds(this.selectionItems.map((el) => {
                return new api.content.ContentId(el.getBrowseItem().getId());
            })).send().done((jsonResponse: api.rest.JsonResponse<api.content.PublishContentResult>) => {
                this.close();
                PublishContentRequest.feedback(jsonResponse);
            });
        }

        private countItemsToPublishAndUpdateCounterElements() {

            var checkedRequested = 0, checkedDependants = 0, checkedChildren = 0;
            this.selectionItems.forEach((item: SelectionPublishItem<ContentPublishItem>)  => {
                if (item.isChecked()) {
                    switch (item.getResolvedContext()) {
                    case ResolvedPublishContext.REQUESTED:
                        checkedRequested++;
                        break;
                    case ResolvedPublishContext.DEPENDANT:
                        checkedDependants++;
                        break;
                    case ResolvedPublishContext.CHILD:
                        checkedChildren++;
                        break;
                    }
                }
            });

            //subheader
            this.subheaderMessage.setHtml("Based on your <b>selection</b> - we found <b>" +
                                          checkedDependants + " dependent</b> changes");

            // publish button
            this.cleanPublishButtonText();
            this.updatePublishButtonCounter(checkedRequested + checkedDependants + checkedChildren);

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
            var checkedRequestedContentsIds: string[] = [];
            this.selectionItems.forEach((item: SelectionPublishItem<ContentPublishItem>)  => {
                if (item.isChecked()) {
                    switch (item.getResolvedContext()) {
                    case ResolvedPublishContext.REQUESTED:
                        checkedRequestedContentsIds.push(item.getBrowseItem().getId());
                        break;
                    }
                }
            });
            this.selectionItems.forEach((item: SelectionPublishItem<ContentPublishItem>)  => {
                switch (item.getResolvedContext()) {
                case ResolvedPublishContext.CHILD:
                    var itemReason = (<ContentPublishItem>item.getBrowseItem().getModel()).getIdOfContentThatTriggeredPublishForMe();
                    if (checkedRequestedContentsIds.indexOf(itemReason) > -1) {
                        result++
                    }
                }
            });
            return result;
        }

        private getCheckedItemsCount(): number {
            var result = 0;
            this.selectionItems.forEach((item: SelectionPublishItem<ContentPublishItem>)  => {
                if (item.isChecked()) {
                    result++;
                }
            });
            return result;
        }

        private getResolvedChildrenCount(): number {
            var result = 0;
            result += this.childrenResolved.length;
            return result;
        }

        private getResolvedDependantsCount(resolvedWithChildren: boolean): number {
            var result = 0;
            if (resolvedWithChildren) {
                result += this.dependantsResolvedWithChildrenIncluded.length;
            } else {
                result += this.dependantsResolvedWithoutChildrenIncluded.length;
            }
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