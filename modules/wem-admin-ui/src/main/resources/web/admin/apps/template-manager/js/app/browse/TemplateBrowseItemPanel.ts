module app.browse {

    export class TemplateBrowseItemPanel extends api.app.browse.BrowseItemPanel<app.browse.TemplateBrowseItem> {

        constructor() {
            super();
        }

        createItemStatisticsPanel(): app.view.TemplateItemStatisticsPanel {
            return new app.view.TemplateItemStatisticsPanel();
        }
    }
}