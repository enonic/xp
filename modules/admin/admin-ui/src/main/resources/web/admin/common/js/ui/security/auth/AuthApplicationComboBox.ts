module api.ui.security.auth {

    export class AuthApplicationComboBox extends api.ui.selector.combobox.RichComboBox<api.application.Application> {
        constructor() {
            var builder = new api.ui.selector.combobox.RichComboBoxBuilder<api.application.Application>();
            builder.
                setMaximumOccurrences(1).
                setComboBoxName("authApplicationSelector").
                setLoader(new AuthApplicationLoader()).
                setSelectedOptionsView(new AuthApplicationSelectedOptionsView()).
                setOptionDisplayValueViewer(new AuthApplicationViewer()).
                setDelayedInputValueChangedHandling(500);
            super(builder);
        }
    }

    export class AuthApplicationSelectedOptionsView extends api.ui.selector.combobox.BaseSelectedOptionsView<api.application.Application> {

        createSelectedOption(option: api.ui.selector.Option<api.application.Application>): api.ui.selector.combobox.SelectedOption<api.application.Application> {
            var optionView = new AuthApplicationSelectedOptionView(option);
            return new api.ui.selector.combobox.SelectedOption<api.application.Application>(optionView, this.count());
        }
    }

    export class AuthApplicationSelectedOptionView extends api.ui.selector.combobox.RichSelectedOptionView<api.application.Application> {


        constructor(option: api.ui.selector.Option<api.application.Application>) {
            super(option);
        }

        resolveIconUrl(content: api.application.Application): string {
            return api.util.UriHelper.getAdminUri("common/images/icons/icoMoon/32x32/shield.png");
        }

        resolveTitle(content: api.application.Application): string {
            return content.getDisplayName();
        }

        resolveSubTitle(content: api.application.Application): string {
            return content.getApplicationKey().toString();
        }

    }
}