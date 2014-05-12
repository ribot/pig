// Dependencies
var EventEmitter = require( 'events' ).EventEmitter,
    Response = require( './response' );

/**********/
/* Events */
/**********/

// Create and export the events. We'll export most of the method directly,
// but we'll change emit slightly
var events = new EventEmitter();

// This hands over to the normal event emitter for the JS side and also pass
// the event to the native bridge
var emit = function emit ( type, data ) {
  // Throw an error if we have more than 1 data argument
  if ( arguments.length > 2 ) {
    throw new TypeError( 'Piggie only supports a single data argument on the EventEmitter interface' );
  }

  _emit( this, type, data, true );
};

var _nativeEmit = function _nativeEmit ( type, data ) {
  _emit( this, type, data, false );
};

var _emit = function _emit( thisArg, type, data, passToNative ) {
  // Call the normal EventEmitter for JavaScript
  var result = events.emit.call( thisArg, type, data );

  // Pass the event to the native layers if they exist
  if ( passToNative && typeof window != "undefined" && window.android ) {
    window.android.event( type, data );
  }

  return result;
};

// Export the event methods
module.exports.addListener = events.addListener;
module.exports.on = events.on;
module.exports.once = events.once;
module.exports.removeListener = events.removeListener;
module.exports.removeAllListeners = events.removeAllListeners;
module.exports.setMaxListeners = events.setMaxListeners;
module.exports.listener = events.listener;
module.exports.emit = emit;
module.exports._nativeEmit = _nativeEmit;

/***********/
/* REQ/RES */
/***********/

// Handlers hash
var handlers = {};

/**
 * Resets the handlers hash
 */
var _reset = function _reset () {
  handlers = {};
  this.removeAllListeners();
};

/**
 * Register handler
 */
var register = function register ( path, handler ) {

  if ( handlers.hasOwnProperty( path ) ) {
    throw new Error( 'Handler already exists for path: ' + path );
  } else {
    handlers[ path ] = handler;
  }

};

/**
 * Execute handler
 */
var _execute = function _execute ( key, path, data ) {
  var response = ( key instanceof Response ? key : new Response( key ) );

  path = unescape( path );

  if ( data && data.trim() !== "" ) {
    data = unescape( data );
    try {
      data = JSON.parse( data );
    } catch ( error ) {
      return response.fail( error );
    }
  }

  if ( handlers[ path ] ) {
    handlers[ path ]( data, response );
    return response;
  } else {
    return response.fail( new Error( 'No handler defined for path: ' + path ) );
  }

};

// Exports
module.exports.register = register;
module.exports._execute = _execute;
module.exports._reset = _reset;
