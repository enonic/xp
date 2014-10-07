module app.create {

    import GetAllContentTypesRequest = api.schema.content.GetAllContentTypesRequest;
    import ContentName = api.content.ContentName;
    import ContentTypeName = api.schema.content.ContentTypeName;
    import ContentTypeSummary = api.schema.content.ContentTypeSummary;

    export class NewContentDialog extends api.ui.dialog.ModalDialog {

        private parentContent: api.content.Content;
        private grandParent: api.content.Content;

        private contentDialogTitle: NewContentDialogTitle;

        private recentList: RecentItemsList;
        private contentList: NewContentDialogList;

        private contentListMask: api.ui.mask.LoadMask;
        private recentListMask: api.ui.mask.LoadMask;

        private input: api.ui.text.TextInput;

        private listItems: NewContentDialogListItem[];

        constructor() {
            this.contentDialogTitle = new NewContentDialogTitle("What do you want to create?", "");

            super({
                title: this.contentDialogTitle
            });

            this.addClass("new-content-dialog");

            var leftColumn = new api.dom.DivEl().setClass("column column-left");
            this.appendChildToContentPanel(leftColumn);

            this.input = api.ui.text.TextInput.large("list-filter").setPlaceholder("Search");
            leftColumn.appendChild(this.input);

            this.contentList = new app.create.NewContentDialogList();
            leftColumn.appendChild(this.contentList);

            var rightColumn = new api.dom.DivEl("column column-right");
            this.appendChildToContentPanel(rightColumn);

            var dropzone = new api.dom.DivEl("dropzone").setId('new-content-dialog-dropzone');
            rightColumn.appendChild(dropzone);

            this.recentList = new RecentItemsList();
            rightColumn.appendChild(this.recentList);

            this.setCancelAction(new api.ui.Action("Cancel", "esc"));

            api.dom.Body.get().appendChild(this);

            this.contentListMask = new api.ui.mask.LoadMask(this.contentList);
            this.recentListMask = new api.ui.mask.LoadMask(this.recentList);

            this.contentList.appendChild(this.contentListMask);
            this.recentList.appendChild(this.recentListMask);

            this.listItems = [];

            this.input.onValueChanged((event: api.ui.ValueChangedEvent) => {
                this.filterList();
            });
            this.input.onKeyUp((event: KeyboardEvent) => {
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

            this.getCancelAction().onExecuted(()=> this.close());
        }

        private closeAndFireEventFromContentType(item: NewContentDialogListItem) {
            this.close();
            new NewContentEvent(item.getContentType(), this.parentContent).fire();
        }

        private filterList() {
            var inputValue = this.input.getValue();

            var filteredItems = this.listItems.filter((item: NewContentDialogListItem) => {
                return (!inputValue || (item.getDisplayName().indexOf(inputValue) != -1) || (item.getName().indexOf(inputValue) != -1));
            });

            this.contentList.setItems(filteredItems);
        }

        private filterByParentContent(items: NewContentDialogListItem[]): NewContentDialogListItem[] {
            return items.filter((item: NewContentDialogListItem) => {
                var contentTypeName = item.getContentType().getContentTypeName();
                // Don't show page-template content type if parent content is not 'templates'
                var parentContentIsPageTemplates = (this.parentContent &&
                                                    this.parentContent.getName().equals(ContentName.fromString("templates")));
                var grandParentIsSite = this.grandParent && this.grandParent.isSite();
                return !contentTypeName.isPageTemplate() || (parentContentIsPageTemplates && grandParentIsSite);
            });
        }

        setParentContent(parent: api.content.Content, grandParent: api.content.Content) {
            this.parentContent = parent;
            this.grandParent = grandParent;
        }

        show() {
            if (this.parentContent) {
                this.contentDialogTitle.setPath(this.parentContent.getPath().getParentPath().toString());
            }
            super.show();

            if (this.input.getValue()) {
                this.input.selectText();
            }
            this.input.giveFocus();

            // CMS-3711: reload content types each time when dialog is show.
            // It is slow but newly create content types are displayed.
            this.loadContentTypes();
        }

        private loadContentTypes() {
            this.contentListMask.show();
            this.recentListMask.show();

            var contentTypesRequest = new GetAllContentTypesRequest();

            wemQ.all([contentTypesRequest.sendAndParse()])
                .spread((contentTypes: ContentTypeSummary[]) => {

                    var listItems = this.createListItems(contentTypes);
                    this.listItems = this.filterByParentContent(listItems);

                    this.contentList.setItems(this.listItems);
                    this.recentList.setItems(this.listItems);


                }).catch((reason: any) => {

                    api.DefaultErrorHandler.handle(reason);

                }).finally(() => {
                    this.filterList();
                    this.contentListMask.hide();
                    this.recentListMask.hide();

                }).done();
        }

        private createListItems(contentTypes: ContentTypeSummary[]): NewContentDialogListItem[] {
            var contentTypesByName: {[name: string]: ContentTypeSummary} = {};
            contentTypes.forEach((contentType: ContentTypeSummary) => {
                contentTypesByName[contentType.getName()] = contentType;
            });

            var items: NewContentDialogListItem[] = [];
            contentTypes.forEach((contentType: ContentTypeSummary) => {
                items.push(NewContentDialogListItem.fromContentType(contentType))
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
            this.pathEl.setHtml(path);
        }
    }

}