var assert = require( 'assert' ),
    Pig = require( '../index' );

var pig = new Pig();

describe( 'Events', function() {
  beforeEach( function() {
    pig._reset();

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
    pig.on( 'event', function() {
      done();
    } );

    pig.emit( 'event' );
  } );


  it( 'can emit events with data strings', function( done ) {
    // Setup the handler
    pig.on( 'event', function( data ) {
      assert( data === 'a string' );
      done();
    } );

    pig.emit( 'event', 'a string' );
  } );


  it( 'can emit events with data numbers', function( done ) {
    // Setup the handler
    pig.on( 'event', function( data ) {
      assert( data === 1 );
      done();
    } );

    pig.emit( 'event', 1 );
  } );


  it( 'can emit events with data boolean', function( done ) {
    // Setup the handler
    pig.on( 'event', function( data ) {
      assert( data );
      done();
    } );

    pig.emit( 'event', true );
  } );


  it( 'can emit events with data object', function( done ) {
    // Setup the handler
    pig.on( 'event', function( data ) {
      assert( data.name === "Jake" );
      done();
    } );

    pig.emit( 'event', {name:"Jake"} );
  } );


  it( 'can emit events with data array', function( done ) {
    // Setup the handler
    pig.on( 'event', function( data ) {
      assert( data.length === 2 );
      assert( data[0] === "One" );
      assert( data[1] === "Two" );
      done();
    } );

    pig.emit( 'event', ["One", "Two"] );
  } );


  it( 'can emit events from native code', function( done ) {
    // Setup the handler
    pig.on( 'event', function() {
      assert( true );
      done();
    } );

    pig.emit( 'event', null, true );
  } );

} );
