module api.ui {

    export class Dropdown extends api.dom.SelectEl {

        constructor(name: string) {
            super();
            this.setName(name);

            this.onChange((event: Event) => {
                this.refreshDirtyState();
                this.refreshValueChanged();
            });
        }

        addOption(value: string, displayName: string) {
            let option = new DropdownOption(value, displayName);
            this.appendChild(option);
        }
    }


    export class DropdownOption extends api.dom.OptionEl {
        constructor(value: string, displayName: string) {
            super(value, displayName);
        }
    }
}