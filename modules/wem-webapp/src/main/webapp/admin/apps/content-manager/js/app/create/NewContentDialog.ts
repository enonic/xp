module app.create {

    export class NewContentDialog extends api.ui.dialog.ModalDialog {

        private parentContent: api.content.Content;

        private contentDialogTitle: NewContentDialogTitle;

        private recentList: RecentContentTypesList;
        private contentList: ContentTypesList;
        private templatesList: SiteTemplatesList;

        private deckPanel: api.ui.NavigatedDeckPanel;
        private templatesTab: api.ui.tab.TabBarItem;
        private contentTab: api.ui.tab.TabBarItem;
        private input: api.dom.InputEl;

        constructor() {
            this.contentDialogTitle = new NewContentDialogTitle("What do you want to create?", "");

            super({
                title: this.contentDialogTitle,
                width: 800,
                height: 520
            });

            this.getEl().addClass("new-content-dialog");

            var leftColumn = new api.dom.DivEl().setClass("column column-left");
            this.appendChildToContentPanel(leftColumn);

            this.input = api.ui.TextInput.large().setPlaceholder("Search");
            this.input.addClass("list-filter");
            this.input.getEl().addEventListener("keyup", (event: Event) => {
                var value = (<HTMLInputElement> event.target).value;
                this.filterList(value);
            });
            leftColumn.appendChild(this.input);

            var tabBar = new api.ui.tab.TabBar();
            this.deckPanel = new api.ui.NavigatedDeckPanel(tabBar);
            this.deckPanel.addListener({
                onPanelShown: (event: api.ui.PanelShownEvent) => {
                    var value = this.input.getValue();
                    this.filterList(value);
                }
            });
            leftColumn.appendChild(tabBar);
            leftColumn.appendChild(this.deckPanel);

            this.contentTab = new api.ui.tab.TabBarItem("Content (0)");
            this.contentList = new app.create.ContentTypesList("content-type-list");
            this.contentList.addListener({
                onSelected: (contentTypeListItem: ContentTypeListItem) => {
                    this.closeAndFireEventFromContentType(contentTypeListItem);
                }
            });
            this.deckPanel.addNavigablePanelToBack(this.contentTab, this.contentList);

            this.templatesTab = new api.ui.tab.TabBarItem("Sites (0)");
            this.templatesList = new app.create.SiteTemplatesList("site-template-list");
            this.templatesList.addListener({
                onSelected: (item: SiteTemplateListItem) => {
                    this.closeAndFireEventFromSiteTemplate(item);
                }
            });
            this.deckPanel.addNavigablePanelToBack(this.templatesTab, this.templatesList);
            this.deckPanel.showPanel(0);
            tabBar.selectNavigationItem(0);

            var rightColumn = new api.dom.DivEl("column column-right");
            this.appendChildToContentPanel(rightColumn);

            this.recentList = new RecentContentTypesList("content-type-list");

            var dropzone = new api.dom.DivEl("dropzone");
            // id needed for plupload to init
            dropzone.setId('new-content-dialog-dropzone');
            rightColumn.appendChild(dropzone);

            this.recentList.addListener({
                onSelected: (item: ContentTypeListItem) => {
                    this.closeAndFireEventFromContentType(item);
                }
            });
            rightColumn.appendChild(this.recentList);

            this.setCancelAction(new CancelNewContentDialog());
            this.getCancelAction().addExecutionListener(()=> {
                this.close();
            });

            api.dom.Body.get().appendChild(this);
        }

        private closeAndFireEventFromContentType(item: ContentTypeListItem) {
            this.close();
            new NewContentEvent(item.getContentType(), this.parentContent, null).fire();
        }

        private closeAndFireEventFromSiteTemplate(item: SiteTemplateListItem) {
            this.close();
            new NewContentEvent(item.getContentType(), this.parentContent, item.getSiteTemplate()).fire();
        }

        private filterList(value: string) {
            if (this.contentList.isVisible()) {
                this.contentList.filter("displayName", value);
            } else {
                this.templatesList.filter("displayName", value);
            }
        }


        setParentContent(value: api.content.Content) {
            this.parentContent = value;
        }

        show() {
            if (this.parentContent) {
                this.contentDialogTitle.setPath(this.parentContent.getPath().getParentPath().toString());
            }

            super.show();

            ContentTypes.load((contentTypes: ContentTypes)=> {

                SiteRootContentTypes.load((siteRootContentTypes: SiteRootContentTypes)=> {

                    this.contentTab.setLabel("Content (" + contentTypes.get().length + ")");
                    this.contentList.setContentTypes(contentTypes, siteRootContentTypes);
                    this.recentList.setContentTypes(contentTypes, siteRootContentTypes);

                    var siteTemplates = siteRootContentTypes.getSiteTemplates();

                    this.templatesTab.setLabel("Sites (" + siteTemplates.length + ")");
                    this.templatesList.setSiteTemplates(siteTemplates, contentTypes);
                });
            });

        }
    }

    export class NewContentDialogTitle extends api.ui.dialog.ModalDialogHeader {

        private titleEl:api.dom.H2El;

        private pathEl:api.dom.PEl;

        constructor(title:string, path:string) {
            super("");

            this.titleEl = new api.dom.H2El('title');
            this.titleEl.setText(title);
            this.appendChild(this.titleEl);

            this.pathEl = new api.dom.PEl('path');
            this.pathEl.setText(path);
            this.appendChild(this.pathEl);
        }

        setTitle(title:string) {
            this.titleEl.setText(title);
        }

        setPath(path:string) {
            this.pathEl.setText(path);
        }
    }

    export class CancelNewContentDialog extends api.ui.Action {

        constructor() {
            super("Cancel", "esc");
        }
    }

}