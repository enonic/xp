module api_ui_detailpanel {
    export class DetailPanel extends api_ui.DivEl {

        ext;

        constructor() {
            super("detailpanel");
            this.getEl().addClass("detailpanel");
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

    export class DetailTabPanel extends api_ui.DivEl {
        private model:api_model.Model;
        private navigation:DetailPanelTabList;
        private tabs:DetailPanelTab[] = [];
        private canvas:api_ui.DivEl;
        private tabChangeCallback:(DetailPanelTab) => void;
        private actionMenu:api_ui_menu.ActionMenu;

        constructor(model:api_model.Model) {
            super("detailpanel-tab");
            this.getEl().addClass("detailpanel-tab")
            this.model = model;
            this.addHeader(model.data.name, model.id, model.data.iconUrl);
            this.addNavigation();
            this.addCanvas();
            this.setTabChangeCallback((tab:DetailPanelTab) => {
                this.setActiveTab(tab);
            })

        }

        private addHeader(title:string, subtitle:string, iconUrl:string) {
            var headerEl = new api_ui.DivEl("header");
            headerEl.getEl().addClass("header");

            var iconEl = api_util.ImageLoader.get(iconUrl + "?size=80", 80, 80);


            var hgroupEl = new api_ui.Element("hgroup");

            var headerTextEl = new api_ui.H1El();
            headerTextEl.getEl().setInnerHtml(title);
            hgroupEl.appendChild(headerTextEl);

            var subtitleEl = new api_ui.H4El();
            subtitleEl.getEl().setInnerHtml(subtitle);
            hgroupEl.appendChild(subtitleEl);

            headerEl.getEl().appendChild(iconEl);
            headerEl.appendChild(hgroupEl);
            headerEl.appendChild(this.createActionMenu());

            this.appendChild(headerEl);
        }

        private addCanvas() {
            var canvasEl = new api_ui.DivEl("canvas");
            this.canvas = canvasEl;
            canvasEl.getEl().addClass("canvas");
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

        addAction(action:api_action.Action) {
            this.actionMenu.addAction(action);
        }

        private createActionMenu() {
            this.actionMenu = new api_ui_menu.ActionMenu();
            return new api_ui_menu.ActionMenuButton(this.actionMenu);
        }

        private addNavigation() {
            this.navigation = new DetailPanelTabList();
            this.getEl().appendChild(this.navigation.getHTMLElement());
        }
    }

    export class DetailPanelTab {
        name:string;
        content:api_ui.Element;

        constructor(name:string) {
            this.name = name;
            this.content = new api_ui.DivEl("test-content");
            this.content.getEl().setInnerHtml(this.name);
        }
    }

    export class DetailPanelTabList extends api_ui.UlEl {

        private tabs:api_ui.LiEl[] = [];

        constructor() {
            super("tab-list");
            this.getEl().addClass("tab-list");
        }

        addTab(tab, clickCallback:(DetailPanelTab) => void) {
            var tabEl = new api_ui.LiEl("tab");
            this.tabs.push(tabEl);
            tabEl.getEl().setInnerHtml(tab.name);
            tabEl.getEl().addEventListener("click", (event) => {
                this.selectTab(tabEl);
                clickCallback(tab);
            });
            this.getEl().appendChild(tabEl.getHTMLElement());
        }

        private selectTab(tab:api_ui.LiEl) {
            this.tabs.forEach(function (entry:api_ui.LiEl) {
                entry.getEl().removeClass("active");
            });
            tab.getEl().addClass("active");
        }
    }

    export class DetailPanelBox extends api_ui.DivEl {
        private model:api_model.Model;

        constructor(model:any, removeCallback?:(DetailPanelBox) => void) {
            super("detailpanel-box");
            this.model = model;
            this.getEl().addClass("detailpanel-box");
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
