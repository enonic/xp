module api.module {

    export class ModuleComboBox extends api.ui.selector.combobox.RichComboBox<api.module.ModuleSummary>
    {
        constructor()
        {
            var builder:api.ui.selector.combobox.RichComboBoxBuilder<api.module.ModuleSummary> = new api.ui.selector.combobox.RichComboBoxBuilder<api.module.ModuleSummary>();
            builder.setComboBoxName("moduleSelector" ).setLoader(new api.module.ModuleLoader() ).setSelectedOptionsView(new ModuleSelectedOptionsView());
            super(builder);
        }

        optionFormatter(row:number, cell:number, moduleInst:api.module.ModuleSummary, columnDef:any, dataContext:api.ui.selector.combobox.Option<api.module.ModuleSummary>):string {
            var namesAndIconView = new api.app.NamesAndIconViewBuilder().setSize( api.app.NamesAndIconViewSize.small ).build();

            namesAndIconView.
                setIconUrl(api.util.getAdminUri("common/images/icons/icoMoon/32x32/puzzle.png")).
                setMainName(moduleInst.getDisplayName()).
                setSubName(moduleInst.getUrl());

            return namesAndIconView.toString();
        }
    }

    export class ModuleSelectedOptionsView extends api.ui.selector.combobox.SelectedOptionsView<api.module.ModuleSummary> {

        createSelectedOption(option:api.ui.selector.combobox.Option<api.module.ModuleSummary>, index:number):api.ui.selector.combobox.SelectedOption<api.module.ModuleSummary> {
            var optionView = new ModuleSelectedOptionView( option );
            return new api.ui.selector.combobox.SelectedOption<api.module.ModuleSummary>( optionView, option, index);
        }
    }

    export class ModuleSelectedOptionView extends api.ui.selector.combobox.RichSelectedOptionView<api.module.ModuleSummary> {


        constructor(option:api.ui.selector.combobox.Option<api.module.ModuleSummary>) {
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