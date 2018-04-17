interface Listener{
    (eventType: string, ...eventArg: any[]): void
}

interface EventBase{
    new();
    addListener(type: string, listener: Listener ): void;
    removeListener(type: string, listener: Listener): void;
    fireEvent( type: string ) : void;
}

declare let EventBase : EventBase;