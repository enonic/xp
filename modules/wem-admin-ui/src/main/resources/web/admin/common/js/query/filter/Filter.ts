module api.query.filter {

    export class Filter {

        public toJson(): api.query.filter.FilterTypeWrapperJson {
            throw new Error("Must be implemented by inheritor: " + api.ClassHelper.getClassName(this));
        }

    }


}

