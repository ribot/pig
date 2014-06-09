// Dependencies
var nativeInterface = require( './native-interface' );

/**
 * Response constructor
 */
var Response = function Response( key ) {
  this.key = key.toString();
  return this;
};

Response.prototype = {

  /**
   * Execute native interface's failure callback
   */
  fail: function fail( error, code ) {

    if ( typeof error == 'string' ) {
      error = new Error( error );
    } else if ( typeof error == 'undefined' || typeof error == 'null' ) {
      error = new Error( '' );
    }

    code = code || '';

    if ( this.key !== -1 ) {
      nativeInterface.fail( this.key, code, error.name, error.message );
    }

    _preventRecurrence.call( this );

  },

  /**
   * Execute native interface's success callback
   */
  success: function success( data ) {
    var json;

    if ( data ) {
      try {
        json = JSON.stringify( data );
      } catch ( error ) {
        return this.fail( error );
      }
    }

    json = json || '';

    if ( this.key !== -1 ) {
      nativeInterface.success( this.key, json );
    }

    _preventRecurrence.call( this );

  }

};

/**
 * Replaces the success and fail callbacks in a response instance with a
 * function that throws an Error
 */
function _preventRecurrence() {

  this.fail = this.success = function recurrenceHandler() {
    throw new Error( 'Cannot respond more than once using the same response object.' );
  };

}

// Exports
module.exports = Response;
