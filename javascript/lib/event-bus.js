// Dependencies
var events = require( 'events' ),
    nativeInterface = require( './native-interface' );

var PigEvents = function PigEvents() {};

// Set base class
PigEvents.prototype = new events.EventEmitter();

/**
 * Emit override
 */
PigEvents.prototype.emit = function emit( name, data, fromNative ) {
  var json = ( data ? JSON.stringify( data ) : '' );

  if ( typeof name !== 'string' ) {
    throw Error( 'An event name must be defined' );
  }

  fromNative = ( fromNative === true ) || false;

  if ( !fromNative ) {
    nativeInterface.event( name, json );
  }

  events.EventEmitter.prototype.emit.call( this, name, data );

};

// Exports
module.exports = PigEvents;
