var assert = require( 'assert' ),
    piggie = require( '../index' );

describe( 'Events', function() {
  beforeEach( function() {
    piggie._reset();

    window = {
      android: {
        fail: function() {},
        success: function() {},
        event: function() {}
      }
    };
  } );


  it( 'can emit events', function( done ) {
    // Setup the handler
    piggie.on( 'event', function() {
      done();
    } );

    piggie.emit( 'event' );
  } );


  it( 'can emit events with data strings', function( done ) {
    // Setup the handler
    piggie.on( 'event', function( data ) {
      assert( data === 'a string' );
      done();
    } );

    piggie.emit( 'event', 'a string' );
  } );


  it( 'can emit events with data numbers', function( done ) {
    // Setup the handler
    piggie.on( 'event', function( data ) {
      assert( data === 1 );
      done();
    } );

    piggie.emit( 'event', 1 );
  } );


  it( 'can emit events with data boolean', function( done ) {
    // Setup the handler
    piggie.on( 'event', function( data ) {
      assert( data );
      done();
    } );

    piggie.emit( 'event', true );
  } );


  it( 'can emit events with data object', function( done ) {
    // Setup the handler
    piggie.on( 'event', function( data ) {
      assert( data.name === "Jake" );
      done();
    } );

    piggie.emit( 'event', {name:"Jake"} );
  } );


  it( 'can emit events with data array', function( done ) {
    // Setup the handler
    piggie.on( 'event', function( data ) {
      assert( data.length === 2 );
      assert( data[0] === "One" );
      assert( data[1] === "Two" );
      done();
    } );

    piggie.emit( 'event', ["One", "Two"] );
  } );


  it( 'can emit events from native code', function( done ) {
    // Setup the handler
    piggie.on( 'event', function() {
      assert( true )
      done();
    } );

    piggie._nativeEmit( 'event' );
  } );


  it( 'throws and error with ore than one data argument', function() {
    try {
      piggie.emit( 'event', true, true );
    } catch ( e ) {
      return assert( true );
    }

    assert( false );
  } );
} );
