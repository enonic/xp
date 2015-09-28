module app.create {

    export class MostPopularItemsBlock extends api.dom.DivEl {

        public static DEFAULT_MAX_ITEMS = 2;

        private mostPopularItemsList: MostPopularItemsList;

        private title: api.dom.H2El;

        constructor(title = "Most Popular") {
            super("most-popular-content-types-container");

            this.title = new api.dom.H2El();
            this.title.setHtml(title);

            this.mostPopularItemsList = new MostPopularItemsList();
            this.appendChildren(this.title, this.mostPopularItemsList);
        }

        getMostPopularList(): MostPopularItemsList {
            return this.mostPopularItemsList;
        }

        setTitle(newTitle: string) {
            this.title.setHtml(newTitle);
        }
    }
}