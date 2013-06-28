module api_ui {

    export interface PanelNavigationItem {

        setIndex(value:number);

        getIndex():number;

        getLabel():string;

        isVisible():bool;

        isRemovable():bool;

    }
}
