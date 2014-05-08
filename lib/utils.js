var where = function where ( list, properties ) {
  var passed = [],
      passing,
      i = 0,
      listLength = list.length,
      key;

  for ( i; i < listLength; ++i ) {
    passing = false;
    for ( key in properties ) {
      if ( properties.hasOwnProperty( key ) ) {
        if ( properties[ key ] === list[ i ][ key ] ) {
          passing = true;
        } else {
          passing = false;
          break;
        }
      }
    }
    if ( passing ) {
      passed.push( list[ i ] );
    }
  }

  return passed;
};

// Exports
module.exports.where = where;
