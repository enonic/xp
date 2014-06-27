module api.ui.menu {

    export class ContextMenuWithTitle extends api.dom.DivEl {

        private names: api.app.NamesAndIconView;
        private closeBtn: api.ui.Button;
        private menu: ContextMenu;

        private closeListeners: {(event: MouseEvent):void}[] = [];

        constructor(actions?: api.ui.Action[], appendToBody = true) {
            super('context-menu-with-title bottom');

            this.closeBtn = new api.ui.Button();
            this.closeBtn.addClass('close icon-close'); //live-edit-font-icon-close
            this.closeBtn.onClicked((event: MouseEvent) => {
                this.hide();
                this.notifyCloseClicked(event);
                event.preventDefault();
                event.stopPropagation();
            });
            this.appendChild(this.closeBtn);

            this.names = new api.app.NamesAndIconViewBuilder().setSize(api.app.NamesAndIconViewSize.small).build();
            this.appendChild(this.names);

            this.menu = new api.ui.menu.ContextMenu(actions, false).setHideOnItemClick(false);
            this.appendChild(this.menu);

            if (appendToBody) {
                api.dom.Body.get().appendChild(this);
                api.dom.Body.get().onClicked((event: MouseEvent) => this.hideMenuOnOutsideClick(event));
            }

        }

        addAction(action: api.ui.Action) {
            this.menu.addAction(action);
        }

        setName(name: string) {
            this.names.setMainName(name);
        }

        setIconClass(icon: string) {
            this.names.setIconClass(icon);
        }

        showAt(x: number, y: number) {
            this.menu.showAt.call(this, x, y);
        }

        onCloseClicked(listener: (event: MouseEvent) => void) {
            this.closeListeners.push(listener);
        }

        unCloseClicked(listener: (event: MouseEvent) => void) {
            this.closeListeners = this.closeListeners.filter((current: (event: MouseEvent) => void) => {
                return listener !== current;
            });
        }

        private notifyCloseClicked(event: MouseEvent) {
            this.closeListeners.forEach((listener: (event: MouseEvent) => void) => {
                listener(event);
            })
        }

        private hideMenuOnOutsideClick(evt: Event): void {
            var id = this.getId();
            var target: any = evt.target;
            for (var element = target; element; element = element.parentNode) {
                if (element.id === id) {
                    return; // menu clicked
                }
            }
            // click outside menu
            this.hide();
        }
    }

}
