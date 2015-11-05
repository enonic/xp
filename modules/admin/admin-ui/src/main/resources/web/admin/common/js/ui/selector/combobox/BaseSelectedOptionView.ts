module api.ui.selector.combobox {

    export class BaseSelectedOptionView<T> extends api.dom.DivEl implements SelectedOptionView<T> {

        private option: api.ui.selector.Option<T>;

        private optionValueEl: api.dom.DivEl;

        private removeClickedListeners: {(): void;}[] = [];

        constructor(option: api.ui.selector.Option<T>) {
            super("selected-option");
            this.layout();
            this.setOption(option);
        }

        setOption(option: api.ui.selector.Option<T>) {
            this.option = option;
            if (this.optionValueEl) {
                this.optionValueEl.getEl().setInnerHtml(this.option.displayValue.toString());
            }
        }

        getOption(): api.ui.selector.Option<T> {
            return this.option;
        }

        layout() {
            var removeButtonEl = new api.dom.AEl("remove");
            this.optionValueEl = new api.dom.DivEl('option-value');

            removeButtonEl.onClicked((event: Event) => {
                this.notifyRemoveClicked();

                event.stopPropagation();
                event.preventDefault();
                return false;
            });

            this.appendChild(removeButtonEl);
            this.appendChild(this.optionValueEl);
        }

        protected notifyRemoveClicked() {
            this.removeClickedListeners.forEach((listener) => {
                listener();
            });
        }

        onRemoveClicked(listener: {(): void;}) {
            this.removeClickedListeners.push(listener);
        }

        unRemoveClicked(listener: {(): void;}) {
            this.removeClickedListeners = this.removeClickedListeners.filter(function (curr) {
                return curr != listener;
            });
        }
    }
}