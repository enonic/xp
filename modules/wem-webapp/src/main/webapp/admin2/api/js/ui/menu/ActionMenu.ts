module api_ui_menu{

    export class ActionMenu extends api_ui.Component {
        private ext; //:Ext.Component;

        private button:ActionMenuButton;

        private menuItems:MenuItem[] = [];

        constructor(...actions:api_action.Action[]) {
            super("action-menu", "ul");
            this.getEl().addClass("action-menu");

            this.button = new ActionMenuButton(this);

            for (var i = 0; i < actions.length; i++) {
                this.addAction(actions[i]);
            }

            window.document.addEventListener("click", (evt:Event) => {
                this.onDocumentClick(evt);
            });

            this.initExt();
        }

        addAction(action:api_action.Action) {
            var menuItem = this.createMenuItem(action);
            this.appendChild(menuItem);
        }

        getExt() {
            return this.button.getExt();
        }

        showBy(button:ActionMenuButton) {
            this.ext.show();
            this.ext.getEl().alignTo(button.getExt().getEl(), 'tl-bl?', [-2, 0]);
        }

        private initExt() {
            var htmlEl = this.getHTMLElement();
            this.ext = new Ext.Component({
                contentEl: htmlEl
            });

            // add Floating mixin so that later call to showAt() works properly
            this.ext.self.mixin('floating', Ext.util.Floating);
            this.ext.mixins.floating.constructor.call(this.ext);
        }

        private createMenuItem(action:api_action.Action):MenuItem {
            var menuItem = new MenuItem(action);
            menuItem.getEl().addEventListener("click", (evt:Event) => {
                this.hide();
            });
            this.menuItems.push(menuItem);
            return menuItem;
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


    export class ActionMenuButton extends api_ui.Component {
        private ext;

        private menu:ActionMenu;

        constructor(menu:ActionMenu) {
            super("button", "button");
            this.menu = menu;

            var btnEl = this.getEl();
            btnEl.addClass("action-menu-button");

            var em = api_ui.HTMLElementHelper.fromName('em');
            em.setInnerHtml("Actions");
            btnEl.appendChild(em.getHTMLElement());

            btnEl.addEventListener("click", (e) => {
                menu.showBy(this);

                // stop event to prevent menu close because of body click
                if(e.stopPropagation) {
                    e.stopPropagation();
                }
                e.cancelBubble = true;
            });

            this.initExt();
        }

        setEnabled(value:bool) {
            this.getEl().setDisabled(!value);
        }

        getExt() {
            return this.ext;
        }

        private initExt() {
            var htmlEl = this.getHTMLElement();
            this.ext = new Ext.Component({
                contentEl: htmlEl
            });
        }
    }

}
