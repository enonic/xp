module api_app_browse {

    export class ItemsSelectionPanel extends api_ui.Panel {

        private fireGridDeselectEvent:Function;

        constructor(fireGridDeselectEvent:Function) {
            super("ItemsSelectionPanel");
            this.fireGridDeselectEvent = fireGridDeselectEvent;
        }

        setItems(items:BrowseItem[]) {

            this.removeChildren();

            if (items.length > 0) {
                items.forEach((item:BrowseItem) => {

                    var removeCallback = (selectionItem:SelectionItem) => {
                        this.fireGridDeselectEvent(selectionItem.getBrowseItem().getModel());
                    };

                    this.appendChild(new SelectionItem(item, removeCallback));

                });
            }
            else {
                this.getEl().setInnerHtml("Nothing selected");
            }
        }
    }

    export class SelectionItem extends api_dom.DivEl {

        private browseItem:api_app_browse.BrowseItem;

        constructor(browseItem:BrowseItem, removeCallback?:(selectionItem:SelectionItem) => void) {
            super("ItemsSelectionPanel", "browse-selection-item");
            this.browseItem = browseItem;
            this.setIcon(this.browseItem.getIconUrl(), 32);
            this.setData(this.browseItem.getDisplayName(), this.browseItem.getPath());
            this.addRemoveButton(removeCallback);
        }

        private addRemoveButton(callback?:(SelectionItem) => void) {
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

        getBrowseItem():BrowseItem {
            return this.browseItem;
        }
    }

}
