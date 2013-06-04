///<reference path='../../../api/js/ExtJs.d.ts' />
///<reference path='../../../api/js/api.d.ts' />

///<reference path='../../../api/js/lib/JsonRpcProvider.ts' />
///<reference path='../../../api/js/lib/RemoteService.ts' />

///<reference path='event/DeletedEvent.ts' />
///<reference path='event/SpaceModelEvent.ts' />
///<reference path='event/DeletePromptEvent.ts' />
///<reference path='event/GridSelectionChangeEvent.ts' />
///<reference path='event/ShowContextMenuEvent.ts' />
///<reference path='event/NewSpaceEvent.ts' />
///<reference path='event/OpenSpaceEvent.ts' />
///<reference path='event/EditSpaceEvent.ts' />
///<reference path='event/SaveSpaceEvent.ts' />

///<reference path='action/SpaceActions.ts' />

///<reference path='plugin/PersistentGridSelectionPlugin.ts' />
///<reference path='plugin/GridToolbarPlugin.ts' />
///<reference path='plugin/fileupload/FileUploadGrid.ts' />
///<reference path='plugin/fileupload/PhotoUploadButton.ts' />
///<reference path='plugin/fileupload/PhotoUploadWindow.ts' />

///<reference path='model/SpaceModel.ts' />

///<reference path='handler/DeleteSpacesHandler.ts' />
///<reference path='view/WizardLayout.ts' />
///<reference path='view/WizardHeader.ts' />
///<reference path='view/WizardPanel.ts' />

///<reference path='view/BaseActionMenu.ts' />
///<reference path='view/ActionMenu.ts' />
///<reference path='view/DetailToolbar.ts' />
///<reference path='view/DetailPanel.ts' />

///<reference path='view/DeleteSpaceWindow.ts' />

///<reference path='view/TreeGridPanel.ts' />

///<reference path='view/ContextMenu.ts' />

///<reference path='view/wizard/SpaceWizardToolbar.ts' />
///<reference path='view/wizard/SpaceStepPanel.ts' />
///<reference path='view/wizard/SpaceWizardPanel.ts' />

///<reference path='view/AdminImageButton.ts' />
///<reference path='view/TopBarMenuItem.ts' />
///<reference path='view/TopBarMenu.ts' />
///<reference path='view/TopBar.ts' />
///<reference path='view/TabPanel.ts' />
///<reference path='view/FilterPanel.ts' />
///<reference path='view/BrowseToolbar.ts' />

///<reference path='view/BrowseToolbar2.ts' />

///<reference path='controller/Controller.ts' />
///<reference path='controller/SpaceController.ts' />

///<reference path='controller/FilterPanelController.ts' />
///<reference path='controller/GridPanelController.ts' />
///<reference path='controller/BrowseToolbarController.ts' />
///<reference path='controller/DetailPanelController.ts' />
///<reference path='controller/DetailToolbarController.ts' />
///<reference path='controller/DialogWindowController.ts' />
///<reference path='controller/WizardController.ts' />

///<reference path='context/SpaceContext.ts' />


declare var Ext;
declare var Admin;
declare var CONFIG;

module APP {

    // Application id for uniquely identifying app
    export var id = 'space-manager';

}

module components {
    export var detailPanel:admin.ui.SpaceDetailPanel;
    export var gridPanel:admin.ui.TreeGridPanel;
    export var tabPanel;
    export var deleteWindow;
}

Ext.application({
    name: 'spaceAdmin',

    controllers: [
        'Admin.controller.FilterPanelController',
        'Admin.controller.GridPanelController',
        'Admin.controller.BrowseToolbarController',
        'Admin.controller.DetailPanelController',
        'Admin.controller.DetailToolbarController',
        'Admin.controller.DialogWindowController',
        'Admin.controller.WizardController'
    ],

    stores: [],

    launch: function () {

        var toolbar = new admin.ui.BrowseToolbar('north');
        var toolbar2 = new APP.ui.BrowseToolbar2();

        var grid = components.gridPanel = new admin.ui.TreeGridPanel('center');

        var detail = components.detailPanel = new admin.ui.SpaceDetailPanel('south');

        var center = new Ext.container.Container({
            region: 'center',
            layout: 'border'
        });

        center.add(detail.ext);
        center.add(grid.ext);
        center.add(toolbar.ext);
        center.add(toolbar2.ext);

        var west = new admin.ui.FilterPanel({
            region: 'west',
            width: 200
        }).getExtEl();

        var p = new Ext.panel.Panel({
            id: 'tab-browse',
            title: 'Browse',
            closable: false,
            border: false,
            layout: 'border',
            tabConfig: { hidden: true }
        });

        p.add(center);
        p.add(west);

        var tabPanel = components.tabPanel = new admin.ui.TabPanel({
            appName: 'Space Admin',
            appIconCls: 'icon-metro-space-admin-24'
        }).getExtEl();

        tabPanel.add(p);

        var wp = new Ext.container.Viewport({
            layout: 'fit',
            cls: 'admin-viewport'
        });

        wp.add(tabPanel);

        // Instanciating classes that will be triggered by events
        components.deleteWindow = new admin.ui.DeleteSpaceWindow();
        new admin.ui.ContextMenu();

    }

});

APP_context.SpaceContext.init();