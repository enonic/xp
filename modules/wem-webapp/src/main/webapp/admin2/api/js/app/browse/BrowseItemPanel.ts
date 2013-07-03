module api_app_browse {

    export interface BrowseItemPanelParams {

        actionMenuActions:api_ui.Action[];
    }

    export class BrowseItemPanel extends api_dom.DivEl {

        ext;

        private actionMenuActions:api_ui.Action[];

        constructor(browseItemPanelParams:BrowseItemPanelParams) {
            super("BrowseItemPanel", "browse-item-panel");
            this.actionMenuActions = browseItemPanelParams.actionMenuActions;
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

        setItems(items:api_app_browse.BrowseItem[]) {

            if( items.length == 0 ) {
                this.showBlank();
            }
            else if (items.length == 1) {
                this.showSingle(items[0]);
            } else if (items.length > 1) {
                this.showMultiple(items);
            }
        }

        showBlank() {
            this.removeChildren();
        }

        showSingle(item:api_app_browse.BrowseItem) {

            this.removeChildren();

            var tabPanel = new ItemStatisticsPanel(item);
            tabPanel.addTab(new DetailPanelTab("Analytics"));
            tabPanel.addTab(new DetailPanelTab("Sales"));
            tabPanel.addTab(new DetailPanelTab("History"));

            this.actionMenuActions.forEach( (action:api_ui.Action) => {

                tabPanel.addAction( action );
            } );

            this.getEl().appendChild(tabPanel.getHTMLElement());
        }

        showMultiple(items:api_app_browse.BrowseItem[]) {
            this.removeChildren();

            for (var i in items) {

                var removeCallback = (box:api_app_browse.DetailPanelBox) => {
                    this.fireGridDeselectEvent(box.getDetailPanelItem().getModel());
                };

                this.appendChild(new api_app_browse.DetailPanelBox(items[i], removeCallback));
            }
        }

        fireGridDeselectEvent(model:any) {

        }
    }

    export class DetailPanelBox extends api_dom.DivEl {

        private detailPanelItem:api_app_browse.BrowseItem;

        constructor(detailPanelItem:api_app_browse.BrowseItem, removeCallback?:(DetailPanelBox) => void) {
            super("DetailPanelBox", "browse-item-panel-box");
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

        getDetailPanelItem():api_app_browse.BrowseItem {
            return this.detailPanelItem;
        }
    }

}
