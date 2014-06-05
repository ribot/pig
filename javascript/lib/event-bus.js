// Dependencies
var events = require( 'events' ),
    nativeInterface = require( './native-interface' );

var PigEvents = function PigEvents() {};

// Set base class
PigEvents.prototype = new events.EventEmitter();

/**
 * Emit override
 */
PigEvents.prototype.emit = function emit( type, data, fromNative ) {

  fromNative = ( fromNative === true ) || false;

  if ( !fromNative ) {
    nativeInterface.event( type, data );
  }

  events.EventEmitter.prototype.emit.call( this, type, data );

};

// Exports
module.exports = PigEvents;
