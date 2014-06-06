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

        private contentListMask: api.ui.LoadMask;
        private recentListMask: api.ui.LoadMask;

        private input: api.ui.TextInput;
        private facetContainer: api.dom.DivEl;
        private contentFacet: api.dom.SpanEl;
        private sitesFacet: api.dom.SpanEl;
        private allFacet: api.dom.SpanEl;

        private listItems: NewContentDialogListItem[];

        constructor() {
            this.contentDialogTitle = new NewContentDialogTitle("What do you want to create?", "");

            super({
                title: this.contentDialogTitle
            });

            this.addClass("new-content-dialog");

            var leftColumn = new api.dom.DivEl().setClass("column column-left");
            this.appendChildToContentPanel(leftColumn);

            this.input = api.ui.TextInput.large("list-filter").setPlaceholder("Search");
            leftColumn.appendChild(this.input);

            this.facetContainer = new api.dom.DivEl('content-type-facet');
            this.allFacet = new api.dom.SpanEl('all-facet').setHtml("All (0)");
            this.contentFacet = new api.dom.SpanEl('content-facet').setHtml("Content (0)");
            this.sitesFacet = new api.dom.SpanEl('sites-facet').setHtml("Sites (0)");

            this.facetContainer.appendChild(this.allFacet);
            this.facetContainer.appendChild(this.contentFacet);
            this.facetContainer.appendChild(this.sitesFacet);
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

            this.contentListMask = new api.ui.LoadMask(this.contentList);
            this.recentListMask = new api.ui.LoadMask(this.recentList);

            this.listItems = [];

            this.input.onValueChanged((event: api.ui.ValueChangedEvent) => {
                this.filterList();
            });
            this.input.onKeyUp((event: KeyboardEvent) => {
                if (event.keyCode === 27) {
                    this.getCancelAction().execute();
                }
            });

            this.allFacet.onClicked((event: MouseEvent) => {
                this.facetContainer.getChildren().forEach((child:api.dom.Element) => {
                    child.removeClass('active');
                });
                this.allFacet.addClass('active');
                this.filterList();
            });

            this.contentFacet.onClicked((event: MouseEvent) => {
                this.facetContainer.getChildren().forEach((child:api.dom.Element) => {
                    child.removeClass('active');
                });
                this.contentFacet.addClass('active');
                this.filterList();
            });

            this.sitesFacet.onClicked((event: MouseEvent) => {
                this.facetContainer.getChildren().forEach((child:api.dom.Element) => {
                    child.removeClass('active');
                });
                this.sitesFacet.addClass('active');
                this.filterList();
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
            new NewContentEvent(item.getContentType(), this.parentContent, item.getSiteTemplate()).fire();
        }

        private filterList() {
            var inputValue = this.input.getValue();
            var contentOnly = this.contentFacet.hasClass('active');
            var sitesOnly = this.sitesFacet.hasClass('active');
            var all = this.allFacet.hasClass('active');

            var filteredItems = this.listItems.filter((item: NewContentDialogListItem) => {
                return (!inputValue || (item.getDisplayName().indexOf(inputValue) != -1) || (item.getName().indexOf(inputValue) != -1))
                    && ((!contentOnly && !sitesOnly) || (contentOnly && !item.isSiteTemplate()) || (sitesOnly && item.isSiteTemplate()) || all);
            });

            this.contentList.setItems(filteredItems);

            var contentTypesCount:number = 0;
            var siteTemplatesCount:number = 0;
            this.listItems.forEach((item: NewContentDialogListItem) => {
                if (!inputValue || (item.getDisplayName().indexOf(inputValue) != -1) || (item.getName().indexOf(inputValue) != -1)) {
                    item.isSiteTemplate() ? siteTemplatesCount++ : contentTypesCount++;
                }
            });
            this.contentFacet.setHtml("Content (" + contentTypesCount + ")");
            this.sitesFacet.setHtml("Sites (" + siteTemplatesCount + ")");
            this.allFacet.setHtml("All (" + (siteTemplatesCount + contentTypesCount) + ")");
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

            if (this.listItems.length == 0) {
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

            Q.all([contentTypesRequest.sendAndParse(), siteTemplatesRequest.sendAndParse()])
                .spread((contentTypes: ContentTypeSummary[], siteTemplates: SiteTemplateSummary[]) => {

                    this.allFacet.setHtml("All (" + (siteTemplates.length + + contentTypes.length) + ")");
                    this.contentFacet.setHtml("Content (" + contentTypes.length + ")");
                    this.sitesFacet.setHtml("Sites (" + siteTemplates.length + ")");
                    this.listItems = this.createListItems(contentTypes, siteTemplates);
                    this.contentList.setItems(this.listItems);
                    this.recentList.setItems(this.listItems);

                    this.allFacet.addClass('active');

                    if (this.input.getValue()) {
                        this.filterList();
                    }

                }).catch((reason: any) => {

                    api.notify.showError(reason.toString());

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
            this.pathEl.setText(path);
            this.appendChild(this.pathEl);
        }

        setPath(path: string) {
            this.pathEl.setText(path);
        }
    }

}