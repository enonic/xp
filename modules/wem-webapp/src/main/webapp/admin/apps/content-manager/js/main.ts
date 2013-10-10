///<reference path='../../../api/js/lib/ExtJs.d.ts' />
///<reference path='../../../api/js/lib/Mousetrap.d.ts' />
///<reference path='../../../api/js/api.d.ts' />

///<reference path='model/ContentSummaryExtModel.ts' />
///<reference path='model/ContentTypeExtModel.ts' />

///<reference path='lib/ux/toggleslide/Thumb.ts' />
///<reference path='lib/ux/toggleslide/ToggleSlide.ts' />

///<reference path='app/delete/ContentDeleteDialog.ts' />

///<reference path='app/wizard/ContentWizardEvents.ts' />
///<reference path='app/wizard/ContentWizardActions.ts' />
///<reference path='app/wizard/ContentForm.ts' />
///<reference path='app/wizard/ContentWizardToolbar.ts' />
///<reference path='app/wizard/ContentWizardPanel.ts' />

///<reference path='app/wizard/form/input/InputTypeManager.ts' />
///<reference path='app/wizard/form/module.ts' />
///<reference path='app/wizard/form/input/type/module.ts' />
///<reference path='app/wizard/form/input/module.ts' />
///<reference path='app/wizard/form/formitemset/module.ts' />
///<reference path='app/wizard/form/layout/module.ts' />



///<reference path='app/browse/filter/ContentBrowseFilterEvents.ts' />
///<reference path='app/browse/filter/ContentBrowseFilterPanel.ts' />

///<reference path='app/browse/ContentBrowseEvents.ts' />
///<reference path='app/browse/ContentBrowseActions.ts' />
///<reference path='app/browse/ContentBrowseToolbar.ts' />
///<reference path='app/browse/ContentTreeGridContextMenu.ts' />
///<reference path='app/browse/ContentBrowseItemPanel.ts' />
///<reference path='app/browse/ContentBrowsePanel.ts' />
///<reference path='app/browse/grid/ContentTreeStore.ts' />
///<reference path='app/browse/grid/ContentGridStore.ts' />
///<reference path='app/browse/ContentTreeGridPanel.ts' />

///<reference path='app/contextwindow/Component.ts' />
///<reference path='app/contextwindow/ContextWindowEvents.ts' />
///<reference path='app/contextwindow/ComponentTypesPanel.ts' />
///<reference path='app/contextwindow/ComponentGrid.ts' />
///<reference path='app/contextwindow/EmulatorGrid.ts' />
///<reference path='app/contextwindow/EmulatorPanel.ts' />
///<reference path='app/contextwindow/SelectPanel.ts' />
///<reference path='app/contextwindow/DetailPanel.ts' />
///<reference path='app/contextwindow/InspectorPanel.ts' />
///<reference path='app/contextwindow/ContextWindow.ts' />

///<reference path='app/view/ContentItemViewActions.ts' />
///<reference path='app/view/ContentItemViewToolbar.ts' />
///<reference path='app/view/ContentItemStatisticsPanel.ts' />
///<reference path='app/view/ContentItemViewPanel.ts' />

///<reference path='app/new/NewContentEvent.ts' />
///<reference path='app/new/NewContentDialog.ts' />
///<reference path='app/new/RecentContentTypes.ts' />
///<reference path='app/new/AllContentTypesList.ts' />
///<reference path='app/new/ContentTypesListListener.ts' />
///<reference path='app/new/ContentTypesList.ts' />
///<reference path='app/new/ContentTypeListItem.ts' />
///<reference path='app/new/RecentContentTypesList.ts' />
///<reference path='app/new/RecommendedContentTypesList.ts' />

///<reference path='handler/DeleteContentHandler.ts' />

///<reference path='store/ContentTypeStore.ts' />
///<reference path='store/ContentTypeTreeStore.ts' />

///<reference path='lib/UriHelper.ts' />
///<reference path='lib/DateHelper.ts' />
///<reference path='lib/Sortable.ts' />

///<reference path='app/ContentAppPanel.ts' />

declare var Ext;
declare var Admin;
declare var CONFIG;

module components {
    export var browseToolbar:app_browse.ContentBrowseToolbar;
    export var contextMenu:app_browse.ContentTreeGridContextMenu;
    export var gridPanel:app_browse.ContentTreeGridPanel;
    export var detailPanel:app_browse.ContentBrowseItemPanel;
}

var router;
function setRouter(r) {
    console.log("Setting rounter ", r);
    router = r;
}

window.onload = () => {
        var appBar = new api_app.AppBar("Content Manager", new api_app.AppBarTabMenu("ContentAppBarTabMenu"));
        var appPanel = new app.ContentAppPanel(appBar);

        api_dom.Body.get().appendChild(appBar);
        api_dom.Body.get().appendChild(appPanel);

        appPanel.init();

        var contentDeleteDialog = new app_delete.ContentDeleteDialog();
        app_browse.ContentDeletePromptEvent.on((event) => {
            contentDeleteDialog.setContentToDelete(event.getModels());
            contentDeleteDialog.open();
        });

        var newContentDialog = new app_new.NewContentDialog();
        app_browse.ShowNewContentDialogEvent.on((event) => {

            var parentContent:api_content.ContentSummary = event.getParentContent();

            new api_content.GetContentByIdRequest(parentContent.getId()).send().
                done((jsonResponse:api_rest.JsonResponse) => {
                    var newParentContent = new api_content.Content(jsonResponse.getJson());
                    newContentDialog.setParentContent(newParentContent);
                    newContentDialog.open();
                });
        });
};