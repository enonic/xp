module API_ui_menu{

    export class ContextMenu extends API_ui.Component {
        ext; //:Ext.Component;

        private menuItems:MenuItem[] = [];

        constructor() {
            super("context-menu", "ul");
            this.getEl().addClass("context-menu");
            this.initExt();
        }

        private initExt() {
            var htmlEl = this.getHTMLElement();
            this.ext = new Ext.Component({
                contentEl: htmlEl,
                region: 'north'
            });
            // add Floating mixin so that later call to showAt() works properly
            this.ext.self.mixin('floating', Ext.util.Floating);
            this.ext.mixins.floating.constructor.call(this.ext);
        }

        addAction(action:API_action.Action) {
            var menuItem = this.createMenuItem(action);
            this.appendChild(menuItem);
        }

        private createMenuItem(action:API_action.Action):MenuItem {
            var menuItem = new MenuItem(this, action);
            this.menuItems.push(menuItem);
            return menuItem;
        }

        showAt(x:number, y:number) {
            this.ext.showAt(x, y);
        }
    }

    class MenuItem extends API_ui.Component {

        private menu:API_ui_menu.ContextMenu;
        private action:API_action.Action;

        constructor(parent:API_ui_menu.ContextMenu, action:API_action.Action) {
            super("menu-item", "li");
            this.action = action;
            this.menu = parent;
            this.getEl().setInnerHtml(this.action.getLabel());
            this.getEl().addEventListener("click", () => {
                this.action.execute();
                this.menu.ext.hide();
            });
            this.setEnable(action.isEnabled());

            action.addPropertyChangeListener((action:API_action.Action) => {
                this.setEnable(action.isEnabled());
            });
        }

        setEnable(value:bool) {
            this.getEl().setDisabled(!value);
        }
    }

}
