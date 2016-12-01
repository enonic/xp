module api.ui.security.acl {

    import Option = api.ui.selector.Option;
    import SelectedOption = api.ui.selector.combobox.SelectedOption;
    import BaseSelectedOptionView = api.ui.selector.combobox.BaseSelectedOptionView;
    import Permission = api.security.acl.Permission;
    import UserStoreAccessControlEntry = api.security.acl.UserStoreAccessControlEntry;
    import UserStoreAccessControlEntryLoader = api.security.acl.UserStoreAccessControlEntryLoader;
    import SelectedOptionEvent = api.ui.selector.combobox.SelectedOptionEvent;

    export class UserStoreAccessControlComboBox extends api.ui.selector.combobox.RichComboBox<UserStoreAccessControlEntry> {

        private aceSelectedOptionsView: UserStoreACESelectedOptionsView;

        constructor() {
            let aceSelectedOptionsView = new UserStoreACESelectedOptionsView();

            var builder = new api.ui.selector.combobox.RichComboBoxBuilder<UserStoreAccessControlEntry>().
                setMaximumOccurrences(0).
                setComboBoxName("principalSelector").
                setLoader(new UserStoreAccessControlEntryLoader()).
                setSelectedOptionsView(aceSelectedOptionsView).
                setOptionDisplayValueViewer(new UserStoreAccessControlEntryViewer()).
                setDelayedInputValueChangedHandling(500);
            
            super(builder);

            this.aceSelectedOptionsView = aceSelectedOptionsView;
        }

        onOptionValueChanged(listener: (item: UserStoreAccessControlEntry) => void) {
            this.aceSelectedOptionsView.onItemValueChanged(listener);
        }

        unItemValueChanged(listener: (item: UserStoreAccessControlEntry) => void) {
            this.aceSelectedOptionsView.unItemValueChanged(listener);
        }
    }

    class UserStoreACESelectedOptionView extends UserStoreAccessControlEntryView implements api.ui.selector.combobox.SelectedOptionView<UserStoreAccessControlEntry> {

        private option: Option<UserStoreAccessControlEntry>;

        constructor(option: Option<UserStoreAccessControlEntry>) {
            var ace = option.displayValue;
            super(ace);
            this.option = option;
        }

        setOption(option: Option<UserStoreAccessControlEntry>) {
            this.option = option;
            this.setUserStoreAccessControlEntry(option.displayValue);
        }

        getOption(): Option<UserStoreAccessControlEntry> {
            return this.option;
        }

    }

    class UserStoreACESelectedOptionsView extends UserStoreAccessControlListView implements api.ui.selector.combobox.SelectedOptionsView<UserStoreAccessControlEntry> {

        private maximumOccurrences: number;
        private list: SelectedOption<UserStoreAccessControlEntry>[] = [];

        private selectedOptionRemovedListeners: {(removed: SelectedOptionEvent<UserStoreAccessControlEntry>): void;}[] = [];
        private selectedOptionAddedListeners: {(added: SelectedOptionEvent<UserStoreAccessControlEntry>): void;}[] = [];

        constructor(className?: string) {
            super(className);
        }

        setEditable(editable: boolean) {
            this.getSelectedOptions().forEach((option: SelectedOption<UserStoreAccessControlEntry>) => {
                option.getOptionView().setEditable(editable);
            });
        }

        setMaximumOccurrences(value: number) {
            this.maximumOccurrences = value;
        }

        getMaximumOccurrences(): number {
            return this.maximumOccurrences;
        }

        createSelectedOption(option: Option<UserStoreAccessControlEntry>): SelectedOption<UserStoreAccessControlEntry> {
            throw new Error('Not supported, use createItemView instead');
        }

        createItemView(entry: UserStoreAccessControlEntry, readOnly: boolean): UserStoreACESelectedOptionView {

            var option = {
                displayValue: entry,
                value: this.getItemId(entry),
                readOnly: readOnly
            };
            var itemView = new UserStoreACESelectedOptionView(option);
            itemView.onValueChanged((item: UserStoreAccessControlEntry) => {
                // update our selected options list with new values
                var selectedOption = this.getById(item.getPrincipal().getKey().toString());
                if (selectedOption) {
                    selectedOption.getOption().displayValue = item;
                }
                this.notifyItemValueChanged(item);
            });
            var selectedOption = new SelectedOption<UserStoreAccessControlEntry>(itemView, this.list.length);

            itemView.onRemoveClicked(() => this.removeOption(option, false));

            if(readOnly)
            {
                itemView.setEditable(false);
            }

            // keep track of selected options for SelectedOptionsView
            this.list.push(selectedOption);
            return itemView;
        }


        addOption(option: Option<UserStoreAccessControlEntry>, silent: boolean = false, keyCode: number = -1): boolean {
            if(option.readOnly)
            {
                this.addItemReadOnly(option.displayValue)
            }
            else
            {
                this.addItem(option.displayValue);
            }
            if (!silent) {
                var selectedOption = this.getByOption(option);
                this.notifySelectedOptionAdded(new SelectedOptionEvent(selectedOption, keyCode));
            }
            return true;
        }

        removeOption(optionToRemove: Option<UserStoreAccessControlEntry>, silent: boolean = false) {
            api.util.assertNotNull(optionToRemove, "optionToRemove cannot be null");

            var selectedOption = this.getByOption(optionToRemove);
            api.util.assertNotNull(selectedOption, "Did not find any selected option to remove from option: " + optionToRemove.value);

            this.removeItem(optionToRemove.displayValue);

            this.list = this.list.filter((option: SelectedOption<UserStoreAccessControlEntry>) => {
                return option.getOption().value != selectedOption.getOption().value;
            });

            // update item indexes to the right of removed item
            if (selectedOption.getIndex() < this.list.length) {
                for (var i: number = selectedOption.getIndex(); i < this.list.length; i++) {
                    this.list[i].setIndex(i);
                }
            }

            if (!silent) {
                this.notifySelectedOptionRemoved(new SelectedOptionEvent(selectedOption));
            }
        }

        count(): number {
            return this.list.length;
        }

        getSelectedOptions(): SelectedOption<UserStoreAccessControlEntry>[] {
            return this.list;
        }

        getByIndex(index: number): SelectedOption<UserStoreAccessControlEntry> {
            return this.list[index];
        }

        getByOption(option: Option<UserStoreAccessControlEntry>): SelectedOption<UserStoreAccessControlEntry> {
            return this.getById(option.value);
        }

        getById(id: string): SelectedOption<UserStoreAccessControlEntry> {
            return this.list.filter((selectedOption: SelectedOption<UserStoreAccessControlEntry>) => {
                return selectedOption.getOption().value == id;
            })[0];
        }

        isSelected(option: Option<UserStoreAccessControlEntry>): boolean {
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

            this.list.forEach((selectedOption: SelectedOption<UserStoreAccessControlEntry>,
                               index: number) => selectedOption.setIndex(index));
        }

        private notifySelectedOptionRemoved(removed: SelectedOptionEvent<UserStoreAccessControlEntry>) {
            this.selectedOptionRemovedListeners.forEach((listener) => {
                listener(removed);
            });
        }

        onOptionDeselected(listener: {(removed: SelectedOptionEvent<UserStoreAccessControlEntry>): void;}) {
            this.selectedOptionRemovedListeners.push(listener);
        }

        unOptionDeselected(listener: {(removed: SelectedOptionEvent<UserStoreAccessControlEntry>): void;}) {
            this.selectedOptionRemovedListeners = this.selectedOptionRemovedListeners.filter(function (curr) {
                return curr != listener;
            });
        }

        onOptionSelected(listener: {(added: SelectedOptionEvent<UserStoreAccessControlEntry>): void;}) {
            this.selectedOptionAddedListeners.push(listener);
        }

        unOptionSelected(listener: {(added: SelectedOptionEvent<UserStoreAccessControlEntry>): void;}) {
            this.selectedOptionAddedListeners = this.selectedOptionAddedListeners.filter(function (curr) {
                return curr != listener;
            });
        }

        private notifySelectedOptionAdded(added: SelectedOptionEvent<UserStoreAccessControlEntry>) {
            this.selectedOptionAddedListeners.forEach((listener) => {
                listener(added);
            });
        }

        onOptionMoved(listener: {(moved: SelectedOption<UserStoreAccessControlEntry>): void;}) {
        }

        unOptionMoved(listener: {(moved: SelectedOption<UserStoreAccessControlEntry>): void;}) {
        }

    }

}