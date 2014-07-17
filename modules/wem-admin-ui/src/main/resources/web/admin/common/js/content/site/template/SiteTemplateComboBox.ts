module api.content.site.template {

    import SiteTemplateSummary = api.content.site.template.SiteTemplateSummary;
    
    export class SiteTemplateComboBox extends api.ui.selector.combobox.RichComboBox<SiteTemplateSummary>
    {
        constructor()
        {
            var builder = new api.ui.selector.combobox.RichComboBoxBuilder<SiteTemplateSummary>();
            builder.
                setComboBoxName("siteTemplateSelector" ).
                setLoader(new api.content.site.template.SiteTemplateLoader() ).
                setSelectedOptionsView(new SiteTemplateSelectedOptionsView()).
                setOptionDisplayValueViewer(new SiteTemplateSummaryViewer()).
                setDelayedInputValueChangedHandling(500).
                setMaximumOccurrences(1);
            super(builder);
        }
    }

    export class SiteTemplateSelectedOptionsView extends api.ui.selector.combobox.SelectedOptionsView<SiteTemplateSummary> {

        createSelectedOption(option:api.ui.selector.Option<SiteTemplateSummary>):api.ui.selector.combobox.SelectedOption<SiteTemplateSummary> {
            var optionView = new SiteTemplateSelectedOptionView( option );
            return new api.ui.selector.combobox.SelectedOption<SiteTemplateSummary>( optionView, this.count());
        }
    }

    export class SiteTemplateSelectedOptionView extends api.ui.selector.combobox.RichSelectedOptionView<SiteTemplateSummary> {


        constructor(option:api.ui.selector.Option<SiteTemplateSummary>) {
            super(option, api.app.NamesAndIconViewSize.medium);
        }

        resolveIconUrl(siteTemplate:SiteTemplateSummary):string
        {
            return api.util.getAdminUri("common/images/icons/icoMoon/128x128/earth.png");
        }

        resolveTitle(siteTemplate:SiteTemplateSummary):string
        {
            return siteTemplate.getDisplayName();
        }

        resolveSubTitle(siteTemplate:SiteTemplateSummary):string
        {
            return siteTemplate.getDescription();
        }

    }
}