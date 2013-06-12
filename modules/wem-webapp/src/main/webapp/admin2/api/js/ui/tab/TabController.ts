module api_ui_tab {

    export class TabController implements TabRemovedListener, TabSelectedListener {

        /*private tabMenu:api_ui_tab.TabMenu;

         private deckPanel:api_ui.DeckPanel;

         private tabs:Tab[] = [];

         constructor(tabMenu:api_ui_tab.TabMenu, deckPanel:api_ui.DeckPanel) {

         this.tabMenu = tabMenu;
         this.deckPanel = deckPanel;

         this.tabMenu.addTabSelectedListener( (tabComponent:api_ui.Element) => {
         var tab = resolveTab(tabMenuItem);
         this.deckPanel.showPanel( tab.getDeckIndex() );

         } );

         this.tabMenu.addTabRemovedListener( (tabComponent:api_ui.Element) => {
         var tab = resolveTab(tabMenuItem);
         this.deckPanel.removePanel(  );
         } );


         // iterate deckPanel and add tabs, except the first one (which is the main app window)
         }



         add( tabComponent:Element, panel ) {

         this.tabMenu.addTab( tab );
         var deckIndex = this.deckPanel.addPanel( panel );

         var tab = new Tab(tabMenuItem, deckIndex);
         }

         resolveTab( tabComponent:Element ):Tab {
         return null;
         } */

        removedTab(tab:Tab) {
            //this.deckPanel.removePanel(tab.getDeckIndex());
        }

        selectedTab(tab:Tab) {
            //this.deckPanel.showPanel(tab.getDeckIndex());
        }
    }
}
