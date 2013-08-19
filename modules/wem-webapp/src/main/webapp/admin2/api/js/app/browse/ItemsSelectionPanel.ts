module api_app_browse {

    export interface ItemSelectionPanelListener extends api_ui.Listener {

        onDeselected(item:BrowseItem);

    }

    export class ItemsSelectionPanel extends api_ui.Panel implements api_ui.Observable {

        private listeners:ItemSelectionPanelListener[] = [];
        private items:BrowseItem[] = [];

        constructor() {
            super("ItemsSelectionPanel");
        }

        setItems(items:BrowseItem[]) {
            this.removeChildren();
            this.items = [];
            if (items.length > 0) {
                items.forEach((item:BrowseItem) => {
                    this.addItem(item);
                });
            } else {
                this.getEl().setInnerHtml("Nothing selected");
            }
        }

        getItems():BrowseItem[] {
            return this.items;
        }

        private removeItem(selectionItem:SelectionItem) {
            var index = this.items.indexOf(selectionItem.getBrowseItem());
            if (index >= 0 && this.items.splice(index, 1)) {
                selectionItem.remove();
                return true;
            } else {
                return false;
            }
        }

        private addItem(item:BrowseItem) {
            var removeCallback = (selectionItem:SelectionItem) => {
                if (this.removeItem(selectionItem)) {
                    this.notifyDeselected(selectionItem.getBrowseItem());
                }
            };

            this.appendChild(new SelectionItem(item, removeCallback));
            this.items.push(item);
        }

        addListener(listener:ItemSelectionPanelListener) {
            this.listeners.push(listener);
        }

        removeListener(listener:ItemSelectionPanelListener) {
            this.listeners = this.listeners.filter(function (curr) {
                return curr != listener;
            });
        }

        private notifyDeselected(item:BrowseItem) {
            this.listeners.forEach((listener:ItemSelectionPanelListener) => {
                if (listener.onDeselected) {
                    listener.onDeselected(item);
                }
            });
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
