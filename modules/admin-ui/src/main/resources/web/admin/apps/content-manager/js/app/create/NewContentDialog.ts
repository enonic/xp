module app.create {

    import GetAllContentTypesRequest = api.schema.content.GetAllContentTypesRequest;
    import GetContentTypeByNameRequest = api.schema.content.GetContentTypeByNameRequest;
    import GetNearestSiteRequest = api.content.GetNearestSiteRequest;
    import ContentName = api.content.ContentName;
    import Content = api.content.Content;
    import ContentPath = api.content.ContentPath;
    import Attachment = api.content.attachment.Attachment;
    import AttachmentName = api.content.attachment.AttachmentName;
    import ContentTypeName = api.schema.content.ContentTypeName;
    import ContentTypeSummary = api.schema.content.ContentTypeSummary;
    import ContentType = api.schema.content.ContentType;
    import Site =api.content.site.Site;
    import ModuleKey = api.module.ModuleKey;
    import FileUploadStartedEvent = api.ui.uploader.FileUploadStartedEvent;
    import UploadItem = api.ui.uploader.UploadItem;

    export class NewContentDialog extends api.ui.dialog.ModalDialog {

        private parentContent: api.content.Content;

        private contentDialogTitle: NewContentDialogTitle;

        private recentList: RecentItemsList;
        private contentList: NewContentDialogList;

        private contentListMask: api.ui.mask.LoadMask;
        private recentListMask: api.ui.mask.LoadMask;

        private fileInput: api.ui.text.FileInput;

        private mediaUploader: api.content.MediaUploader;

        private listItems: NewContentDialogListItem[];

        private mockModalDialog: NewContentDialog; //used to calculate modal window height for smooth animation

        constructor() {
            this.contentDialogTitle = new NewContentDialogTitle("Create Content", "");

            super({
                title: this.contentDialogTitle
            });

            this.addClass("new-content-dialog hidden");

            var section = new api.dom.SectionEl().setClass("column");
            this.appendChildToContentPanel(section);

            this.fileInput = new api.ui.text.FileInput('large').setPlaceholder("Search for content types").setUploaderParams({
                parent: ContentPath.ROOT.toString()
            });
            this.fileInput.onUploadStarted((event: FileUploadStartedEvent<Content>) => {
                this.closeAndFireEventFromMediaUpload(event.getUploadItems());
            });

            this.contentList = new app.create.NewContentDialogList();

            section.appendChildren(this.fileInput, this.contentList);

            var aside = new api.dom.AsideEl("column");
            this.appendChildToContentPanel(aside);

            this.initMediaUploader();

            var recentTitle = new api.dom.H1El();
            recentTitle.setHtml('Recently Used');

            this.recentList = new RecentItemsList();

            aside.appendChildren(recentTitle, this.recentList);

            api.dom.Body.get().appendChild(this);

            this.contentListMask = new api.ui.mask.LoadMask(this.contentList);
            this.recentListMask = new api.ui.mask.LoadMask(this.recentList);

            this.listItems = [];

            this.fileInput.onInput((event: Event) => {
                this.filterList();
            });
            this.fileInput.onKeyUp((event: KeyboardEvent) => {
                if (event.keyCode === 27) {
                    this.getCancelAction().execute();
                }
            });

            this.contentList.onSelected((event: app.create.NewContentDialogItemSelectedEvent) => {
                this.closeAndFireEventFromContentType(event.getItem());
            });

            this.recentList.onSelected((event: app.create.NewContentDialogItemSelectedEvent) => {
                this.closeAndFireEventFromContentType(event.getItem());
            });

        }

        private initMediaUploader() {

            var uploaderContainer = new api.dom.DivEl('uploader-container');
            this.appendChild(uploaderContainer);

            var uploaderMask = new api.dom.DivEl('uploader-mask');
            uploaderContainer.appendChild(uploaderMask);

            this.mediaUploader = new api.content.MediaUploader({
                operation: api.content.MediaUploaderOperation.create,
                params: {
                    parent: ContentPath.ROOT.toString()
                },
                name: 'new-content-uploader',
                showButtons: false,
                showResult: false,
                allowMultiSelection: true,
                deferred: true  // wait till the window is shown
            });
            uploaderContainer.appendChild(this.mediaUploader);

            this.mediaUploader.onUploadStarted((event: FileUploadStartedEvent<Content>) => {
                this.closeAndFireEventFromMediaUpload(event.getUploadItems());
            });

            var dragOverEl;
            // make use of the fact that when dragging
            // first drag enter occurs on the child element and after that
            // drag leave occurs on the parent element that we came from
            // meaning that to know when we left some element
            // we need to compare it to the one currently dragged over
            this.onDragEnter((event: DragEvent) => {
                var target = <HTMLElement> event.target;

                if (!!dragOverEl || dragOverEl == this.getHTMLElement()) {
                    uploaderContainer.show();
                }
                dragOverEl = target;
            });

            this.onDragLeave((event: DragEvent) => {
                var targetEl = <HTMLElement> event.target;

                if (dragOverEl == targetEl) {
                    uploaderContainer.hide();
                }
            });

            this.onDrop((event: DragEvent) => {
                uploaderContainer.hide();
            });
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

            var filteredItems = this.listItems.filter((item: NewContentDialogListItem) => {
                return (!inputValue || (item.getDisplayName().indexOf(inputValue) != -1) || (item.getName().indexOf(inputValue) != -1));
            });

            this.contentList.setItems(filteredItems);
        }

        private filterByParentContent(items: NewContentDialogListItem[], siteModuleKeys: ModuleKey[]): NewContentDialogListItem[] {
            var typesAllowedEverywhere: {[key:string]: ContentTypeName} = {};
            [ContentTypeName.UNSTRUCTURED, ContentTypeName.FOLDER, ContentTypeName.SITE,
                ContentTypeName.SHORTCUT].forEach((contentTypeName: ContentTypeName) => {
                    typesAllowedEverywhere[contentTypeName.toString()] = contentTypeName;
                });
            var siteModules: {[key:string]: ModuleKey} = {};
            siteModuleKeys.forEach((moduleKey: ModuleKey) => {
                siteModules[moduleKey.toString()] = moduleKey;
            });

            var parentContentIsTemplateFolder = this.parentContent && this.parentContent.getType().isTemplateFolder();
            var parentContentIsSite = this.parentContent && this.parentContent.getType().isSite();
            var parentContentIsPageTemplate = this.parentContent && this.parentContent.getType().isPageTemplate();

            return items.filter((item: NewContentDialogListItem) => {
                var contentType = item.getContentType();
                var contentTypeName = contentType.getContentTypeName();
                if (contentType.isAbstract()) {
                    return false;
                }
                else if (parentContentIsPageTemplate) {
                    return false; // children not allowed for page-template
                }
                else if (contentTypeName.isTemplateFolder()) {
                    return parentContentIsSite; // template-folder only allowed under site
                }
                else if (contentTypeName.isPageTemplate()) {
                    return parentContentIsTemplateFolder; // page-template only allowed under a template-folder
                }
                else if (parentContentIsTemplateFolder) {
                    return contentTypeName.isPageTemplate(); // in a template-folder allow only page-template
                }
                else if (typesAllowedEverywhere[contentTypeName.toString()]) {
                    return true;
                }
                else if (siteModules[contentTypeName.getModuleKey().toString()]) {
                    return true;
                }
                else {
                    return false;
                }

            });
        }

        setParentContent(parent: api.content.Content) {
            this.parentContent = parent;

            var params: {[key: string]: any} = {
                parent: parent ? parent.getPath().toString() : api.content.ContentPath.ROOT.toString()
            };

            this.mediaUploader.setParams(params);
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
            if (this.parentContent) {
                this.contentDialogTitle.setPath(this.parentContent.getPath().toString());
            } else {
                this.contentDialogTitle.setPath('');
            }
            super.show();

            this.fileInput.reset().giveFocus();

            this.mediaUploader.reset();

            // CMS-3711: reload content types each time when dialog is show.
            // It is slow but newly create content types are displayed.
            this.loadContentTypes();
        }

        hide() {
            super.hide();
            this.mediaUploader.stop();
            this.addClass("hidden");
            this.removeClass("animated");
        }

        close() {
            this.fileInput.reset();
            super.close();
        }

        private loadContentTypes() {
            this.contentList.insertChild(this.contentListMask, 0);
            this.recentList.insertChild(this.recentListMask, 0);
            this.contentListMask.show();
            this.recentListMask.show();

            var requests: wemQ.Promise<any>[] = [];
            requests.push(new GetAllContentTypesRequest().sendAndParse());
            if (this.parentContent) {
                requests.push(new GetNearestSiteRequest(this.parentContent.getContentId()).sendAndParse());
            }

            wemQ.all(requests)
                .spread((contentTypes: ContentTypeSummary[], parentSite: Site) => {
                    var listItems = this.createListItems(contentTypes);

                    var siteModules: ModuleKey[] = parentSite ? parentSite.getModuleKeys() : [];
                    this.listItems = this.filterByParentContent(listItems, siteModules);

                    if (this.listItems.length > 0) {
                        this.contentList.setItems(this.listItems);
                        this.recentList.setItems(this.listItems);
                    } else {
                        this.contentList.clearItems();
                        this.recentList.clearItems();
                    }


                }).catch((reason: any) => {

                    api.DefaultErrorHandler.handle(reason);

                }).finally(() => {
                    this.filterList();
                    this.contentListMask.hide();
                    this.recentListMask.hide();

                    this.handleModalDialogAnimation();
                }).done();
        }

        private showMockDialog() {
            super.show();
            this.removeClass("hidden");
        }

        private handleModalDialogAnimation() {
            if(this.mockModalDialog == null) {
                this.mockModalDialog = new NewContentDialog();
                this.mockModalDialog.close = function() {
                    this.hide();
                }
                this.getParentElement().appendChild(this.mockModalDialog);
                this.mockModalDialog.addClass("mock-modal-dialog");
            }

            this.mockModalDialog.contentList.setItems(this.listItems);

            if (this.parentContent) {
                this.mockModalDialog.contentDialogTitle.setPath(this.parentContent.getPath().toString());
            } else {
                this.mockModalDialog.contentDialogTitle.setPath('');
            }
            this.mockModalDialog.showMockDialog();

            this.addClass("animated");
            this.removeClass("hidden");
            this.getEl().setMarginTop("-" + ( this.mockModalDialog.getEl().getHeightWithBorder() / 2) + "px");
        }

        private createListItems(contentTypes: ContentTypeSummary[]): NewContentDialogListItem[] {
            var contentTypesByName: {[name: string]: ContentTypeSummary} = {};
            var items: NewContentDialogListItem[] = [];

            contentTypes.forEach((contentType: ContentTypeSummary) => {
                // filter media type descendants out
                var contentTypeName = contentType.getContentTypeName();
                if (!contentTypeName.isMedia() && !contentTypeName.isDescendantOfMedia()) {
                    contentTypesByName[contentType.getName()] = contentType;
                    items.push(NewContentDialogListItem.fromContentType(contentType))
                }
            });

            items.sort(this.compareListItems);
            return items;
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

}