module api.ui.tab {

    export class ShowTabMenuEvent extends api.event.Event {

        tabMenu: TabMenu;

        constructor(tabMenu: TabMenu) {
            super();
            this.tabMenu = tabMenu;
        }

        getTabMenu(): TabMenu {
            return this.tabMenu;
        }

        static on(handler: (event: ShowTabMenuEvent) => void) {
            api.event.Event.bind(api.ClassHelper.getFullName(this), handler);
        }

        static un(handler?: (event: ShowTabMenuEvent) => void) {
            api.event.Event.unbind(api.ClassHelper.getFullName(this), handler);
        }

    }
}