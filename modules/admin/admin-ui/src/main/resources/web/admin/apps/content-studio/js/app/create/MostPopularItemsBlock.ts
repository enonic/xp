import "../../api.ts";
import {MostPopularItemsList} from "./MostPopularItemsList";

export class MostPopularItemsBlock extends api.dom.DivEl {

    public static DEFAULT_MAX_ITEMS: number = 2;

    private mostPopularItemsList: MostPopularItemsList;

    private title: api.dom.H2El;

    constructor(title: string = 'Most Popular') {
        super('most-popular-content-types-container');

        this.title = new api.dom.H2El();
        this.title.setHtml(title);

        this.mostPopularItemsList = new MostPopularItemsList();
        this.appendChildren(this.title, this.mostPopularItemsList);
    }

    getItemsList(): MostPopularItemsList {
        return this.mostPopularItemsList;
    }

    setTitle(newTitle: string) {
        this.title.setHtml(newTitle);
    }

    showIfNotEmpty() {
        if (this.mostPopularItemsList.getItems().length > 0) {
            this.show();
        }
    }
}
