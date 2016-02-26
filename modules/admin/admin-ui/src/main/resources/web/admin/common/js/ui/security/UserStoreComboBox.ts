module api.ui.security {

    import Option = api.ui.selector.Option;
    import UserStore = api.security.UserStore;
    import SelectedOption = api.ui.selector.combobox.SelectedOption;

    export class UserStoreComboBox extends api.ui.selector.combobox.RichComboBox<UserStore> {
        constructor(maxOccurrences?: number) {
            var builder = new api.ui.selector.combobox.RichComboBoxBuilder<UserStore>().
                setMaximumOccurrences(maxOccurrences || 0).
                setComboBoxName("userStoreSelector").
                setIdentifierMethod("getKey").
                setLoader(new api.security.UserStoreLoader()).
                setSelectedOptionsView(new UserStoreSelectedOptionsView()).
                setOptionDisplayValueViewer(new UserStoreViewer()).
                setDelayedInputValueChangedHandling(500);
            super(builder);
        }
    }

    class UserStoreSelectedOptionView extends UserStoreViewer implements api.ui.selector.combobox.SelectedOptionView<UserStore> {

        private option: Option<UserStore>;

        constructor(option: Option<UserStore>) {
            super();
            this.setOption(option);
            this.setClass("userstore-selected-option-view");
            var removeButton = new api.dom.AEl("icon-close");
            removeButton.onClicked((event: MouseEvent) => {
                this.notifyRemoveClicked(event);
                event.stopPropagation();
                event.preventDefault();
                return false;
            });
            this.appendChild(removeButton);
        }

        setOption(option: api.ui.selector.Option<UserStore>) {
            this.option = option;
            this.setObject(option.displayValue);
        }

        getOption(): api.ui.selector.Option<UserStore> {
            return this.option;
        }

    }

    class UserStoreSelectedOptionsView extends api.ui.selector.combobox.BaseSelectedOptionsView<UserStore> {

        constructor() {
            super("userstore-selected-options-view");
        }

        createSelectedOption(option: Option<UserStore>): SelectedOption<UserStore> {
            var optionView = new UserStoreSelectedOptionView(option);
            return new api.ui.selector.combobox.SelectedOption<UserStore>(optionView, this.count());
        }

    }

}