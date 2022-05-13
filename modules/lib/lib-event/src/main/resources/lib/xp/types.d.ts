declare interface EventListenerHelper {
  readonly setType(type: string): void;
  readonly setListener<T = unknown>(callback: (event: T) => void): void;
  readonly setLocalOnly(localOnly: boolean): void;
  readonly register(): void;
}

declare interface EventSenderHelper {
  readonly setType(type: string): void;
  readonly setDistributed(distributed: boolean): void;
  readonly setData(value: object): void;
  readonly send(): void;
}
