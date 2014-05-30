// Dependencies
var events = require( 'events' );

var PigEvents = function PigEvents() {};

// Set base class
PigEvents.prototype = new events.EventEmitter();

/**
 * Emit override
 */
PigEvents.prototype.emit = function emit( type, data, fromNative ) {

  fromNative = ( fromNative === true ) || false;

  if ( !fromNative ) {
    // Android
    if ( typeof window != "undefined" && window.android ) {
      window.android.event( type, data );
    }
    // iOS
    // TODO
  }

  events.EventEmitter.prototype.emit.call( this, type, data );

};

// Exports
module.exports = PigEvents;
