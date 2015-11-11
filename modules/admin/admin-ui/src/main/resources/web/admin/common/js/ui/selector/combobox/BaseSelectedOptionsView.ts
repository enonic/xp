module api.ui.selector.combobox {

    import Value = api.data.Value;
    import ValueTypes = api.data.ValueTypes;

    export class BaseSelectedOptionsView<T> extends api.dom.DivEl implements SelectedOptionsView<T> {

        private list: SelectedOption<T>[] = [];

        private draggingIndex: number;

        private beforeDragStartedHeight: number;

        private maximumOccurrences: number;

        private optionRemovedListeners: {(removed: SelectedOption<T>): void;}[] = [];

        private optionAddedListeners: {(added: SelectedOption<T>): void;}[] = [];

        private optionMovedListeners: {(moved: SelectedOption<T>) : void}[] = [];

        constructor(className?: string) {
            super("selected-options" + (className ? " " + className : ""));
        }

        setOccurrencesSortable(sortable: boolean) {
            if (this.isRendered()) {
                this.setSortable(sortable);
            } else {
                this.onRendered(() => this.setSortable(sortable));
            }
            this.toggleClass('sortable', sortable);
        }

        private setSortable(sortable: boolean) {
            console.log('setSortable', sortable);
            if (sortable) {
                wemjq(this.getHTMLElement()).sortable({
                    cursor: 'move',
                    tolerance: 'pointer',
                    placeholder: 'selected-option placeholder',
                    start: (event: Event, ui: JQueryUI.SortableUIParams) => this.handleDnDStart(event, ui),
                    update: (event: Event, ui: JQueryUI.SortableUIParams) => this.handleDnDUpdate(event, ui)
                });
            } else {
                wemjq(this.getHtml()).sortable('destroy');
            }
        }


        private handleDnDStart(event: Event, ui: JQueryUI.SortableUIParams): void {
            this.beforeDragStartedHeight = this.getEl().getHeight();

            var draggedElement = api.dom.Element.fromHtmlElement(<HTMLElement>ui.item.context);
            this.draggingIndex = draggedElement.getSiblingIndex();
        }

        private handleDnDUpdate(event: Event, ui: JQueryUI.SortableUIParams) {

            if (this.draggingIndex >= 0) {
                var draggedElement = api.dom.Element.fromHtmlElement(<HTMLElement>ui.item.context);
                var draggedToIndex = draggedElement.getSiblingIndex();
                this.handleMovedOccurrence(this.draggingIndex, draggedToIndex);
            }

            this.draggingIndex = -1;
        }

        private handleMovedOccurrence(fromIndex: number, toIndex: number) {

            this.moveOccurrence(fromIndex, toIndex);

            this.getSelectedOptions().forEach((option: SelectedOption<T>, index: number) => {
                if (Math.min(fromIndex, toIndex) <= index && index <= Math.max(fromIndex, toIndex)) {
                    this.notifyOptionMoved(option);
                }
            });
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

        addOption(option: api.ui.selector.Option<T>, silent: boolean = false): boolean {

            if (this.isSelected(option) || this.maximumOccurrencesReached()) {
                return false;
            }

            var selectedOption: SelectedOption<T> = this.createSelectedOption(option);

            selectedOption.getOptionView().onRemoveClicked(() => this.removeOption(option));

            this.getSelectedOptions().push(selectedOption);

            this.appendChild(selectedOption.getOptionView());

            if (!silent) {
                this.notifyOptionSelected(selectedOption);
            }

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
                this.notifyOptionDeselected(selectedOption);
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

        moveOccurrence(fromIndex: number, toIndex: number) {

            api.util.ArrayHelper.moveElement(fromIndex, toIndex, this.list);
            api.util.ArrayHelper.moveElement(fromIndex, toIndex, this.getChildren());

            this.list.forEach((selectedOption: SelectedOption<T>, index: number) => selectedOption.setIndex(index));
        }

        protected notifyOptionDeselected(removed: SelectedOption<T>) {
            this.optionRemovedListeners.forEach((listener) => {
                listener(removed);
            });
        }

        onOptionDeselected(listener: {(removed: SelectedOption<T>): void;}) {
            this.optionRemovedListeners.push(listener);
        }

        unOptionDeselected(listener: {(removed: SelectedOption<T>): void;}) {
            this.optionRemovedListeners = this.optionRemovedListeners.filter(function (curr) {
                return curr != listener;
            });
        }

        onOptionSelected(listener: (added: SelectedOption<T>)=>void) {
            this.optionAddedListeners.push(listener);
        }

        unOptionSelected(listener: (added: SelectedOption<T>)=>void) {
            this.optionAddedListeners = this.optionAddedListeners.filter((current: (added: SelectedOption<T>)=>void) => {
                return listener != current;
            });
        }

        protected notifyOptionSelected(added: SelectedOption<T>) {
            this.optionAddedListeners.forEach((listener: (added: SelectedOption<T>)=>void) => {
                listener(added);
            });
        }

        onOptionMoved(listener: (moved: SelectedOption<T>) => void) {
            this.optionMovedListeners.push(listener);
        }

        unOptionMoved(listener: (moved: SelectedOption<T>) => void) {
            this.optionMovedListeners =
                this.optionMovedListeners.filter((current: (option: SelectedOption<T>)=>void) => {
                    return listener != current;
                });
        }

        protected notifyOptionMoved(moved: SelectedOption<T>) {
            this.optionMovedListeners.forEach((listener: (option: SelectedOption<T>) => void) => {
                listener(moved);
            });
        }
    }
}