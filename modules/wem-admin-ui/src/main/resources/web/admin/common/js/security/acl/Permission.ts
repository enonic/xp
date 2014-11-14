module api.security.acl {

    /**
     *  enum Color{
     *      Red, Green
     *  }
     *
     *  // To String
     *  var green: string = Color[Color.Green];
     *
     *  // To Enum / number
     *  var color : Color = Color[green];
     */

    export enum Permission {
        READ,
        CREATE,
        MODIFY,
        DELETE,
        PUBLISH,
        READ_PERMISSIONS,
        WRITE_PERMISSIONS
    }

    export enum PermissionState {
        ALLOW,
        DENY,
        INHERIT
    }
}
