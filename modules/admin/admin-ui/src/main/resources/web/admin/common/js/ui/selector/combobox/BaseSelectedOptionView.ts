module api.ui.selector.combobox {

    export class BaseSelectedOptionView<T> extends api.dom.DivEl implements SelectedOptionView<T> {

        private option: api.ui.selector.Option<T>;

        private optionValueEl: api.dom.DivEl;

        private removeClickedListeners: {(): void;}[] = [];

        private editable: boolean = true;

        constructor(option: api.ui.selector.Option<T>) {
            super('selected-option');

            this.option = option;
        }

        setOption(option: api.ui.selector.Option<T>) {
            if (this.optionValueEl) {
                this.optionValueEl.getEl().setInnerHtml(option.displayValue.toString());
            }
        }

        getOption(): api.ui.selector.Option<T> {
            return this.option;
        }

        doRender(): wemQ.Promise<boolean> {

            let removeButtonEl = new api.dom.AEl('remove');
            this.optionValueEl = new api.dom.DivEl('option-value');
            if (this.option) {
                this.setOption(this.option);
            }

            removeButtonEl.onClicked((event: Event) => {
                this.notifyRemoveClicked();

                event.stopPropagation();
                event.preventDefault();
                return false;
            });

            this.appendChildren<api.dom.Element>(removeButtonEl, this.optionValueEl);

            return wemQ(true);
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
            this.removeClickedListeners = this.removeClickedListeners.filter(function (curr: {(): void;}) {
                return curr !== listener;
            });
        }

        setEditable(editable: boolean) {
            this.editable = editable;
            this.toggleClass('readonly', !editable);
        }

        isEditable(): boolean {
            return this.editable;
        }
    }
}
