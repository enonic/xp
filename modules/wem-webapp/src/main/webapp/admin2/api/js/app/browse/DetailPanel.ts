module api_app_browse {
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


    }

    export class DetailTabPanel extends api_dom.DivEl {
        private model:api_model.Model;
        private navigation:DetailPanelTabList;
        private tabs:DetailPanelTab[] = [];
        private canvas:api_dom.DivEl;
        private tabChangeCallback:(DetailPanelTab) => void;
        private actionMenu:api_ui_menu.ActionMenu;

        constructor(model:api_model.Model) {
            super("detailpanel-tab", "detailpanel-tab");
            this.model = model;
            this.addHeader(model.data.name, model.id, model.data.iconUrl);
            this.addNavigation();
            this.addCanvas();
            this.setTabChangeCallback((tab:DetailPanelTab) => {
                this.setActiveTab(tab);
            })

        }

        private addHeader(title:string, subtitle:string, iconUrl:string) {
            var headerEl = new api_dom.DivEl("header", "header");

            var iconEl = api_util.ImageLoader.get(iconUrl + "?size=80", 80, 80);


            var hgroupEl = new api_dom.Element("hgroup");

            var headerTextEl = new api_dom.H1El();
            headerTextEl.getEl().setInnerHtml(title);
            hgroupEl.appendChild(headerTextEl);

            var subtitleEl = new api_dom.H4El();
            subtitleEl.getEl().setInnerHtml(subtitle);
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
        private model:api_model.Model;

        constructor(model:any, removeCallback?:(DetailPanelBox) => void) {
            super("detailpanel-box", "detailpanel-box");
            this.model = model;
            this.setIcon(model.data.iconUrl, 32);
            this.setData(model.data.displayName, model.data.name);
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

        getModel() {
            return this.model;
        }
    }

}
