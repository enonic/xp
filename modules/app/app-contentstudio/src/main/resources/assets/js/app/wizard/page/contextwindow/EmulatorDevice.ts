import '../../../../api.ts';

export class EmulatorDevice {
    private id: number;
    private device: EmulatorDeviceItem;

    constructor(id: number, name: string, type: string, width: number, height: number, units: string, displayUnits: boolean,
                rotatable: boolean) {
        this.id = id;
        this.device = new EmulatorDeviceItem(name, type, width, height, units, displayUnits, rotatable);
    }
}

export class EmulatorDeviceItem {
    private name: string;
    private deviceType: string;
    private width: number;
    private height: number;
    private units: string;
    private displayUnits: boolean;
    private rotatable: boolean;

    constructor(name: string, type: string, width: number, height: number, units: string, displayUnits: boolean, rotatable: boolean) {
        this.name = name;
        this.deviceType = type;
        this.width = width;
        this.height = height;
        this.units = units;
        this.displayUnits = displayUnits;
        this.rotatable = rotatable;
    }

    getName(): string {
        return this.name;
    }

    getDeviceType(): string {
        return this.deviceType;
    }

    getWidth(): number {
        return this.width;
    }

    getHeight(): number {
        return this.height;
    }

    getUnits(): string {
        return this.units;
    }

    getDisplayUnits(): boolean {
        return this.displayUnits;
    }

    getRotatable(): boolean {
        return this.rotatable;
    }
}
