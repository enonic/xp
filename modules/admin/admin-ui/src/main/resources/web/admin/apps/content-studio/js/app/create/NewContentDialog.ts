module app.create {

    import GetAllContentTypesRequest = api.schema.content.GetAllContentTypesRequest;
    import GetContentTypeByNameRequest = api.schema.content.GetContentTypeByNameRequest;
    import GetNearestSiteRequest = api.content.GetNearestSiteRequest;
    import ContentName = api.content.ContentName;
    import Content = api.content.Content;
    import ContentPath = api.content.ContentPath;
    import ContentTypeName = api.schema.content.ContentTypeName;
    import ContentTypeSummary = api.schema.content.ContentTypeSummary;
    import ContentType = api.schema.content.ContentType;
    import Site = api.content.site.Site;
    import ApplicationKey = api.application.ApplicationKey;
    import FileUploadStartedEvent = api.ui.uploader.FileUploadStartedEvent;
    import UploadItem = api.ui.uploader.UploadItem;
    import ListContentByPathRequest = api.content.ListContentByPathRequest;

    export class NewContentDialog extends api.ui.dialog.ModalDialog {

        private parentContent: api.content.Content;

        private contentDialogTitle: NewContentDialogTitle;

        private contentList: NewContentDialogList;

        private contentListMask: api.ui.mask.LoadMask;
        private recentListMask: api.ui.mask.LoadMask;

        private fileInput: api.ui.text.FileInput;

        private mediaUploaderEl: api.content.MediaUploaderEl;

        private listItems: NewContentDialogListItem[];
        private mostPopularItems: MostPopularItem[];

        private uploaderEnabled: boolean;

        private mockModalDialog: NewContentDialog; //used to calculate modal window height for smooth animation

        private mostPopularItemsBlock: MostPopularItemsBlock;

        private recentItemsBlock: RecentItemsBlock;

        constructor() {
            this.contentDialogTitle = new NewContentDialogTitle("Create Content", "");

            super({
                title: this.contentDialogTitle
            });

            this.uploaderEnabled = true;

            this.listItems = [];

            this.addClass("new-content-dialog hidden");

            this.initContentList();

            this.initMostPopularItemsBlock();

            this.initFileInput();

            this.initAndAppendContentSection();

            this.initAndAppendRecentItemsBlock();

            this.initMediaUploader();

            this.initLoadingMasks();

            api.dom.Body.get().appendChild(this);
        }

        private initContentList() {
            this.contentList = new app.create.NewContentDialogList();

            this.contentList.onSelected((event: app.create.NewContentDialogItemSelectedEvent) => {
                this.closeAndFireEventFromContentType(event.getItem());
            });
        }

        private initMostPopularItemsBlock() {
            this.mostPopularItemsBlock = new MostPopularItemsBlock();
            this.mostPopularItemsBlock.hide();
            this.mostPopularItems = [];

            this.mostPopularItemsBlock.getMostPopularList().onSelected((event: app.create.NewContentDialogItemSelectedEvent) => {
                this.closeAndFireEventFromContentType(event.getItem());
            });
        }

        private initFileInput() {
            this.fileInput = new api.ui.text.FileInput('large').setPlaceholder("Search for content types").setUploaderParams({
                parent: ContentPath.ROOT.toString()
            });

            this.fileInput.onUploadStarted((event: FileUploadStartedEvent<Content>) => {
                this.closeAndFireEventFromMediaUpload(event.getUploadItems());
            });

            this.fileInput.onInput((event: Event) => {
                if (api.util.StringHelper.isEmpty(this.fileInput.getValue()) && this.mostPopularItems.length > 0) {
                    this.mostPopularItemsBlock.show();
                } else {
                    this.mostPopularItemsBlock.hide();
                }

                this.filterList();
            });

            this.fileInput.onKeyUp((event: KeyboardEvent) => {
                if (event.keyCode === 27) {
                    this.getCancelAction().execute();
                }
            });
        }

        private initAndAppendContentSection() {
            var section = new api.dom.SectionEl().setClass("column");
            this.appendChildToContentPanel(section);

            var contentTypesListDiv = new api.dom.DivEl("content-types-content");
            contentTypesListDiv.appendChildren(<api.dom.Element>this.mostPopularItemsBlock,
                <api.dom.Element>this.contentList);

            section.appendChildren(<api.dom.Element>this.fileInput, <api.dom.Element>contentTypesListDiv);
        }


        private initAndAppendRecentItemsBlock() {
            this.recentItemsBlock = new RecentItemsBlock();
            this.appendChildToContentPanel(this.recentItemsBlock);

            this.recentItemsBlock.getRecentItemsList().onSelected((event: app.create.NewContentDialogItemSelectedEvent) => {
                this.closeAndFireEventFromContentType(event.getItem());
            });
        }

        private initMediaUploader() {

            var uploaderContainer = new api.dom.DivEl('uploader-container');
            this.appendChild(uploaderContainer);

            var uploaderMask = new api.dom.DivEl('uploader-mask');
            uploaderContainer.appendChild(uploaderMask);

            this.mediaUploaderEl = new api.content.MediaUploaderEl({
                operation: api.content.MediaUploaderElOperation.create,
                params: {
                    parent: ContentPath.ROOT.toString()
                },
                name: 'new-content-uploader',
                showResult: false,
                showReset: false,
                showCancel: false,
                allowMultiSelection: true,
                deferred: true  // wait till the window is shown
            });
            uploaderContainer.appendChild(this.mediaUploaderEl);

            this.mediaUploaderEl.onUploadStarted((event: FileUploadStartedEvent<Content>) => {
                this.closeAndFireEventFromMediaUpload(event.getUploadItems());
            });

            var dragOverEl;
            // make use of the fact that when dragging
            // first drag enter occurs on the child element and after that
            // drag leave occurs on the parent element that we came from
            // meaning that to know when we left some element
            // we need to compare it to the one currently dragged over
            this.onDragEnter((event: DragEvent) => {
                if (this.uploaderEnabled) {
                    var target = <HTMLElement> event.target;

                    if (!!dragOverEl || dragOverEl == this.getHTMLElement()) {
                        uploaderContainer.show();
                    }
                    dragOverEl = target;
                }
            });

            this.onDragLeave((event: DragEvent) => {
                if (this.uploaderEnabled) {
                    var targetEl = <HTMLElement> event.target;

                    if (dragOverEl == targetEl) {
                        uploaderContainer.hide();
                    }
                }
            });

            this.onDrop((event: DragEvent) => {
                if (this.uploaderEnabled) {
                    uploaderContainer.hide();
                }

            });
        }

        private initLoadingMasks() {
            this.contentListMask = new api.ui.mask.LoadMask(this.contentList);
            this.recentListMask = new api.ui.mask.LoadMask(this.recentItemsBlock.getRecentItemsList());
        }

        private closeAndFireEventFromMediaUpload(items: UploadItem<Content>[]) {
            this.close();
            new NewMediaUploadEvent(items, this.parentContent).fire();
        }

        private closeAndFireEventFromContentType(item: NewContentDialogListItem) {
            this.close();
            new NewContentEvent(item.getContentType(), this.parentContent).fire();
        }

        private filterList() {
            var inputValue = this.fileInput.getValue();
            var inputValueLowerCase = inputValue ? inputValue.toLowerCase() : undefined;

            var filteredItems = this.listItems.filter((item: NewContentDialogListItem) => {
                return (!inputValueLowerCase || (item.getDisplayName().toLowerCase().indexOf(inputValueLowerCase) != -1) ||
                        (item.getName().toLowerCase().indexOf(inputValueLowerCase) != -1));
            });

            this.contentList.setItems(filteredItems);
        }

        private filterByParentContent(items: NewContentDialogListItem[],
                                      siteApplicationKeys: ApplicationKey[]): NewContentDialogListItem[] {
            var createContentFilter = new api.content.CreateContentFilter().siteApplicationsFilter(siteApplicationKeys);
            return items.filter((item: NewContentDialogListItem) =>
                    createContentFilter.isCreateContentAllowed(this.parentContent, item.getContentType())
            );
        }

        setParentContent(parent: api.content.Content) {
            this.parentContent = parent;

            var params: {[key: string]: any} = {
                parent: parent ? parent.getPath().toString() : api.content.ContentPath.ROOT.toString()
            };

            this.mediaUploaderEl.setParams(params);
            this.fileInput.setUploaderParams(params)
        }

        open() {
            super.open();
            var keyBindings = [
                new api.ui.KeyBinding('up', () => {
                    api.dom.FormEl.moveFocusToPrevFocusable(api.dom.Element.fromHtmlElement(<HTMLElement>document.activeElement),
                        "input,li");
                }).setGlobal(true),
                new api.ui.KeyBinding('down', () => {
                    api.dom.FormEl.moveFocusToNextFocusable(api.dom.Element.fromHtmlElement(<HTMLElement>document.activeElement),
                        "input,li");
                }).setGlobal(true)];

            api.ui.KeyBindings.get().bindKeys(keyBindings);
        }

        show() {
            this.updateDialogTitlePath();

            this.toggleUploaderEnabled();
            this.resetFileInputWithUploader();

            super.show();

            this.fileInput.giveFocus();

            if (this.mockModalDialog == null) {
                this.createMockDialog();
            }

            // CMS-3711: reload content types each time when dialog is show.
            // It is slow but newly create content types are displayed.
            this.loadContentTypes();
        }

        hide() {
            super.hide();
            this.mediaUploaderEl.stop();
            this.addClass("hidden");
            this.removeClass("animated");
            this.mostPopularItemsBlock.hide();
        }

        close() {
            this.fileInput.reset();
            super.close();
        }

        private loadContentTypes() {

            this.showLoadingMasks();

            wemQ.all(this.prepareRequestsToFetchContentData())
                .spread((contentTypes: ContentTypeSummary[], directChilds: api.content.ContentResponse<api.content.ContentSummary>,
                         parentSite: Site) => {

                    this.listItems = this.createListOfContentTypeItems(contentTypes, parentSite);
                    this.mostPopularItems =
                        this.createMostPopularItemList(this.listItems.map((el) => el.getContentType()), directChilds.getContents());

                    this.resetNewContentDialogContent();
                    this.toggleMostPopularBlockShown();
                }).catch((reason: any) => {

                    api.DefaultErrorHandler.handle(reason);

                }).finally(() => {
                    this.filterList();
                    this.hideLoadingMasks();
                    this.handleModalDialogAnimation();
                }).done();
        }

        private showLoadingMasks() {
            this.contentList.insertChild(this.contentListMask, 0);
            this.recentItemsBlock.getRecentItemsList().insertChild(this.recentListMask, 0);
            this.contentListMask.show();
            this.recentListMask.show();
        }

        private hideLoadingMasks() {
            this.contentListMask.hide();
            this.recentListMask.hide();
        }

        private prepareRequestsToFetchContentData(): wemQ.Promise<any>[] {
            var requests: wemQ.Promise<any>[] = [];
            requests.push(new GetAllContentTypesRequest().sendAndParse());
            if (this.parentContent) {
                requests.push(new ListContentByPathRequest(this.parentContent.getPath()).sendAndParse());
                requests.push(new GetNearestSiteRequest(this.parentContent.getContentId()).sendAndParse());
            } else {
                requests.push(new ListContentByPathRequest(ContentPath.ROOT).sendAndParse());
            }

            return requests;
        }

        private showMockDialog() {
            wemjq(this.getEl().getHTMLElement()).show();
        }

        private handleModalDialogAnimation() {

            this.mockModalDialog.mostPopularItemsBlock.getMostPopularList().setItems(this.mostPopularItems);

            this.toggleMockDialogMostPopularBlockShown();

            this.mockModalDialog.contentList.setItems(this.listItems);

            this.updateMockDialogTitlePath();

            this.mockModalDialog.showMockDialog();

            this.addClass("animated");
            this.removeClass("hidden");

            this.alignDialogWindowVertically();
        }

        private toggleMockDialogMostPopularBlockShown() {
            if (this.mostPopularItems.length > 0) {
                this.mockModalDialog.mostPopularItemsBlock.show();
            } else {
                this.mockModalDialog.mostPopularItemsBlock.hide();
            }
        }

        private toggleMostPopularBlockShown() {
            if (this.mostPopularItems.length > 0) {
                this.mostPopularItemsBlock.getMostPopularList().setItems(this.mostPopularItems);
                this.mostPopularItemsBlock.show();
            }
        }

        private updateMockDialogTitlePath() {
            if (this.parentContent) {
                this.mockModalDialog.contentDialogTitle.setPath(this.parentContent.getPath().toString());
            } else {
                this.mockModalDialog.contentDialogTitle.setPath('');
            }
        }

        private updateDialogTitlePath() {
            if (this.parentContent) {
                this.contentDialogTitle.setPath(this.parentContent.getPath().toString());
            } else {
                this.contentDialogTitle.setPath('');
            }
        }

        private resetNewContentDialogContent() {
            if (this.listItems.length > 0) {
                this.contentList.setItems(this.listItems);
                this.recentItemsBlock.getRecentItemsList().setItems(this.listItems);
                this.mostPopularItemsBlock.getMostPopularList().setItems(this.mostPopularItems);
            } else {
                this.mostPopularItemsBlock.getMostPopularList().clearItems();
                this.contentList.clearItems();
                this.recentItemsBlock.getRecentItemsList().clearItems();
            }
        }

        private toggleUploaderEnabled() {
            this.uploaderEnabled = !this.parentContent || !this.parentContent.getType().isTemplateFolder();

            this.toggleClass("no-uploader-el", !this.uploaderEnabled);
        }

        private resetFileInputWithUploader() {
            this.mediaUploaderEl.reset();
            this.fileInput.reset();
            this.mediaUploaderEl.setEnabled(this.uploaderEnabled);
            this.fileInput.getUploader().setEnabled(this.uploaderEnabled);
        }

        private createMockDialog() {
            this.mockModalDialog = new NewContentDialog();
            this.mockModalDialog.close = function () {
                wemjq(this.getEl().getHTMLElement()).hide();
            };
            this.getParentElement().appendChild(this.mockModalDialog);
            this.mockModalDialog.addClass("mock-modal-dialog");
            this.mockModalDialog.removeClass("hidden");
        }

        private createListItems(contentTypes: ContentTypeSummary[]): NewContentDialogListItem[] {
            var contentTypesByName: {[name: string]: ContentTypeSummary} = {};
            var items: NewContentDialogListItem[] = [];

            contentTypes.forEach((contentType: ContentTypeSummary) => {
                // filter media type descendants out
                var contentTypeName = contentType.getContentTypeName();
                if (!contentTypeName.isMedia() && !contentTypeName.isDescendantOfMedia() && !contentTypeName.isFragment()) {
                    contentTypesByName[contentType.getName()] = contentType;
                    items.push(NewContentDialogListItem.fromContentType(contentType))
                }
            });

            items.sort(this.compareListItems);
            return items;
        }

        private createListOfContentTypeItems(allContentTypes: ContentTypeSummary[], parentSite: Site): NewContentDialogListItem[] {
            var allListItems: NewContentDialogListItem[] = this.createListItems(allContentTypes);
            var siteApplications: ApplicationKey[] = parentSite ? parentSite.getApplicationKeys() : [];
            return this.filterByParentContent(allListItems, siteApplications);
        }

        private findElementByFieldValue<T>(array: Array<T>, field: string, value: any): T {
            var result: T;

            array.every((element: T) => {
                if (element[field] == value) {
                    result = element;
                    return false;
                }
                return true;
            });

            return result;
        }

        private sortByCountAndDate(contentType1: ContentTypeInfo, contentType2: ContentTypeInfo) {
            if (contentType2.count == contentType1.count) {
                return contentType2.lastModified > contentType1.lastModified ? 1 : -1;
            }
            return contentType2.count - contentType1.count;
        }

        private getAggregatedItemList(contentTypes: api.content.ContentSummary[]) {
            var aggregatedList: ContentTypeInfo[] = [];

            contentTypes.forEach((content: api.content.ContentSummary) => {
                var contentType = content.getType().toString();
                var existingContent = this.findElementByFieldValue(aggregatedList, "contentType", contentType);

                if (existingContent) {
                    existingContent.count++;
                    if (content.getModifiedTime() > existingContent.lastModified) {
                        existingContent.lastModified = content.getModifiedTime();
                    }
                }
                else {
                    aggregatedList.push({contentType: contentType, count: 1, lastModified: content.getModifiedTime()});
                }
            });

            aggregatedList.sort(this.sortByCountAndDate);

            return aggregatedList;
        }

        private createMostPopularItemList(allowedContentTypes: ContentTypeSummary[],
                                          directChildContents: api.content.ContentSummary[]): MostPopularItem[] {
            var mostPopularItems: MostPopularItem[] = [],
                filteredList: api.content.ContentSummary[] = directChildContents.filter((content: api.content.ContentSummary) => {
                    return this.isAllowedContentType(allowedContentTypes, content);
                }),
                aggregatedList: ContentTypeInfo[] = this.getAggregatedItemList(filteredList);

            for (var i = 0; i < aggregatedList.length && i < MostPopularItemsBlock.DEFAULT_MAX_ITEMS; i++) {
                var contentType: ContentTypeSummary = this.findElementByFieldValue(allowedContentTypes, "name",
                    aggregatedList[i].contentType);
                mostPopularItems.push(new MostPopularItem(contentType, aggregatedList[i].count));
            }

            return mostPopularItems;
        }

        private compareListItems(item1: NewContentDialogListItem, item2: NewContentDialogListItem): number {
            if (item1.getDisplayName().toLowerCase() > item2.getDisplayName().toLowerCase()) {
                return 1;
            } else if (item1.getDisplayName().toLowerCase() < item2.getDisplayName().toLowerCase()) {
                return -1;
            } else if (item1.getName() > item2.getName()) {
                return 1;
            } else if (item1.getName() < item2.getName()) {
                return -1;
            } else {
                return 0;
            }
        }

        private isAllowedContentType(allowedContentTypes: ContentTypeSummary[], content: api.content.ContentSummary) {
            return !content.getType().isMedia() && !content.getType().isDescendantOfMedia() &&
                   Boolean(this.findElementByFieldValue(allowedContentTypes, "id", content.getType().toString()));
        }

        private alignDialogWindowVertically() {
            this.getEl().setMarginTop("-" + ( this.mockModalDialog.getEl().getHeightWithBorder() / 2) + "px");
        }
    }

    export class NewContentDialogTitle extends api.ui.dialog.ModalDialogHeader {

        private pathEl: api.dom.PEl;

        constructor(title: string, path: string) {
            super(title);

            this.pathEl = new api.dom.PEl('path');
            this.pathEl.setHtml(path);
            this.appendChild(this.pathEl);
        }

        setPath(path: string) {
            this.pathEl.setHtml(path).setVisible(!api.util.StringHelper.isBlank(path));
        }
    }

    export interface ContentTypeInfo {
        contentType: string;
        count: number;
        lastModified: Date;
    }

}
