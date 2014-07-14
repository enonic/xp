module api.liveedit {

    import PageComponent = api.content.page.PageComponent;

    export class ItemViewContextMenu extends api.dom.DivEl {

        private itemView: ItemView;

        private names: api.app.NamesAndIconView;
        private menu: api.ui.menu.ContextMenu;

        constructor(itemView: ItemView, actions: api.ui.Action[]) {
            super('item-view-context-menu bottom');

            this.itemView = itemView;


            this.names = new api.app.NamesAndIconViewBuilder().setAddTitleAttribute(false).build();
            this.names.setMainName(this.getMainName());
            this.names.setIconClass(itemView.getType().getConfig().getIconCls());
            this.appendChild(this.names);

            this.menu = new api.ui.menu.ContextMenu(actions, false).setHideOnItemClick(false);
            this.appendChild(this.menu);

            api.dom.Body.get().appendChild(this);
        }

        showAt(x: number, y: number) {
            this.names.setMainName(this.getMainName());
            this.menu.showAt.call(this, x, y);
        }

        private getMainName():string {
            return this.itemView.getName();
        }
    }

}
