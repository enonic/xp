module api_app_browse {

    export class DetailPanelItem {

        private model:any;

        private displayName:string;

        private path:string;

        private iconUrl;

        constructor(model:any) {
            this.model = model;
        }

        setDisplayName(value:string):DetailPanelItem {
            this.displayName = value;
            return this;
        }

        setPath(value:string):DetailPanelItem {
            this.path = value;
            return this;
        }

        setIconUrl(value:string):DetailPanelItem {
            this.iconUrl = value;
            return this;
        }

        getModel():any {
            return this.model;
        }

        getDisplayName():string {
            return this.displayName;
        }

        getPath():string {
            return this.path;
        }

        getIconUrl():string {
            return this.iconUrl;
        }
    }

    export class DetailPanel extends api_dom.DivEl {

        ext;

        constructor() {
            super("detailpanel", "detailpanel");
            this.initExt();
        }

        private initExt() {
            var htmlEl = this.getHTMLElement();
            this.ext = new Ext.Component({
                contentEl: htmlEl,
                region: 'south',
                split: true
            });
        }

        setItems(items:DetailPanelItem[]) {

            if (items.length == 1) {
                this.showSingle(items[0]);
            } else if (items.length > 1) {
                this.showMultiple(items);
            }
        }

        showSingle(item:DetailPanelItem) {

            this.empty();

            var tabPanel = new api_app_browse.DetailTabPanel(item);
            tabPanel.addTab(new api_app_browse.DetailPanelTab("Analytics"));
            tabPanel.addTab(new api_app_browse.DetailPanelTab("Sales"));
            tabPanel.addTab(new api_app_browse.DetailPanelTab("History"));

            tabPanel.addAction(new api_ui.Action("Test"));
            tabPanel.addAction(new api_ui.Action("More test"));
            tabPanel.addAction(new api_ui.Action("And finally the last one"));

            this.getEl().appendChild(tabPanel.getHTMLElement());
        }

        showMultiple(items:DetailPanelItem[]) {
            this.empty();
            for (var i in items) {

                var removeCallback = (box:api_app_browse.DetailPanelBox) => {
                    this.fireGridDeselectEvent(box.getDetailPanelItem().getModel());
                }

                this.getEl().appendChild(new api_app_browse.DetailPanelBox(items[i], removeCallback).getHTMLElement());
            }
        }

        fireGridDeselectEvent(model:any) {

        }
    }

    export class DetailTabPanel extends api_dom.DivEl {

        private detailPanelItem:DetailPanelItem;

        private navigation:DetailPanelTabList;

        private tabs:DetailPanelTab[] = [];

        private canvas:api_dom.DivEl;

        private tabChangeCallback:(DetailPanelTab) => void;

        private actionMenu:api_ui_menu.ActionMenu;

        constructor(item:DetailPanelItem) {
            super("detailpanel-tab", "detailpanel-tab");
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
            this.canvas.empty();
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

    export class DetailPanelBox extends api_dom.DivEl {

        private detailPanelItem:DetailPanelItem;

        constructor(detailPanelItem:DetailPanelItem, removeCallback?:(DetailPanelBox) => void) {
            super("detailpanel-box", "detailpanel-box");
            this.detailPanelItem = detailPanelItem;
            this.setIcon(this.detailPanelItem.getIconUrl(), 32);
            this.setData(this.detailPanelItem.getDisplayName(), this.detailPanelItem.getPath());
            this.addRemoveButton(removeCallback);
        }

        private addRemoveButton(callback?:(DetailPanelBox) => void) {
            var removeEl = document.createElement("div");
            removeEl.className = "remove";
            removeEl.innerHTML = "&times;";
            removeEl.addEventListener("click", (event) => {
                this.getEl().remove();
                if (callback) {
                    callback(this);
                }
            });
            this.getEl().appendChild(removeEl);
        }

        private setIcon(iconUrl:string, size:number) {
            this.getEl().appendChild(api_util.ImageLoader.get(iconUrl + "?size=" + size, 32, 32));
        }

        private setData(title:string, subtitle:string) {
            var titleEl = document.createElement("h6");
            titleEl.innerHTML = title;

            var subtitleEl = document.createElement("small");
            subtitleEl.innerHTML = subtitle;
            titleEl.appendChild(subtitleEl);

            this.getEl().appendChild(titleEl);
            return titleEl;
        }

        getDetailPanelItem():DetailPanelItem {
            return this.detailPanelItem;
        }
    }

}
