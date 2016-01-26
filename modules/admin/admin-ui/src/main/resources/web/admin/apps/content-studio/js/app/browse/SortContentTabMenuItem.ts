module app.browse {

    import TabMenuItemBuilder = api.ui.tab.TabMenuItemBuilder;

    export class SortContentTabMenuItem extends api.ui.tab.TabMenuItem {

        private childOrder: api.content.ChildOrder;

        constructor(builder: SortContentTabMenuItemBuilder) {
            super((<TabMenuItemBuilder>new TabMenuItemBuilder().
                setLabel(builder.label)));
            this.childOrder = builder.childOrder;
        }

        getChildOrder(): api.content.ChildOrder {
            return this.childOrder;
        }

    }

    export class SortContentTabMenuItemBuilder {
        label: string;
        childOrder: api.content.ChildOrder;

        setLabel(label: string): SortContentTabMenuItemBuilder {
            this.label = label;
            return this;
        }

        setChildOrder(value: api.content.ChildOrder): SortContentTabMenuItemBuilder {
            this.childOrder = value;
            return this;
        }

        build(): SortContentTabMenuItem {
            return new SortContentTabMenuItem(this);
        }

    }
}
