module app.create {

    export class MostPopularItemsList extends NewContentDialogList {

        constructor() {
            super();
        }

        createItemView(item: MostPopularItem): api.dom.LiEl {
            var namesAndIconView = new api.app.NamesAndIconViewBuilder().setSize(api.app.NamesAndIconViewSize.small).build();
            namesAndIconView
                .setIconUrl(item.getIconUrl())
                .setMainName(item.getDisplayName() + " (" + item.getHits() + ")")
                .setSubName(item.getName())
                .setDisplayIconLabel(item.isSite());

            var itemEl = new api.dom.LiEl('content-types-list-item' + (item.isSite() ? ' site' : ''));
            itemEl.getEl().setTabIndex(0);
            itemEl.appendChild(namesAndIconView);
            itemEl.onClicked((event: MouseEvent) => this.notifySelected(item));
            itemEl.onKeyPressed((event: KeyboardEvent) => {
                if (event.keyCode == 13) {
                    this.notifySelected(item);
                }
            });
            return itemEl;
        }

    }
}