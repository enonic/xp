module app.publish {

    import ContentIconUrlResolver = api.content.ContentIconUrlResolver;
    import BrowseItem = api.app.browse.BrowseItem;
    import ContentPath = api.content.ContentPath;
    import SelectionItem = api.app.browse.SelectionItem;
    import ContentSummary = api.content.ContentSummary;
    import DialogButton = api.ui.dialog.DialogButton;
    import PublishContentRequest = api.content.PublishContentRequest;

    export class ContentPublishDialog extends api.ui.dialog.ModalDialog {

        private modelName: string;

        private selectedItems: SelectionItem<ContentSummary>[];

        private publishButton: DialogButton;

        private publishAction: api.ui.Action;

        private itemList: PublishDialogItemList = new PublishDialogItemList();

        private includeChildItemsCheck: api.ui.Checkbox;

        private subheaderMessage = new api.dom.H6El();

        private publishContentDependants: api.content.ResolvePublishDependenciesResult;

        constructor() {
            super({
                title: new api.ui.dialog.ModalDialogHeader("Publishing Wizard")
            });

            this.modelName = "item";

            this.getEl().addClass("publish-dialog");
            this.appendChildToContentPanel(this.itemList);

            this.subheaderMessage.addClass("publish-dialog-subheader");
            this.appendChildToTitle(this.subheaderMessage);

            this.publishButton = this.setPublishAction(new ContentPublishDialogAction());

            this.getPublishAction().onExecuted(() => {
                this.doPublish();
            });

            this.addCancelButtonToBottom();

            this.includeChildItemsCheck = new api.ui.Checkbox();
            this.includeChildItemsCheck.getEl().setDisabled(true);
            this.includeChildItemsCheck.addClass('include-child-check');
            this.includeChildItemsCheck.onValueChanged(() => {
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

        renderResolvedPublishItems(selectedItems: SelectionItem<ContentSummary>[]) {
            this.itemList.clear();

            for (var i in selectedItems) {
                var selectionItem: SelectionItem<ContentSummary> = selectedItems[i];
                this.itemList.appendChild(selectionItem);
            }
        }


        setContentToPublish(contents: ContentSummary[]) {
            this.selectedItems = [];

            contents.forEach((content: ContentSummary) => {
                this.selectedItems.push(this.createSelectionItemForPublish(content));
            });
        }

        private indexOf(item: SelectionItem<ContentSummary>): number {
            for (var i = 0; i < this.selectedItems.length; i++) {
                if (item.getBrowseItem().getPath() == this.selectedItems[i].getBrowseItem().getPath()) {
                    return i;
                }
            }
            return -1;
        }

        private createSelectionItemForPublish(content: ContentSummary): SelectionItem<ContentSummary> {

            var publishItemViewer = new api.content.ContentSummaryViewer();
            publishItemViewer.setObject(content);

            var browseItem = new BrowseItem<ContentSummary>(content).
                setId(content.getId()).
                setDisplayName(content.getDisplayName()).
                setPath(content.getPath().toString()).
                setIconUrl(new ContentIconUrlResolver().setContent(content).resolve());

            var selectionItem = new SelectionItem(publishItemViewer, browseItem, () => {
                var index = this.indexOf(selectionItem);
                if (index < 0) {
                    return;
                }

                this.selectedItems[index].remove();
                this.selectedItems.splice(index, 1);

                if (this.selectedItems.length == 0) {
                    this.close();
                }
            });

            return selectionItem;
        }

        private getPublishDependantsAndUpdateView() {

            this.showLoadingSpinner();

            var getPublishContentDependantsRequest = new api.content.ResolvePublishDependenciesRequest(this.selectedItems.map((el) => {
                return new api.content.ContentId(el.getBrowseItem().getId());
            }));

            getPublishContentDependantsRequest.send().then((jsonResponse: api.rest.JsonResponse<api.content.ResolvePublishDependenciesResult>) => {
                this.publishContentDependants = jsonResponse.getResult();
                this.renderResolvedPublishItems(this.selectedItems);
                this.countItemsToPublishAndUpdateCounterElements();
            }).finally(() => {
                this.hideLoadingSpinner();
            }).done();
        }

        private doPublish() {
            new PublishContentRequest().setIds(this.selectedItems.map((el) => {
                return new api.content.ContentId(el.getBrowseItem().getId());
            })).send().done((jsonResponse: api.rest.JsonResponse<api.content.PublishContentResult>) => {
                this.close();
                PublishContentRequest.feedback(jsonResponse);
            });
        }

        private countItemsToPublishAndUpdateCounterElements() {
            //subheader
            if (this.includeChildItemsCheck.isChecked()) {
                this.subheaderMessage.setHtml("Based on your <b>selection</b> - we found <b>" +
                                              this.getResolvedDependantsCount(true) +
                                              " dependent</b> changes");
            } else {
                this.subheaderMessage.setHtml("Based on your <b>selection</b> - we found <b>" +
                                              this.getResolvedDependantsCount(false) +
                                              " dependent</b> changes");
            }

            // publish button
            this.cleanPublishButtonText();
            if (this.includeChildItemsCheck.isChecked()) {
                this.updatePublishButtonCounter(this.selectedItems.length +
                                                this.getResolvedDependantsCount(true) + this.getResolvedChildrenCount());
            } else {
                this.updatePublishButtonCounter(this.selectedItems.length +
                                                this.getResolvedDependantsCount(false));
            }

            // includeChildren link
            if (this.getResolvedChildrenCount() > 0) {
                this.includeChildItemsCheck.setLabel('Include child items (+' + this.getResolvedChildrenCount() + ')');
                this.includeChildItemsCheck.getEl().setDisabled(false);
            } else {
                this.includeChildItemsCheck.getEl().setDisabled(true);
                this.includeChildItemsCheck.setLabel('Include child items');
            }
        }

        private getResolvedChildrenCount(): number {
            var result = 0;
            result += this.publishContentDependants.childrenResolved.length;
            result += this.publishContentDependants.deletedChildrenResolved.length;
            return result;
        }

        private getResolvedDependantsCount(childrenIncluded: boolean): number {
            var result = 0;
            if (childrenIncluded) {
                result += this.publishContentDependants.dependantsResolvedWithChildrenIncluded.length;
                result += this.publishContentDependants.deletedDependantsResolvedWithChildrenIncluded.length;
            } else {
                result += this.publishContentDependants.dependantsResolvedWithoutChildrenIncluded.length;
                result += this.publishContentDependants.deletedDependantsResolvedWithoutChildrenIncluded.length;
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