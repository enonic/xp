module api.module {

    export class ModuleComboBox extends api.ui.selector.combobox.RichComboBox<api.module.Module> {
        constructor(maximumOccurrences?: number) {
            var builder = new api.ui.selector.combobox.RichComboBoxBuilder<api.module.Module>();
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

    export class ModuleSelectedOptionsView extends api.ui.selector.combobox.BaseSelectedOptionsView<api.module.Module> {

        createSelectedOption(option: api.ui.selector.Option<api.module.Module>): api.ui.selector.combobox.SelectedOption<api.module.Module> {
            var optionView = new ModuleSelectedOptionView(option);
            return new api.ui.selector.combobox.SelectedOption<api.module.Module>(optionView, this.count());
        }
    }

    export class ModuleSelectedOptionView extends api.ui.selector.combobox.RichSelectedOptionView<api.module.Module> {


        constructor(option: api.ui.selector.Option<api.module.Module>) {
            super(option);
        }

        resolveIconUrl(content: api.module.Module): string {
            return api.util.UriHelper.getAdminUri("common/images/icons/icoMoon/128x128/puzzle.png");
        }

        resolveTitle(content: api.module.Module): string {
            return content.getDisplayName().toString();
        }

        resolveSubTitle(content: api.module.Module): string {
            return content.getApplicationKey().toString();
        }

    }
}