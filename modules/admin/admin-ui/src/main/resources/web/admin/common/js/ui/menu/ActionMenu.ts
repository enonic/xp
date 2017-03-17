module api.ui.menu {

    export class ActionMenu extends api.dom.DivEl {

        private actionListEl: api.dom.UlEl;

        private labelEl: api.dom.DivEl;

        constructor(label: string, ...actions: Action[]) {
            super('action-menu');
            this.labelEl = new api.dom.DivEl('drop-down-button icon-arrow-down');
            this.labelEl.setHtml(label);
            this.appendChild(this.labelEl);

            this.actionListEl = new api.dom.UlEl();
            this.appendChild(this.actionListEl);

            if (actions.length > 0) {
                actions.forEach((action: Action) => {
                    this.addAction(action);
                });
            }

            this.labelEl.onClicked((event) => {
                if (this.hasClass('expanded')) {
                    this.removeClass('expanded');
                } else {
                    this.addClass('expanded');
                }
            });

            api.dom.Body.get().onClicked((event: MouseEvent) => this.foldMenuOnOutsideClick(event));
        }

        setLabel(label: string) {
            this.labelEl.getEl().setInnerHtml(label);
        }

        addAction(action: Action) {
            let actionMenuItem = new ActionMenuItem(action);
            this.actionListEl.appendChild(actionMenuItem);
            actionMenuItem.onClicked(() => {
                this.removeClass('expanded');
            });
        }

        private foldMenuOnOutsideClick(evt: Event): void {
            if (!this.getEl().contains(<HTMLElement> evt.target)) {
                // click outside menu
                this.removeClass('expanded');
            }
        }
    }
}
