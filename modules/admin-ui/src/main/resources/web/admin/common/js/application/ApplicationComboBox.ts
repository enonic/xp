module api.application {

    export class ApplicationComboBox extends api.ui.selector.combobox.RichComboBox<api.application.Application> {
        constructor(maximumOccurrences?: number) {
            var builder = new api.ui.selector.combobox.RichComboBoxBuilder<api.application.Application>();
            builder.
                setMaximumOccurrences(maximumOccurrences || 0).
                setComboBoxName("applicationSelector").
                setLoader(new api.application.ApplicationLoader()).
                setSelectedOptionsView(new ApplicationSelectedOptionsView()).
                setOptionDisplayValueViewer(new ApplicationViewer()).
                setDelayedInputValueChangedHandling(500);
            super(builder);
        }
    }

    export class ApplicationSelectedOptionsView extends api.ui.selector.combobox.BaseSelectedOptionsView<api.application.Application> {

        createSelectedOption(option: api.ui.selector.Option<api.application.Application>): api.ui.selector.combobox.SelectedOption<api.application.Application> {
            var optionView = new ApplicationSelectedOptionView(option);
            return new api.ui.selector.combobox.SelectedOption<api.application.Application>(optionView, this.count());
        }
    }

    export class ApplicationSelectedOptionView extends api.ui.selector.combobox.RichSelectedOptionView<api.application.Application> {


        constructor(option: api.ui.selector.Option<api.application.Application>) {
            super(option);
        }

        resolveIconUrl(content: api.application.Application): string {
            return api.util.UriHelper.getAdminUri("common/images/icons/icoMoon/128x128/puzzle.png");
        }

        resolveTitle(content: api.application.Application): string {
            return content.getDisplayName().toString();
        }

        resolveSubTitle(content: api.application.Application): string {
            return content.getApplicationKey().toString();
        }

    }
}