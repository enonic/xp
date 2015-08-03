module api.application {

    export class ApplicationComboBox extends api.ui.selector.combobox.RichComboBox<api.application.Application> {
        constructor(maximumOccurrences?: number) {
            var builder = new api.ui.selector.combobox.RichComboBoxBuilder<api.application.Application>();
            builder.
                setMaximumOccurrences(maximumOccurrences || 0).
                setComboBoxName("moduleSelector").
                setLoader(new api.application.ApplicationLoader()).
                setSelectedOptionsView(new ModuleSelectedOptionsView()).
                setOptionDisplayValueViewer(new ApplicationViewer()).
                setDelayedInputValueChangedHandling(500);
            super(builder);
        }
    }

    export class ModuleSelectedOptionsView extends api.ui.selector.combobox.BaseSelectedOptionsView<api.application.Application> {

        createSelectedOption(option: api.ui.selector.Option<api.application.Application>): api.ui.selector.combobox.SelectedOption<api.application.Application> {
            var optionView = new ModuleSelectedOptionView(option);
            return new api.ui.selector.combobox.SelectedOption<api.application.Application>(optionView, this.count());
        }
    }

    export class ModuleSelectedOptionView extends api.ui.selector.combobox.RichSelectedOptionView<api.application.Application> {


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