// Dependencies
var events = require( 'events' );

var PiggieEvents = function PiggieEvents () {};

// Set base class
PiggieEvents.prototype = new events.EventEmitter();

/**
 * Emit override
 */
PiggieEvents.prototype.emit = function emit ( type, data, fromNative ) {

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
module.exports = PiggieEvents;
