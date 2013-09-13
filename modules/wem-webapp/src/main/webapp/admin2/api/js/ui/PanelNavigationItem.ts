module api_ui {

    export interface PanelNavigationItem {

        getElement():api_dom.Element;

        setIndex(value:number);

        getIndex():number;

        setLabel(value:string);

        getLabel():string;

        setActive(value:boolean);

        isActive():boolean;

        setVisible(value:boolean);

        isVisible():boolean;

        setRemovable(value:boolean);

        isRemovable():boolean;

    }
}
