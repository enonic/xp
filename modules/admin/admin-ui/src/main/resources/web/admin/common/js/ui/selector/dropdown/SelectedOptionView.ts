module api.ui.selector.dropdown {

    import DropdownHandle = api.ui.selector.DropdownHandle;

    export class SelectedOptionView<T> extends api.dom.ButtonEl {

        private objectViewer:Viewer<T>;

        private dropdownHandle: DropdownHandle;

        private optionValueEl: api.dom.DivEl;

        private option: api.ui.selector.Option<T>;

        private openDropdownListeners: {(): void;}[] = [];

        constructor(objectViewer:Viewer<T>) {
            super("selected-option");
            this.objectViewer = objectViewer;
            this.dropdownHandle = new DropdownHandle();
            this.optionValueEl = new api.dom.DivEl('option-value');
            this.appendChild(this.optionValueEl);
            this.optionValueEl.appendChild(this.objectViewer);
            this.appendChild(this.dropdownHandle);

            this.dropdownHandle.onClicked(() => {
                this.notifyOpenDropdown();
            } );

            this.onClicked((event: MouseEvent)=> {

                if (document["selection"] && document["selection"].empty) {
                    document["selection"].empty();
                } else if(window.getSelection) {
                    var sel = window.getSelection();
                    sel.removeAllRanges();
                }

                this.notifyOpenDropdown();
            });

            this.onKeyPressed((event:KeyboardEvent) => {
                if (event.which == 32 || event.which == 13) { // space or enter
                    this.notifyOpenDropdown();
                }
            });
        }

        setOption(option: api.ui.selector.Option<T>) {
            this.option = option;
            this.objectViewer.setObject(option.displayValue);
        }

        getOption(): api.ui.selector.Option<T> {
            return this.option;
        }

        private notifyOpenDropdown() {
            this.openDropdownListeners.forEach((listener) => {
                listener();
            });
        }

        resetOption() {
            this.option = null;
        }

        onOpenDropdown(listener: {(): void;}) {
            this.openDropdownListeners.push(listener);
        }

        unOpenDropdown(listener: {(): void;}) {
            this.openDropdownListeners = this.openDropdownListeners.filter(function (curr) {
                return curr != listener;
            });
        }
    }
}