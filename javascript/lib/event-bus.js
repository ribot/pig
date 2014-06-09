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
  var json;

  if ( typeof name !== 'string' && name.length < 1 ) {
    throw Error( 'An event name must be defined' );
  }

  if ( data ) {
    try {
      json = JSON.stringify( data );
    } catch ( error ) {
      return this.fail( error );
    }
  }

  json = json || '';

  fromNative = ( fromNative === true ) || false;

  if ( !fromNative ) {
    nativeInterface.event( name, json );
  }

  events.EventEmitter.prototype.emit.call( this, name, data );

};

// Exports
module.exports = PigEvents;
