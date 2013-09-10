///<reference path='../../../api/js/ExtJs.d.ts' />
///<reference path='../../../api/js/Mousetrap.d.ts' />
///<reference path='../../../api/js/api.d.ts' />

///<reference path='model/ContentExtModel.ts' />
///<reference path='model/ContentTypeExtModel.ts' />

///<reference path='lib/ux/toggleslide/Thumb.ts' />
///<reference path='lib/ux/toggleslide/ToggleSlide.ts' />

///<reference path='app/delete/ContentDeleteDialog.ts' />

///<reference path='app/wizard/ContentWizardEvents.ts' />
///<reference path='app/wizard/ContentWizardActions.ts' />
///<reference path='app/wizard/ContentForm.ts' />
///<reference path='app/wizard/ContentWizardToolbar.ts' />
///<reference path='app/wizard/ContentWizardPanel.ts' />

///<reference path='app/wizard/form/input/Input.ts' />
///<reference path='app/wizard/form/input/BaseInput.ts' />
///<reference path='app/wizard/form/input/TextLine.ts' />
///<reference path='app/wizard/form/input/TextArea.ts' />
///<reference path='app/wizard/form/input/HtmlArea.ts' />
///<reference path='app/wizard/form/FormItemView.ts' />
///<reference path='app/wizard/form/InputLabel.ts' />
///<reference path='app/wizard/form/InputContainerView.ts' />
///<reference path='app/wizard/form/FormItemSetLabel.ts' />
///<reference path='app/wizard/form/FormItemSetView.ts' />
///<reference path='app/wizard/form/FormView.ts' />


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

///<reference path='store/ContentStore.ts' />
///<reference path='store/ContentTreeStore.ts' />
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

Ext.application({
    name: 'CM',

    appFolder: 'resources/app',

    controllers: [],

    launch: function () {

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

            var parentContent:api_model.ContentExtModel = event.getParentContent();

            new api_content.GetContentByIdRequest(parentContent.data.id).sendAndPromise().
                done((jsonResponse:api_rest.JsonResponse) => {
                    var newParentContent = new api_content.Content(jsonResponse.getJson().contents[0]);
                    newContentDialog.setParentContent(newParentContent);
                    newContentDialog.open();
                });
        });
    }
});