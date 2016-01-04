module api.ui.security.auth {

    import AuthService = api.security.auth.AuthService;

    export class AuthServiceComboBox extends api.ui.selector.combobox.RichComboBox<AuthService> {
        constructor() {
            var builder = new api.ui.selector.combobox.RichComboBoxBuilder<AuthService>();
            builder.
                setMaximumOccurrences(1).
                setComboBoxName("authServiceSelector").
                setLoader(new AuthServiceLoader()).
                setSelectedOptionsView(new AuthServiceSelectedOptionsView()).
                setOptionDisplayValueViewer(new AuthServiceViewer()).
                setDelayedInputValueChangedHandling(500);
            super(builder);
        }
    }

    export class AuthServiceSelectedOptionsView extends api.ui.selector.combobox.BaseSelectedOptionsView<AuthService> {

        createSelectedOption(option: api.ui.selector.Option<AuthService>): api.ui.selector.combobox.SelectedOption<AuthService> {
            var optionView = new AuthServiceSelectedOptionView(option);
            return new api.ui.selector.combobox.SelectedOption<AuthService>(optionView, this.count());
        }
    }

    export class AuthServiceSelectedOptionView extends api.ui.selector.combobox.RichSelectedOptionView<AuthService> {


        constructor(option: api.ui.selector.Option<AuthService>) {
            super(option);
        }

        resolveIconUrl(content: AuthService): string {
            return api.util.UriHelper.getAdminUri("common/images/icons/icoMoon/32x32/shield.png");
        }

        resolveTitle(content: AuthService): string {
            return content.getDisplayName();
        }

        resolveSubTitle(content: AuthService): string {
            return content.getKey();
        }

    }
}