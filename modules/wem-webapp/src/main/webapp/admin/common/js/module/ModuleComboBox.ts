module api.module {

    export class ModuleComboBox extends api.ui.selector.combobox.RichComboBox<api.module.ModuleSummary>
    {
        constructor()
        {
            var builder = new api.ui.selector.combobox.RichComboBoxBuilder<api.module.ModuleSummary>();
            builder.
                setComboBoxName("moduleSelector" ).
                setLoader(new api.module.ModuleLoader() ).
                setSelectedOptionsView(new ModuleSelectedOptionsView()).
                setOptionDisplayValueViewer(new ModuleSummaryViewer()).
                setDelayedInputValueChangedHandling(500);
            super(builder);
        }
    }

    export class ModuleSelectedOptionsView extends api.ui.selector.combobox.SelectedOptionsView<api.module.ModuleSummary> {

        createSelectedOption(option:api.ui.selector.Option<api.module.ModuleSummary>, index:number):api.ui.selector.combobox.SelectedOption<api.module.ModuleSummary> {
            var optionView = new ModuleSelectedOptionView( option );
            return new api.ui.selector.combobox.SelectedOption<api.module.ModuleSummary>( optionView, option, index);
        }
    }

    export class ModuleSelectedOptionView extends api.ui.selector.combobox.RichSelectedOptionView<api.module.ModuleSummary> {


        constructor(option:api.ui.selector.Option<api.module.ModuleSummary>) {
            super(option);
        }

        resolveIconUrl(content:api.module.ModuleSummary):string
        {
            return api.util.getAdminUri("common/images/icons/icoMoon/128x128/puzzle.png");
        }

        resolveTitle(content:api.module.ModuleSummary):string
        {
            return content.getDisplayName().toString();
        }

        resolveSubTitle(content:api.module.ModuleSummary):string
        {
            return content.getModuleKey().toString();
        }

    }
}