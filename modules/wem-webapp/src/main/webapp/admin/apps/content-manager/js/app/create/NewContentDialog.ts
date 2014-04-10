module app.create {

    import GetAllContentTypesRequest = api.schema.content.GetAllContentTypesRequest;
    import GetAllSiteTemplatesRequest = api.content.site.template.GetAllSiteTemplatesRequest;
    import ContentTypeSummary = api.schema.content.ContentTypeSummary;
    import SiteTemplateSummary = api.content.site.template.SiteTemplateSummary;

    export class NewContentDialog extends api.ui.dialog.ModalDialog {

        private static CONTENT = 'content';
        private static SITES = 'sites';

        private parentContent: api.content.Content;

        private contentDialogTitle: NewContentDialogTitle;

        private recentList: RecentItemsList;
        private contentList: NewContentDialogList;

        private contentListMask: api.ui.LoadMask;
        private recentListMask: api.ui.LoadMask;

        private input: api.ui.TextInput;
        private contentFacet: api.dom.SpanEl;
        private sitesFacet: api.dom.SpanEl;

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

            var facets = new api.dom.DivEl('content-type-facet');
            this.contentFacet = new api.dom.SpanEl('content-facet').setHtml("Content (0)");
            this.sitesFacet = new api.dom.SpanEl('sites-facet').setHtml("Sites (0)");
            facets.appendChild(this.contentFacet);
            facets.appendChild(this.sitesFacet);
            leftColumn.appendChild(facets);

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

            this.contentFacet.onClicked((event: MouseEvent) => {
                this.contentFacet.hasClass('active') ? this.contentFacet.removeClass('active') : this.contentFacet.setClass('active');
                this.sitesFacet.removeClass('active');
                this.filterList();
            });

            this.sitesFacet.onClicked((event: MouseEvent) => {
                this.sitesFacet.hasClass('active') ? this.sitesFacet.removeClass('active') : this.sitesFacet.setClass('active');
                this.contentFacet.removeClass('active');
                this.filterList();
            });

            this.contentList.onSelected((event: app.create.NewContentDialogItemSelectedEvent) => {
                this.closeAndFireEventFromContentType(event.getItem());
            });

            this.recentList.onSelected((event: app.create.NewContentDialogItemSelectedEvent) => {
                this.closeAndFireEventFromContentType(event.getItem());
            });

            this.getCancelAction().addExecutionListener(()=> this.close());
        }

        private closeAndFireEventFromContentType(item: NewContentDialogListItem) {
            this.close();
            new NewContentEvent(item.getContentType(), this.parentContent, item.getSiteTemplate()).fire();
        }

        private filterList() {
            var inputValue = this.input.getValue();
            var contentOnly = this.contentFacet.hasClass('active');
            var sitesOnly = this.sitesFacet.hasClass('active');

            var filteredItems = this.listItems.filter((item: NewContentDialogListItem) => {
                return (!inputValue || (item.getDisplayName().indexOf(inputValue) != -1) || (item.getName().indexOf(inputValue) != -1))
                    && ((!contentOnly && !sitesOnly) || (contentOnly && !item.isSiteTemplate()) || (sitesOnly && item.isSiteTemplate()));
            });

            this.contentList.setItems(filteredItems);

            var contentTypesCount = 0;
            var siteTemplatesCount = 0;
            this.listItems.forEach((item: NewContentDialogListItem) => {
                if (!inputValue || (item.getDisplayName().indexOf(inputValue) != -1) || (item.getName().indexOf(inputValue) != -1)) {
                    item.isSiteTemplate() ? siteTemplatesCount++ : contentTypesCount++;
                }
            });
            this.contentFacet.setHtml("Content (" + contentTypesCount + ")");
            this.sitesFacet.setHtml("Sites (" + siteTemplatesCount + ")");
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

                    this.contentFacet.setHtml("Content (" + contentTypes.length + ")");
                    this.sitesFacet.setHtml("Sites (" + siteTemplates.length + ")");
                    this.listItems = this.createListItems(contentTypes, siteTemplates);
                    this.contentList.setItems(this.listItems);
                    this.recentList.setItems(this.listItems);

                    if (this.input.getValue()) {
                        this.filterList();
                    }

                }).catch((reason: any) => {

                    api.notify.Message.newError(reason.toString());

                }).finally(() => {

                    this.contentListMask.hide();
                    this.recentListMask.hide();

                }).done();
        }

        private createListItems(contentTypes: ContentTypeSummary[], siteTemplates: SiteTemplateSummary[]):NewContentDialogListItem[] {
            var contentTypesByName: {[name: string]: ContentTypeSummary} = {};
            contentTypes.forEach((contentType:ContentTypeSummary) => {
                contentTypesByName[contentType.getName()] = contentType;
            });

            var items:NewContentDialogListItem[] = [];
            contentTypes.forEach((contentType: ContentTypeSummary) => {
                items.push(NewContentDialogListItem.fromContentType(contentType))
            });

            siteTemplates.forEach((siteTemplate: SiteTemplateSummary) => {
                var rootContentType = contentTypesByName[siteTemplate.getRootContentType().toString()];
                items.push(NewContentDialogListItem.fromSiteTemplate(siteTemplate, rootContentType));
            });

            items.sort(this.compareListItems);
            return items;
        }

        private compareListItems(item1: NewContentDialogListItem, item2: NewContentDialogListItem): number {
            if (item1.getDisplayName() > item2.getDisplayName()) {
                return 1;
            } else if (item1.getDisplayName() < item2.getDisplayName()) {
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