module api.content {

    export class ContentData extends api.data.RootDataSet implements api.Cloneable {

        public static CONTENT_DATA_PATH = "data";

        constructor() {
            super();
        }

        equals(o: api.Equitable): boolean {

            if (!(api.ObjectHelper.iFrameSafeInstanceOf(o, ContentData))) {
                return false;
            }

            return super.equals(o);
        }

        clone(): ContentData {

            var clone = new ContentData();
            this.getDataArray().forEach((data: api.data.Data) => {
                clone.addData(data.clone());
            });
            return clone;
        }
    }
}