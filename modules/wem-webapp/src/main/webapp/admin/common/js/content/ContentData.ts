module api.content {

    export class ContentData extends api.data.RootDataSet {

        constructor() {
            super();
        }

        equals(o: api.Equitable): boolean {

            if (!(o instanceof ContentData)) {
                return false;
            }

            return super.equals(o);
        }
    }
}