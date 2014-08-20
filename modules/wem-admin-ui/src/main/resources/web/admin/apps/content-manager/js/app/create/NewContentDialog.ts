module app.create {

    import GetAllContentTypesRequest = api.schema.content.GetAllContentTypesRequest;
    import GetAllSiteTemplatesRequest = api.content.site.template.GetAllSiteTemplatesRequest;
    import ContentTypeSummary = api.schema.content.ContentTypeSummary;
    import SiteTemplateSummary = api.content.site.template.SiteTemplateSummary;

    export class NewContentDialog extends api.ui.dialog.ModalDialog {

        private parentContent: api.content.Content;

        private contentDialogTitle: NewContentDialogTitle;

        private recentList: RecentItemsList;
        private contentList: NewContentDialogList;

        private contentListMask: api.ui.mask.LoadMask;
        private recentListMask: api.ui.mask.LoadMask;

        private input: api.ui.text.TextInput;
        private facetContainer: NewContentDialogFacets;

        private listItems: NewContentDialogListItem[];
        private checkReloadListItems: boolean;

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

            this.facetContainer = new NewContentDialogFacets();
            leftColumn.appendChild(this.facetContainer);

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

            this.listItems = [];
            this.checkReloadListItems = true;

            this.input.onValueChanged((event: api.ui.ValueChangedEvent) => {
                this.filterList();
            });
            this.input.onKeyUp((event: KeyboardEvent) => {
                if (event.keyCode === 27) {
                    this.getCancelAction().execute();
                }
            });

            this.facetContainer.onValueChanged((event: api.ui.ValueChangedEvent) => this.filterList());

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
            new NewContentEvent(item.getContentType(), this.parentContent, item.getSiteTemplate()).fire();
        }

        private filterList() {
            var inputValue = this.input.getValue();
            var activeFacet = this.facetContainer.getActiveFacet();
            var contentOnly = activeFacet == NewContentDialogFacets.CONTENT;
            var sitesOnly = activeFacet == NewContentDialogFacets.SITES;
            var all = activeFacet == NewContentDialogFacets.ALL;

            var filteredItems = this.listItems.filter((item: NewContentDialogListItem) => {
                return (!inputValue || (item.getDisplayName().indexOf(inputValue) != -1) || (item.getName().indexOf(inputValue) != -1))
                    && (all || (contentOnly && !item.isSiteTemplate()) || (sitesOnly && item.isSiteTemplate()));
            });

            this.contentList.setItems(filteredItems);

            var contentTypesCount: number = 0;
            var siteTemplatesCount: number = 0;
            this.listItems.forEach((item: NewContentDialogListItem) => {
                if (!inputValue || (item.getDisplayName().indexOf(inputValue) != -1) || (item.getName().indexOf(inputValue) != -1)) {
                    item.isSiteTemplate() ? siteTemplatesCount++ : contentTypesCount++;
                }
            });
            this.facetContainer.updateLabels(contentTypesCount, siteTemplatesCount);
        }

        setParentContent(value: api.content.Content) {
            this.parentContent = value;
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

            if (this.checkReloadListItems) {
                this.loadContentTypes();
            } else {
                this.recentList.setItems(this.listItems);
            }
        }

        private loadContentTypes() {
            this.contentListMask.show();
            this.recentListMask.show();

            var contentTypesRequest = new GetAllContentTypesRequest();
            var siteTemplatesRequest = new GetAllSiteTemplatesRequest();

            wemQ.all([contentTypesRequest.sendAndParse(), siteTemplatesRequest.sendAndParse()])
                .spread((contentTypes: ContentTypeSummary[], siteTemplates: SiteTemplateSummary[]) => {

                    this.facetContainer.updateLabels(contentTypes.length, siteTemplates.length);
                    this.listItems = this.createListItems(contentTypes, siteTemplates);
                    this.contentList.setItems(this.listItems);
                    this.recentList.setItems(this.listItems);

                    var activeFacet = this.facetContainer.getActiveFacet();
                    if (this.input.getValue() || activeFacet != NewContentDialogFacets.ALL) {
                        this.filterList();
                    }

                }).catch((reason: any) => {

                    api.DefaultErrorHandler.handle(reason);

                }).finally(() => {

                    this.contentListMask.hide();
                    this.recentListMask.hide();

                }).done();
        }

        private createListItems(contentTypes: ContentTypeSummary[], siteTemplates: SiteTemplateSummary[]): NewContentDialogListItem[] {
            var contentTypesByName: {[name: string]: ContentTypeSummary} = {};
            contentTypes.forEach((contentType: ContentTypeSummary) => {
                contentTypesByName[contentType.getName()] = contentType;
            });

            var items: NewContentDialogListItem[] = [];
            contentTypes.forEach((contentType: ContentTypeSummary) => {
                items.push(NewContentDialogListItem.fromContentType(contentType))
            });

            var siteContentType = contentTypesByName['site'];
            siteTemplates.forEach((siteTemplate: SiteTemplateSummary) => {
                items.push(NewContentDialogListItem.fromSiteTemplate(siteTemplate, siteContentType));
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