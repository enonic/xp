module app.create {

    export class RecentItemsBlock extends api.dom.AsideEl {

        private recentItemsList: RecentItemsList;

        private title: api.dom.H1El;

        constructor(title = "Recently Used") {
            super("column");

            this.title = new api.dom.H1El();
            this.title.setHtml(title);

            this.recentItemsList = new RecentItemsList();
            this.appendChildren(this.title, this.recentItemsList);
        }

        getRecentItemsList(): RecentItemsList {
            return this.recentItemsList;
        }

        setTitle(newTitle: string) {
            this.title.setHtml(newTitle);
        }
    }
}