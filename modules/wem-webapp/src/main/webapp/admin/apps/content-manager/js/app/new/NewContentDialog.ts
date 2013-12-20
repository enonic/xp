module app_new {

    export class NewContentDialog extends api_ui_dialog.ModalDialog {

        private parentContent: api_content.Content;

        private recentList: RecentContentTypesList;
        private contentList: ContentTypesList;
        private templatesList: TemplatesList;

        private deckPanel: api_ui.NavigatedDeckPanel;
        private templatesTab: api_ui_tab.TabBarItem;
        private contentTab: api_ui_tab.TabBarItem;
        private input: api_dom.InputEl;

        constructor() {
            super({
                title: "Select Content Type",
                width: 800,
                height: 520
            });

            this.getEl().addClass("new-content-dialog");

            var leftColumn = new api_dom.DivEl().setClass("column column-left");
            this.appendChildToContentPanel(leftColumn);

            this.input = api_ui.TextInput.large().setPlaceholder("Search");
            this.input.addClass("list-filter");
            this.input.getEl().addEventListener("keyup", (event: Event) => {
                var value = (<HTMLInputElement> event.target).value;
                this.filterList(value);
            });
            leftColumn.appendChild(this.input);

            var tabBar = new api_ui_tab.TabBar();
            this.deckPanel = new api_ui.NavigatedDeckPanel(tabBar);
            this.deckPanel.setClass("deck-panel");
            this.deckPanel.addListener({
                onPanelShown: (event: api_ui.PanelShownEvent) => {
                    var value = this.input.getValue();
                    this.filterList(value);
                }
            });
            leftColumn.appendChild(tabBar);
            leftColumn.appendChild(this.deckPanel);

            this.contentTab = new api_ui_tab.TabBarItem("Content (0)");
            this.contentList = new app_new.ContentTypesList("content-type-list");
            this.contentList.addListener({
                onSelected: (contentTypeListItem: ContentTypeListItem) => {
                    this.closeAndFireEventFromContentType(contentTypeListItem);
                }
            });
            this.deckPanel.addNavigablePanelToBack(this.contentTab, this.contentList);

            this.templatesTab = new api_ui_tab.TabBarItem("Sites (0)");
            this.templatesList = new app_new.TemplatesList("site-template-list");
            this.templatesList.addListener({
                onSelected: (item: SiteTemplateListItem) => {
                    this.closeAndFireEventFromSiteTemplate(item);
                }
            });
            this.deckPanel.addNavigablePanelToBack(this.templatesTab, this.templatesList);
            this.deckPanel.showPanel(0);
            tabBar.selectNavigationItem(0);

            var rightColumn = new api_dom.DivEl().setClass("column column-right");
            this.appendChildToContentPanel(rightColumn);

            this.recentList = new RecentContentTypesList("content-type-list");

            var dropZone = new api_dom.DivEl("DropZone", "drop-zone");
            rightColumn.appendChild(dropZone);

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

            api_dom.Body.get().appendChild(this);
        }

        private closeAndFireEventFromContentType(item: ContentTypeListItem) {
            this.close();
            new NewContentEvent(item.getContentType(), this.parentContent, item.isSiteRoot()).fire();
        }

        private closeAndFireEventFromSiteTemplate(item: SiteTemplateListItem) {
            this.close();
            new NewContentEvent(item.getContentType(), this.parentContent, true).fire();
        }

        private filterList(value: string) {
            if (this.contentList.isVisible()) {
                this.contentList.filter("displayName", value);
            } else {
                this.templatesList.filter("displayName", value);
            }
        }


        setParentContent(value: api_content.Content) {
            this.parentContent = value;
        }

        show() {

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

    export class CancelNewContentDialog extends api_ui.Action {

        constructor() {
            super("Cancel", "esc");
        }
    }

}