module app.browse {

    import Schema = api.schema.Schema;
    import SchemaKind = api.schema.SchemaKind;

    export class SchemaViewer extends api.ui.Viewer<Schema> {

        private namesAndIconView: SchemaNamesAndIconView;

        constructor() {
            super();
            var builder = new api.app.NamesAndIconViewBuilder().setSize(api.app.NamesAndIconViewSize.small);
            this.namesAndIconView = new SchemaNamesAndIconView(builder);
            this.namesAndIconView.addClass('schema-grid-view');
            this.appendChild(this.namesAndIconView);
        }

        setObject(schema: Schema) {
            super.setObject(schema);

            this.namesAndIconView.setMainName(schema.getDisplayName()).
                setSubName(schema.getName()).
                setIconUrl(schema.getIconUrl());
            this.namesAndIconView.setOverlay(schema.getSchemaKind().toString());
        }

        getPreferredHeight(): number {
            return 50;
        }
    }
}