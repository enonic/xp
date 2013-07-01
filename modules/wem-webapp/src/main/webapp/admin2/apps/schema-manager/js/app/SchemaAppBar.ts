module app {

    export class SchemaAppBar extends api_app.AppBar {

        constructor() {
            super("Schema Manager", new SchemaAppBarTabMenu());
        }
    }
}