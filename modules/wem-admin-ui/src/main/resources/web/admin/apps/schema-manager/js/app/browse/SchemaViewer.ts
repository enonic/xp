module app.browse {

    import Schema = api.schema.Schema;
    import SchemaKind = api.schema.SchemaKind;
    import SchemaIconUrlResolver = api.schema.SchemaIconUrlResolver;

    export class SchemaViewer extends api.ui.Viewer<Schema> {

        private schemaIconUrlResolver: SchemaIconUrlResolver;

        private namesAndIconView: SchemaNamesAndIconView;

        constructor() {
            super();
            this.schemaIconUrlResolver = new SchemaIconUrlResolver();
            var builder = new api.app.NamesAndIconViewBuilder().setSize(api.app.NamesAndIconViewSize.small);
            this.namesAndIconView = new SchemaNamesAndIconView(builder);
            this.namesAndIconView.addClass('schema-grid-view');
            this.appendChild(this.namesAndIconView);
        }

        setObject(schema: Schema) {
            super.setObject(schema);

            this.namesAndIconView.setMainName(schema.getDisplayName()).
                setSubName(schema.getName()).
                setIconUrl(this.schemaIconUrlResolver.resolve(schema));
            this.namesAndIconView.setOverlay(schema.getSchemaKind().toString());
        }

        getPreferredHeight(): number {
            return 50;
        }
    }
}