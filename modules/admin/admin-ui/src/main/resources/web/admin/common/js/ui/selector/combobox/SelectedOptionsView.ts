module api.ui.selector.combobox {

    export interface SelectedOptionsView<T> extends api.dom.DivEl {

        setMaximumOccurrences(value: number);

        getMaximumOccurrences(): number;

        createSelectedOption(option: api.ui.selector.Option<T>): SelectedOption<T>;

        addOption(option: api.ui.selector.Option<T>, silent: boolean): boolean;

        removeOption(optionToRemove: api.ui.selector.Option<T>, silent: boolean);

        count(): number;

        getSelectedOptions(): SelectedOption<T>[];

        getByOption(option: api.ui.selector.Option<T>): SelectedOption<T>;

        getById(id: string): SelectedOption<T>;

        getByIndex(index: number): SelectedOption<T>;

        isSelected(option: api.ui.selector.Option<T>): boolean;

        maximumOccurrencesReached(): boolean;

        moveOccurrence(formIndex: number, toIndex: number);

        onOptionSelected(listener: {(added: SelectedOption<T>): void;});

        unOptionSelected(listener: {(added: SelectedOption<T>): void;});

        onOptionDeselected(listener: {(removed: SelectedOption<T>): void;});

        unOptionDeselected(listener: {(removed: SelectedOption<T>): void;});

        onOptionMoved(listener: (moved: SelectedOption<T>) => void);

        unOptionMoved(listener: (moved: SelectedOption<T>) => void);
    }
}