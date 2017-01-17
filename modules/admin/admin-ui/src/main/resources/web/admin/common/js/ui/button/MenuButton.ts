module api.ui.button {

    import Element = api.dom.Element;
    import Menu = api.ui.menu.Menu;
    import MenuItem = api.ui.menu.MenuItem;

    export class MenuButton extends api.dom.DivEl {

        private dropdownHandle: DropdownHandle;

        private actionButton: ActionButton;

        private menu: Menu;

        constructor(mainAction: Action, menuActions: Action[] = []) {
            super('menu-button');

            this.initDropdownHandle();
            this.initActionButton(mainAction);
            this.initMenu(menuActions);

            this.initListeners();

            let children = [this.dropdownHandle, this.actionButton, this.menu];
            this.appendChildren(...children);
        }

        private initDropdownHandle() {
            this.dropdownHandle = new DropdownHandle();
        }

        private initActionButton(action: Action) {
            this.actionButton = new ActionButton(action);
        }

        private initMenu(actions: Action[]) {
            this.menu = new Menu(actions);
            this.setDropdownHandleEnabled(actions.length > 0);

            this.updateActionEnabled();

            this.getMenuActions().forEach((action) => {
                action.onPropertyChanged(this.updateActionEnabled.bind(this));
            });
        }

        private getMenuActions() {
            return this.menu.getMenuItems().map(item => item.getAction());
        }

        private updateActionEnabled() {
            let allActionsDisabled = this.getMenuActions().every((action) => !action.isEnabled());
            this.setDropdownHandleEnabled(!allActionsDisabled);
        }

        private initListeners() {
            let hideMenu = this.hideMenu.bind(this);

            this.dropdownHandle.onClicked(() => {
                if (this.dropdownHandle.isEnabled()) {
                    this.menu.toggleClass('expanded');
                    this.dropdownHandle.toggleClass('down');
                }
            });

            this.menu.onItemClicked((item: MenuItem) => {
                if (this.menu.isHideOnItemClick() && item.isEnabled()) {
                    hideMenu();
                }
            });

            this.actionButton.onClicked(hideMenu);

            this.dropdownHandle.onClicked(() => this.dropdownHandle.giveFocus());

            this.menu.onClicked(() => this.dropdownHandle.giveFocus());

            api.util.AppHelper.focusInOut(this, hideMenu);
        }

        private hideMenu(event: MouseEvent): void {
            this.menu.removeClass('expanded');
            this.dropdownHandle.removeClass('down');
        }

        setDropdownHandleEnabled(enabled: boolean = true) {
            this.dropdownHandle.setEnabled(enabled);
            if (!enabled) {
                this.menu.removeClass('expanded');
                this.dropdownHandle.removeClass('down');
            }
        }

        hideDropdown(hidden: boolean = true) {
            this.toggleClass('hidden-dropdown', hidden);
        }

        minimize() {
            if (!this.hasClass('minimized')) {
                const action = this.actionButton.getAction();
                const actions = [action, ...this.getMenuActions()];
                this.menu.setActions(actions);
                action.onPropertyChanged(this.updateActionEnabled.bind(this));
                this.addClass('minimized');
                this.updateActionEnabled();
            }
        }

        maximize() {
            if (this.hasClass('minimized')) {
                const action = this.actionButton.getAction();
                this.menu.removeAction(action);
                action.unPropertyChanged(this.updateActionEnabled.bind(this));
                this.removeClass('minimized');
                this.updateActionEnabled();
            }
        }
    }
}
