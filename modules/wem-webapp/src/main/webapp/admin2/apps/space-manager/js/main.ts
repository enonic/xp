//  <reference path='../../../api/js/ExtJs.d.ts' />
///<reference path='../../../api/js/api.d.ts' />

///<reference path='lib/JsonRpcProvider.ts' />
///<reference path='lib/RemoteService.ts' />

///<reference path='event/DeletedEvent.ts' />
///<reference path='event/DeletePromptEvent.ts' />

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

///<reference path='view/BaseTreeGridPanel.ts' />
///<reference path='view/TreeGridPanel.ts' />

///<reference path='view/ContextMenu.ts' />

///<reference path='view/wizard/Toolbar.ts' />
///<reference path='view/wizard/SpaceStepPanel.ts' />
///<reference path='view/wizard/WizardPanel.ts' />

///<reference path='view/AdminImageButton.ts' />
///<reference path='view/TopBarMenuItem.ts' />
///<reference path='view/TopBarMenu.ts' />
///<reference path='view/TopBar.ts' />
///<reference path='view/TabPanel.ts' />
///<reference path='view/BaseFilterPanel.ts' />
///<reference path='view/FilterPanel.ts' />
///<reference path='view/BrowseToolbar.ts' />

///<reference path='controller/Controller.ts' />
///<reference path='controller/SpaceController.ts' />

///<reference path='controller/FilterPanelController.ts' />
///<reference path='controller/GridPanelController.ts' />
///<reference path='controller/BrowseToolbarController.ts' />
///<reference path='controller/DetailPanelController.ts' />
///<reference path='controller/DetailToolbarController.ts' />
///<reference path='controller/DialogWindowController.ts' />
///<reference path='controller/WizardController.ts' />


declare var Ext;
declare var Admin;
declare var CONFIG;

module APP {

    // Application id for uniquely identifying app
    export var id = 'space-manager';

}

module components {
    export var detailPanel:admin.ui.SpaceDetailPanel;
    export var gridPanel;
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

        var grid = components.gridPanel = new admin.ui.TreeGridPanel('center');

        var detail = components.detailPanel = new admin.ui.SpaceDetailPanel('south');

        var center = new Ext.container.Container();
        center.region = 'center';
        center.layout = 'border';

        center.add(detail.ext);
        center.add(grid.ext);
        center.add(toolbar.ext);

        var west = new admin.ui.FilterPanel().getExtEl();
        west.region = 'west';
        west.width = 200;

        var p = new Ext.panel.Panel();
        p.id = 'tab-browse';
        p.title = 'Browse';
        p.closable = false;
        p.border = false;
        p.layout = 'border';
        p.tabConfig = { hidden: true };

        p.add(center);
        p.add(west);

        var tabPanel = new Admin.view.TabPanel({appName: 'Space Admin', appIconCls: 'icon-metro-space-admin-24'});
//        tabPanel.appName = 'Space Admin';
//        tabPanel.appIconCls = 'icon-metro-space-admin-24';

        tabPanel.add(p);

        var wp = new Ext.container.Viewport();
        wp.layout = 'fit';
        wp.cls = 'admin-viewport';

        wp.add(tabPanel);

        new admin.ui.DeleteSpaceWindow();
    }

});