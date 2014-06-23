module api.content {

    export enum CompareStatus {
        NEW,
        NEWER,
        OLDER,
        CONFLICT,
        DELETED,
        EQUAL
    }

//    export class CompareStatus {
//
//        status: Status;
//
//        constructor(status: Status) {
//            this.status = status;
//        }
//
//        static fromJson(json: CompareStatusJson): CompareStatus {
//
//            var status: Status = <Status>Status[json.status];
//
//            return new CompareStatus(status);
//        }
//    }
}