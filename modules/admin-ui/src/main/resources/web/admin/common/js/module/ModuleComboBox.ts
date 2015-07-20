module api.module {

    export class ModuleComboBox extends api.ui.selector.combobox.RichComboBox<api.module.Application> {
        constructor(maximumOccurrences?: number) {
            var builder = new api.ui.selector.combobox.RichComboBoxBuilder<api.module.Application>();
            builder.
                setMaximumOccurrences(maximumOccurrences || 0).
                setComboBoxName("moduleSelector").
                setLoader(new api.module.ModuleLoader()).
                setSelectedOptionsView(new ModuleSelectedOptionsView()).
                setOptionDisplayValueViewer(new ModuleViewer()).
                setDelayedInputValueChangedHandling(500);
            super(builder);
        }
    }

    export class ModuleSelectedOptionsView extends api.ui.selector.combobox.BaseSelectedOptionsView<api.module.Application> {

        createSelectedOption(option: api.ui.selector.Option<api.module.Application>): api.ui.selector.combobox.SelectedOption<api.module.Application> {
            var optionView = new ModuleSelectedOptionView(option);
            return new api.ui.selector.combobox.SelectedOption<api.module.Application>(optionView, this.count());
        }
    }

    export class ModuleSelectedOptionView extends api.ui.selector.combobox.RichSelectedOptionView<api.module.Application> {


        constructor(option: api.ui.selector.Option<api.module.Application>) {
            super(option);
        }

        resolveIconUrl(content: api.module.Application): string {
            return api.util.UriHelper.getAdminUri("common/images/icons/icoMoon/128x128/puzzle.png");
        }

        resolveTitle(content: api.module.Application): string {
            return content.getDisplayName().toString();
        }

        resolveSubTitle(content: api.module.Application): string {
            return content.getApplicationKey().toString();
        }

    }
}