module api.ui.tab {

    export class TabBarItem extends TabItem {

        constructor(builder: TabBarItemBuilder) {
            super(builder, "tab-bar-item");
        }
    }

    export class TabBarItemBuilder extends TabItemBuilder {

        build(): TabBarItem {
            return new TabBarItem(this);
        }

    }
}