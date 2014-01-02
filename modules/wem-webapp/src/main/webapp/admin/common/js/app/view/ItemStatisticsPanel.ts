module api.app.view {

    export interface ItemStatisticsPanelParams {

        actionMenu:api.ui.menu.ActionMenu;
    }

    export class ItemStatisticsPanel<M> extends api.ui.Panel {

        private browseItem:ViewItem<M>;

        private header:ItemStatisticsHeader<M>;

        private navigation:DetailPanelTabList;

        private tabs:DetailPanelTab[] = [];

        private canvas:api.dom.DivEl;

        private tabChangeCallback:(DetailPanelTab) => void;

        private actionMenu:api.ui.menu.ActionMenu;

        constructor(itemStatisticsPanelParams:ItemStatisticsPanelParams) {
            super("ItemStatisticsPanel");
            this.getEl().addClass("item-statistics-panel");

            this.actionMenu = itemStatisticsPanelParams.actionMenu;

            this.header = new ItemStatisticsHeader<M>(itemStatisticsPanelParams.actionMenu);
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

        setItem(item:api.app.view.ViewItem<M>) {
            this.browseItem = item;
            this.header.setItem(item);
        }

        private addCanvas() {
            var canvasEl = this.canvas = new api.dom.DivEl("canvas", "canvas");
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

        addAction(action:api.ui.Action) {
            this.actionMenu.addAction(action);
        }

        private addNavigation() {
            this.navigation = new DetailPanelTabList();
            this.getEl().appendChild(this.navigation.getHTMLElement());
        }
    }

    export class ItemStatisticsHeader<M> extends api.dom.DivEl {

        private browseItem:ViewItem<M>;

        private iconContainerEl:api.dom.SpanEl = new api.dom.SpanEl();

        private headerTextEl = new api.dom.H1El();

        private subtitleEl = new api.dom.H4El();

        constructor(actionMenu:api.ui.menu.ActionMenu) {
            super("header", "header");

            this.appendChild(this.iconContainerEl);

            var hgroupEl = new api.dom.Element("hgroup");
            hgroupEl.appendChild(this.headerTextEl);
            hgroupEl.appendChild(this.subtitleEl);
            this.appendChild(hgroupEl);

            this.appendChild(actionMenu);
        }

        setItem(item:ViewItem<M>) {
            this.browseItem = item;

            var icon:HTMLImageElement = api.util.ImageLoader.get(this.browseItem.getIconUrl() + "?size=64", 64, 64);
            var iconEl = new api.dom.Element("img", null, null, new api.dom.ImgHelper(icon));
            this.iconContainerEl.removeChildren();
            this.iconContainerEl.appendChild(iconEl);

            this.headerTextEl.getEl().setInnerHtml(this.browseItem.getDisplayName());
            this.subtitleEl.getEl().setInnerHtml(this.browseItem.getPath());
        }
    }

    export class DetailPanelTab {

        name:string;

        content:api.dom.Element;

        constructor(name:string) {
            this.name = name;
            this.content = new api.dom.DivEl("test-content");
            this.content.getEl().setInnerHtml(this.name);
        }
    }

    export class DetailPanelTabList extends api.dom.UlEl {

        private tabs:api.dom.LiEl[] = [];

        constructor() {
            super("tab-list", "tab-list");
        }

        addTab(tab, clickCallback:(DetailPanelTab) => void) {
            var tabEl = new api.dom.LiEl("tab");
            this.tabs.push(tabEl);
            tabEl.getEl().setInnerHtml(tab.name);
            tabEl.getEl().addEventListener("click", (event) => {
                this.selectTab(tabEl);
                clickCallback(tab);
            });
            this.getEl().appendChild(tabEl.getHTMLElement());
        }

        private selectTab(tab:api.dom.LiEl) {
            this.tabs.forEach(function (entry:api.dom.LiEl) {
                entry.getEl().removeClass("active");
            });
            tab.getEl().addClass("active");
        }
    }
}
