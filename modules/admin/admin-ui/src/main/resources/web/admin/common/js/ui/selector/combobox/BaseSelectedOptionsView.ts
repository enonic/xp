module api.ui.selector.combobox {

    export class BaseSelectedOptionsView<T> extends api.dom.DivEl implements SelectedOptionsView<T> {

        private list: SelectedOption<T>[] = [];

        private maximumOccurrences: number;

        private selectedOptionRemovedListeners: {(removed: SelectedOption<T>): void;}[] = [];

        constructor(className?: string) {
            super("selected-options" + (className ? " " + className : ""));
        }

        setMaximumOccurrences(value: number) {
            this.maximumOccurrences = value;
        }

        getMaximumOccurrences(): number {
            return this.maximumOccurrences;
        }

        createSelectedOption(option: api.ui.selector.Option<T>): SelectedOption<T> {
            return new SelectedOption<T>(new BaseSelectedOptionView(option), this.count());
        }

        addOption(option: api.ui.selector.Option<T>): boolean {

            if (this.isSelected(option) || this.maximumOccurrencesReached()) {
                return false;
            }

            var selectedOption: SelectedOption<T> = this.createSelectedOption(option);

            selectedOption.getOptionView().onSelectedOptionRemoveRequest(() => {
                this.removeOption(option);
            });

            this.list.push(selectedOption);
            this.appendChild(selectedOption.getOptionView());

            return true;
        }

        removeOption(optionToRemove: api.ui.selector.Option<T>, silent: boolean = false) {
            api.util.assertNotNull(optionToRemove, "optionToRemove cannot be null");

            var selectedOption = this.getByOption(optionToRemove);
            api.util.assertNotNull(selectedOption, "Did not find any selected option to remove from option: " + optionToRemove.value);

            selectedOption.getOptionView().remove();

            this.list = this.list.filter((option: SelectedOption<T>) => {
                return option.getOption().value != selectedOption.getOption().value;
            });

            // update item indexes to the right of removed item
            if (selectedOption.getIndex() < this.list.length) {
                for (var i: number = selectedOption.getIndex(); i < this.list.length; i++) {
                    this.list[i].setIndex(i);
                }
            }

            if (!silent) {
                this.notifySelectedOptionRemoved(selectedOption);
            }
        }

        count(): number {
            return this.list.length;
        }

        getSelectedOptions(): SelectedOption<T>[] {
            return this.list;
        }

        getByIndex(index: number): SelectedOption<T> {
            return this.list[index];
        }

        getByOption(option: api.ui.selector.Option<T>): SelectedOption<T> {
            return this.getById(option.value);
        }

        getById(id: string): SelectedOption<T> {
            return this.list.filter((selectedOption: SelectedOption<T>) => {
                return selectedOption.getOption().value == id;
            })[0];
        }

        isSelected(option: api.ui.selector.Option<T>): boolean {
            return this.getByOption(option) != null;
        }

        maximumOccurrencesReached(): boolean {
            if (this.maximumOccurrences == 0) {
                return false;
            }
            return this.count() >= this.maximumOccurrences;
        }

        moveOccurrence(formIndex: number, toIndex: number) {

            api.util.ArrayHelper.moveElement(formIndex, toIndex, this.list);
            api.util.ArrayHelper.moveElement(formIndex, toIndex, this.getChildren());

            this.list.forEach((selectedOption: SelectedOption<T>, index: number) => selectedOption.setIndex(index));
        }

        private notifySelectedOptionRemoved(removed: SelectedOption<T>) {
            this.selectedOptionRemovedListeners.forEach((listener) => {
                listener(removed);
            });
        }

        onOptionDeselected(listener: {(removed: SelectedOption<T>): void;}) {
            this.selectedOptionRemovedListeners.push(listener);
        }

        unOptionDeselected(listener: {(removed: SelectedOption<T>): void;}) {
            this.selectedOptionRemovedListeners = this.selectedOptionRemovedListeners.filter(function (curr) {
                return curr != listener;
            });
        }
    }
}