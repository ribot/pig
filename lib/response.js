module.exports = function Response( key ) {
  this.error = function error( error ) {
    if ( typeof window != "undefined" && window.android ) {
      window.android.reply( key, error, null );
    } else {
      console.log( 'Error %d: %s', key, error );
    }
  };

  this.send = function send( response ) {
    if ( typeof window != "undefined" && window.android ) {
      window.android.reply( key, null, response );
    } else {
      console.log( 'Send %d: %s', key, response );
    }
  };
};
