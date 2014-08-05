module app.browse {

    import Schema = api.schema.Schema;

    export class SchemaViewer extends api.ui.Viewer<Schema> {

        private namesAndIconView: api.app.NamesAndIconView;

        constructor() {
            super();

            this.namesAndIconView = new api.app.NamesAndIconViewBuilder().setSize(api.app.NamesAndIconViewSize.small).build();
            this.appendChild(this.namesAndIconView);
        }

        setObject(schema: Schema) {
            super.setObject(schema);

            this.namesAndIconView.setMainName(schema.getDisplayName()).
                setSubName(schema.getName()).
                setIconUrl(schema.getIconUrl());
        }

        getPreferredHeight(): number {
            return 50;
        }
    }
}