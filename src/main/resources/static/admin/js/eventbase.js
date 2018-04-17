;( function(){
    function getListener( obj, type, force ) {
        var allListeners;
        type = type.toLowerCase();
        return ( ( allListeners = ( obj.__allListeners || force && ( obj.__allListeners = {} ) ) )
        && ( allListeners[type] || force && ( allListeners[type] = [] ) ) );
    }

    var EventBase = function() {
        this.addListener = function ( type, listener ) {
            getListener( this, type, true ).push( listener );
        }

        this.removeListener = function ( type, listener ) {
            var listeners = getListener( this, type );
            for( var i = 0; i < listeners.length; i++ ) {
                if( listeners[ i ] == listener ) {
                    listeners.splice( i, 1 );
                    return;
                }
            }
        }

        this.fireEvent = function ( type ) {
            var listeners = getListener( this, type ),
                r, t, k;
            if ( listeners ) {
                k = listeners.length;
                while ( k -- ) {
                    t = listeners[k].apply( this, arguments );
                    if ( t !== undefined ) {
                        r = t;
                    }
                }
            }
            if ( t = this['on' + type.toLowerCase()] ) {
                r = t.apply( this, arguments );
            }
            return r;
        }
    }

    window.EventBase = EventBase;
})();

