module api_ui_menu{

    export class ContextMenu extends api_ui.Component {
        ext; //:Ext.Component;

        private menuItems:MenuItem[] = [];

        constructor() {
            super("context-menu", "ul");
            this.getEl().addClass("context-menu");
            this.initExt();

            window.document.addEventListener("click", (evt:Event) => {
                this.onDocumentClick(evt);
            });
        }

        private initExt() {
            var htmlEl = this.getHTMLElement();
            this.ext = new Ext.Component({
                contentEl: htmlEl,
                region: 'north',
                shadow: false
            });
            // add Floating mixin so that later call to showAt() works properly
            this.ext.self.mixin('floating', Ext.util.Floating);
            this.ext.mixins.floating.constructor.call(this.ext);
        }

        addAction(action:api_action.Action) {
            var menuItem = this.createMenuItem(action);
            this.appendChild(menuItem);
        }

        private createMenuItem(action:api_action.Action):MenuItem {
            var menuItem = new MenuItem(this, action);
            this.menuItems.push(menuItem);
            return menuItem;
        }

        showAt(x:number, y:number) {
            this.ext.showAt(x, y);
        }

        private hide() {
            this.ext.hide();
        }

        private onDocumentClick(evt:Event):void {
            var id = this.getId();
            var target:any = evt.target;
            for (var element = target; element; element = element.parentNode) {
                if (element.id === id) {
                    return; // menu clicked
                }
            }

            // click outside menu
            this.hide();
        }
    }

    class MenuItem extends api_ui.Component {

        private menu:api_ui_menu.ContextMenu;
        private action:api_action.Action;

        constructor(parent:api_ui_menu.ContextMenu, action:api_action.Action) {
            super("menu-item", "li");
            this.action = action;
            this.menu = parent;
            this.getEl().setInnerHtml(this.action.getLabel());
            this.getEl().addEventListener("click", () => {
                if (action.isEnabled()) {
                    this.action.execute();
                    this.menu.ext.hide();
                }
            });
            this.setEnable(action.isEnabled());

            action.addPropertyChangeListener((action:api_action.Action) => {
                this.setEnable(action.isEnabled());
            });
        }

        setEnable(value:bool) {
            var el = this.getEl();
            el.setDisabled(!value);
            if (value) {
                el.removeClass("context-menu-item-disabled");
            } else {
                el.addClass("context-menu-item-disabled");
            }
        }
    }

}
