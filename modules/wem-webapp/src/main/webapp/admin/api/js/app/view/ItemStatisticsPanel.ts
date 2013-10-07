module api_app_view {

    export interface ItemStatisticsPanelParams {

        actionMenu:api_ui_menu.ActionMenu;
    }

    export class ItemStatisticsPanel extends api_ui.Panel {

        private browseItem:ViewItem;

        private header:ItemStatisticsHeader;

        private navigation:DetailPanelTabList;

        private tabs:DetailPanelTab[] = [];

        private canvas:api_dom.DivEl;

        private tabChangeCallback:(DetailPanelTab) => void;

        private actionMenu:api_ui_menu.ActionMenu;

        constructor(itemStatisticsPanelParams:ItemStatisticsPanelParams) {
            super("ItemStatisticsPanel");
            this.getEl().addClass("item-statistics-panel");

            this.actionMenu = itemStatisticsPanelParams.actionMenu;

            this.header = new ItemStatisticsHeader(itemStatisticsPanelParams.actionMenu);
            this.appendChild(this.header);

            this.addNavigation();
            this.addCanvas();
            this.setTabChangeCallback((tab:DetailPanelTab) => {
                this.setActiveTab(tab);
            });


            this.addTab(new DetailPanelTab("Analytics"));
            this.addTab(new DetailPanelTab("Sales"));
            this.addTab(new DetailPanelTab("History"));
        }

        setItem(item:api_app_view.ViewItem) {
            this.browseItem = item;
            this.header.setItem(item);
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

        private addNavigation() {
            this.navigation = new DetailPanelTabList();
            this.getEl().appendChild(this.navigation.getHTMLElement());
        }
    }

    export class ItemStatisticsHeader extends api_dom.DivEl {

        private browseItem:ViewItem;

        private iconContainerEl:api_dom.SpanEl = new api_dom.SpanEl();

        private headerTextEl = new api_dom.H1El();

        private subtitleEl = new api_dom.H4El();

        constructor(actionMenu:api_ui_menu.ActionMenu) {
            super("header", "header");

            this.appendChild(this.iconContainerEl);

            var hgroupEl = new api_dom.Element("hgroup");
            hgroupEl.appendChild(this.headerTextEl);
            hgroupEl.appendChild(this.subtitleEl);
            this.appendChild(hgroupEl);

            this.appendChild(actionMenu);
        }

        setItem(item:ViewItem) {
            this.browseItem = item;

            var icon:HTMLImageElement = api_util.ImageLoader.get(this.browseItem.getIconUrl() + "?size=64", 64, 64);
            var iconEl = new api_dom.Element("img", null, null, new api_dom.ImgHelper(icon));
            this.iconContainerEl.removeChildren();
            this.iconContainerEl.appendChild(iconEl);

            this.headerTextEl.getEl().setInnerHtml(this.browseItem.getDisplayName());
            this.subtitleEl.getEl().setInnerHtml(this.browseItem.getPath());
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
