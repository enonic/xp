module app_contextwindow {
    export interface Component {
        name:string;
        key:number;
        componentType:{
            cssSelector:string;
            cursor:string;
            type:number;
            typeName:string;
            iconCls:string;
        };
        isEmpty:() => boolean;
        isSelected:() => boolean;
    }
}