module.exports = function Response( key ) {
  this.error = function error( error ) {
    if ( window.android ) {
      window.android.reply( key, error );
    } else {
      console.log( 'Error %d: %s', key, error );
    }
  };

  this.send = function send( response ) {
    if ( window.android ) {
      window.android.reply( key, null, response );
    } else {
      console.log( 'Send %d: %s', key, error );
    }
  };
};
