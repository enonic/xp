module api_ui_tab {

    export interface Tab {

        setTabIndex(value:number);

        getTabIndex():number;

        getLabel():string;

    }
}
