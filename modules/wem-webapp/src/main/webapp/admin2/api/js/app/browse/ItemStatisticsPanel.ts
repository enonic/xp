module api_app_browse {

    export class ItemStatisticsPanel extends api_dom.DivEl {

        private detailPanelItem:BrowseItem;

        private navigation:DetailPanelTabList;

        private tabs:DetailPanelTab[] = [];

        private canvas:api_dom.DivEl;

        private tabChangeCallback:(DetailPanelTab) => void;

        private actionMenu:api_ui_menu.ActionMenu;

        constructor(item:BrowseItem) {
            super("ItemStatisticsPanel", "browse-item-panel-tab");
            this.detailPanelItem = item;
            this.addHeader();
            this.addNavigation();
            this.addCanvas();
            this.setTabChangeCallback((tab:DetailPanelTab) => {
                this.setActiveTab(tab);
            })

        }

        private addHeader() {
            var headerEl = new api_dom.DivEl("header", "header");

            var iconEl = api_util.ImageLoader.get(this.detailPanelItem.getIconUrl() + "?size=80", 80, 80);


            var hgroupEl = new api_dom.Element("hgroup");

            var headerTextEl = new api_dom.H1El();
            headerTextEl.getEl().setInnerHtml(this.detailPanelItem.getDisplayName());
            hgroupEl.appendChild(headerTextEl);

            var subtitleEl = new api_dom.H4El();
            subtitleEl.getEl().setInnerHtml(this.detailPanelItem.getPath());
            hgroupEl.appendChild(subtitleEl);

            headerEl.getEl().appendChild(iconEl);
            headerEl.appendChild(hgroupEl);
            headerEl.appendChild(this.createActionMenu());

            this.appendChild(headerEl);
        }

        private addCanvas() {
            var canvasEl = this.canvas = new api_dom.DivEl("canvas", "canvas");
            this.appendChild(canvasEl);
        }

        setTabChangeCallback(callback:(DetailPanelTab) => void) {
            this.tabChangeCallback = callback;
        }

        addTab(tab:DetailPanelTab) {
            this.tabs.push(tab);
            this.navigation.addTab(tab, this.tabChangeCallback)
        }

        setActiveTab(tab:DetailPanelTab) {
            this.canvas.getEl().setInnerHtml("");
            this.canvas.appendChild(tab.content);
        }

        addAction(action:api_ui.Action) {
            this.actionMenu.addAction(action);
        }

        private createActionMenu() {
            this.actionMenu = new api_ui_menu.ActionMenu();
            return this.actionMenu;
        }

        private addNavigation() {
            this.navigation = new DetailPanelTabList();
            this.getEl().appendChild(this.navigation.getHTMLElement());
        }
    }

    export class DetailPanelTab {

        name:string;

        content:api_dom.Element;

        constructor(name:string) {
            this.name = name;
            this.content = new api_dom.DivEl("test-content");
            this.content.getEl().setInnerHtml(this.name);
        }
    }

    export class DetailPanelTabList extends api_dom.UlEl {

        private tabs:api_dom.LiEl[] = [];

        constructor() {
            super("tab-list", "tab-list");
        }

        addTab(tab, clickCallback:(DetailPanelTab) => void) {
            var tabEl = new api_dom.LiEl("tab");
            this.tabs.push(tabEl);
            tabEl.getEl().setInnerHtml(tab.name);
            tabEl.getEl().addEventListener("click", (event) => {
                this.selectTab(tabEl);
                clickCallback(tab);
            });
            this.getEl().appendChild(tabEl.getHTMLElement());
        }

        private selectTab(tab:api_dom.LiEl) {
            this.tabs.forEach(function (entry:api_dom.LiEl) {
                entry.getEl().removeClass("active");
            });
            tab.getEl().addClass("active");
        }
    }
}
