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

    export class UserStoreSelectedOptionView extends api.ui.selector.combobox.RichSelectedOptionView<UserStore> {


        constructor(option: api.ui.selector.Option<UserStore>) {
            super(option);
        }

        resolveIconClass(userStore: api.security.UserStore): string {
            return "icon-shield";
        }

        resolveTitle(userStore: UserStore): string {
            return userStore.getDisplayName();
        }

        resolveSubTitle(userStore: UserStore): string {
            return userStore.getKey().toString();
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