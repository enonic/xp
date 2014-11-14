module app.browse {

    import BrowseItem = api.app.browse.BrowseItem;
    import Module = api.module.Module;
    import ModuleViewer = api.module.ModuleViewer;

    export class ModuleBrowseItemsSelectionPanel extends api.app.browse.BrowseItemsSelectionPanel<Module> {

        createItemViewer(item: BrowseItem<Module>): ModuleViewer  {
            var viewer = new ModuleViewer();
            viewer.setObject(item.getModel());
            return viewer;
        }

    }
}