package org.boon.json.implementation;

import org.boon.Exceptions;
import org.boon.IO;
import org.boon.core.Conversions;
import org.boon.core.Typ;
import org.boon.core.Type;
import org.boon.core.Value;
import org.boon.core.reflection.MapObjectConversion;
import org.boon.core.reflection.fields.FieldsAccessor;
import org.boon.json.JsonParser;
import org.boon.json.JsonParserAndMapper;
import org.boon.primitive.CharBuf;

import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class BaseJsonParserAndMapper implements JsonParserAndMapper {



    protected final FieldsAccessor fieldsAccessor;
    protected final JsonParser parser;
    private final CharBuf builder = CharBuf.create( 20 );

    protected Charset charset  = StandardCharsets.UTF_8;


    protected int bufSize  = 256;



    public BaseJsonParserAndMapper( JsonParser parser, FieldsAccessor fieldsAccessor ) {
        this.fieldsAccessor = fieldsAccessor;
        this.parser = parser;

    }



    protected final  <T> T convert( Class<T> clz, Object object ) {
            if (object == null ) {
                return null;
            }

            Type coerceTo = Type.getType(clz);

            switch ( coerceTo ) {
                case MAP:
                case LIST:
                case OBJECT:
                    return (T)object;

            }

            Type coerceFrom = Type.getType(object.getClass());

            switch ( coerceFrom ) {

                case VALUE_MAP:
                    return MapObjectConversion.fromValueMap( fieldsAccessor, ( Map<String, Value> ) object, clz );

                case MAP:
                    return MapObjectConversion.fromMap ( fieldsAccessor, ( Map<String, Object> ) object, clz );

                case VALUE:
                    return (T)( (Value) object).toValue ();

                case LIST:
                    return (T)MapObjectConversion.convertListOfMapsToObjects(this.fieldsAccessor, clz, (List<Object>)object);

                default:
                    if ( Typ.isBasicTypeOrCollection( clz ) ) {
                        return Conversions.coerce(coerceTo, clz, object);
                    } else {
                        return (T)object;
                    }
            }

    }


    public void setCharset( Charset charset ) {
        this.charset = charset;
    }


    @Override
    public Map<String, Object> parseMap ( String jsonString ) {
        return (Map<String, Object>) parse ( jsonString );
    }

    @Override
    public <T> List<T> parseList ( Class<T> componentType, String jsonString ) {
        List<Object> list =  parse ( List.class, jsonString );
        return MapObjectConversion.convertListOfMapsToObjects( fieldsAccessor, componentType, list );
    }



    @Override
    public <T> List<T> parseList ( Class<T> componentType, Reader reader ) {
        List<Object> list =  parse ( List.class, reader );
        return MapObjectConversion.convertListOfMapsToObjects( fieldsAccessor, componentType, list );
    }

    @Override
    public <T> List<T> parseList ( Class<T> componentType, InputStream input ) {
        List<Object> list =  parse ( List.class, input );
        return MapObjectConversion.convertListOfMapsToObjects( fieldsAccessor, componentType, list );
    }

    @Override
    public <T> List<T> parseList ( Class<T> componentType, InputStream input, Charset charset ) {
        List<Object> list =  parse ( List.class, input, charset );
        return MapObjectConversion.convertListOfMapsToObjects( fieldsAccessor, componentType, list );
    }

    @Override
    public <T>  List<T> parseList ( Class<T> componentType, byte[] jsonBytes ) {
        List<Object> list =  parse ( List.class, jsonBytes );
        return MapObjectConversion.convertListOfMapsToObjects( fieldsAccessor, componentType, list );
    }

    @Override
    public <T>  List<T> parseList ( Class<T> componentType, byte[] jsonBytes, Charset charset ) {
        List<Object> list =  parse ( List.class, jsonBytes, charset );
        return MapObjectConversion.convertListOfMapsToObjects( fieldsAccessor, componentType, list );
    }

    @Override
    public <T>  List<T> parseList ( Class<T> componentType, char[] chars ) {
        List<Object> list =  parse ( List.class, chars );
        return MapObjectConversion.convertListOfMapsToObjects( fieldsAccessor, componentType, list );
    }

    @Override
    public <T>  List<T> parseList ( Class<T> componentType, CharSequence jsonSeq ) {
        List<Object> list =  parse ( List.class, jsonSeq );
        return MapObjectConversion.convertListOfMapsToObjects( fieldsAccessor, componentType, list );
    }

    @Override
    public <T>  List<T> parseListFromFile ( Class<T> componentType, String fileName ) {
        List<Object> list =  parseFile ( List.class, fileName );
        return MapObjectConversion.convertListOfMapsToObjects( fieldsAccessor, componentType, list );
    }


    @Override
    public <T> T parse( Class<T> type, String jsonString ) {
        return convert( type, parse( jsonString ) );
    }

    @Override
    public <T> T parse( Class<T> type, byte[] bytes ) {
        return convert( type, parse( bytes ) );
    }

    @Override
    public <T> T parse( Class<T> type, byte[] bytes, Charset charset ) {
        return convert (type, parse(bytes, charset));
    }


    @Override
    public Date parseDate ( String jsonString ) {
        return Conversions.toDate ( parse ( jsonString ) );
    }

    @Override
    public Date parseDate ( InputStream input ) {
        return Conversions.toDate ( parse ( input ) );
    }

    @Override
    public Date parseDate ( InputStream input, Charset charset ) {
        return Conversions.toDate ( parse ( input ) );
    }

    @Override
    public Date parseDate ( byte[] jsonBytes ) {
        return Conversions.toDate ( parse ( jsonBytes ) );
    }

    @Override
    public Date parseDate ( byte[] jsonBytes, Charset charset ) {
        return Conversions.toDate ( parse ( jsonBytes, charset ) );
    }

    @Override
    public Date parseDate ( char[] chars ) {
        return Conversions.toDate ( parse ( chars ) );
    }

    @Override
    public Date parseDate ( CharSequence jsonSeq ) {
        return Conversions.toDate ( parse ( jsonSeq ) );
    }

    @Override
    public Date parseDateFromFile ( String fileName ) {
        return Conversions.toDate ( parseFile ( fileName ) );
    }

    @Override
    public float[] parseFloatArray ( String jsonString ) {
        List<Object> list = (List <Object> ) parse ( jsonString );
        return Conversions.farray ( list );
    }

    @Override
    public double[] parseDoubleArray ( String jsonString ) {
        List<Object> list = (List <Object> ) parse ( jsonString );
        return Conversions.darray ( list );
    }

    @Override
    public long[] parseLongArray ( String jsonString ) {
        List<Object> list = (List <Object> ) parse ( jsonString );
        return Conversions.larray ( list );
    }

    @Override
    public int [] parseIntArray ( String jsonString ) {
        List<Object> list = (List <Object> ) parse ( jsonString );
        return Conversions.iarray ( list );
    }

    @Override
    public <T extends Enum> T  parseEnum (  Class<T> type, String jsonString ) {
        Object obj = parse ( jsonString );
        return Conversions.toEnum ( type, obj );
    }

    @Override
    public short parseShort ( String jsonString ) {
        return Conversions.toShort ( parse (jsonString) );
    }

    @Override
    public byte parseByte ( String jsonString ) {

        return Conversions.toByte ( parse ( jsonString ) );
    }

    @Override
    public char parseChar ( String jsonString ) {
        return Conversions.toChar ( parse ( jsonString ) );
    }

    @Override
    public char[] parseCharArray ( String jsonString ) {
        return Conversions.carray ( parse ( jsonString ) );
    }

    @Override
    public byte[] parseByteArray ( String jsonString ) {
        return Conversions.barray ( parse ( jsonString ) );
    }

    @Override
    public short[] parseShortArray ( String jsonString ) {
        return Conversions.sarray ( parse ( jsonString ) );
    }



    @Override
    public int parseInt ( String jsonString ) {
        return Conversions.toInt ( parse ( jsonString ) );
    }

    @Override
    public int parseInt ( InputStream input ) {
        return Conversions.toInt ( parse (  input ) );
    }

    @Override
    public int parseInt ( InputStream input, Charset charset ) {
        return Conversions.toInt ( parse (  input, charset ) );
    }

    @Override
    public int parseInt ( byte[] jsonBytes ) {
        return Conversions.toInt ( parse (  jsonBytes ) );
    }

    @Override
    public int parseInt ( byte[] jsonBytes, Charset charset ) {
        return Conversions.toInt ( parse (  jsonBytes, charset ) );
    }

    @Override
    public int parseInt ( char[] chars ) {
        return Conversions.toInt ( parse (  chars ) );
    }

    @Override
    public int parseInt ( CharSequence jsonSeq ) {
        return Conversions.toInt ( parse (  jsonSeq ) );
    }

    @Override
    public int parseIntFromFile ( String fileName ) {
        return Conversions.toInt ( parseFile (  fileName ) );
    }




    @Override
    public long parseLong ( String jsonString ) {
        return Conversions.toLong ( parse (jsonString) );
    }

    @Override
    public long parseLong ( InputStream input ) {
        return Conversions.toLong ( parse (  input ) );
    }

    @Override
    public long parseLong ( InputStream input, Charset charset ) {
        return Conversions.toLong ( parse (  input, charset ) );
    }

    @Override
    public long parseLong ( byte[] jsonBytes ) {
        return Conversions.toLong ( parse (  jsonBytes ) );
    }

    @Override
    public long parseLong ( byte[] jsonBytes, Charset charset ) {
        return Conversions.toLong ( parse (  jsonBytes, charset ) );
    }

    @Override
    public long parseLong ( char[] chars ) {
        return Conversions.toLong ( parse (  chars ) );
    }

    @Override
    public long parseLong ( CharSequence jsonSeq ) {
        return Conversions.toLong ( parse (  jsonSeq ) );
    }

    @Override
    public long parseLongFromFile ( String fileName ) {
        return Conversions.toLong ( parseFile ( fileName ) );
    }

    @Override
    public String parseString( String value ) {
        return Conversions.toString( parse ( value ) );
    }

    @Override
    public String parseString( InputStream value ) {
        return Conversions.toString( parse ( value ) );
    }

    @Override
    public String parseString( InputStream value, Charset charset ) {
        return Conversions.toString( parse ( value, charset ) );
    }

    @Override
    public String parseString( byte[] value ) {
        return Conversions.toString( parse ( value ) );
    }

    @Override
    public String parseString( byte[] value, Charset charset ) {
        return Conversions.toString( parse ( value, charset ) );
    }

    @Override
    public String parseString( char[] value ) {
        return Conversions.toString( parse ( value ) );
    }

    @Override
    public String parseString( CharSequence value ) {
        return Conversions.toString( parse ( value ) );
    }

    @Override
    public String parseStringFromFile( String value ) {
        return Conversions.toString( parseFile ( value ) );
    }


    @Override
    public double parseDouble ( String value ) {
        return Conversions.toDouble ( parse ( value ) );
    }

    @Override
    public double parseDouble ( InputStream value ) {
        return Conversions.toDouble ( parse (  value ) );
    }

    @Override
    public double parseDouble ( byte[] value ) {
        return Conversions.toDouble ( parse (  value ) );
    }

    @Override
    public double parseDouble ( char[] value ) {
        return Conversions.toDouble ( parse (  value ) );
    }

    @Override
    public double parseDouble ( CharSequence value ) {
        return Conversions.toDouble ( parse (  value ) );
    }

    @Override
    public double parseDouble ( byte[] value, Charset charset ) {
        return Conversions.toDouble ( parse (  value, charset ) );
    }

    @Override
    public double parseDouble ( InputStream value, Charset charset ) {
        return Conversions.toDouble ( parse (  value, charset ) );
    }

    @Override
    public double parseDoubleFromFile ( String fileName ) {
        return Conversions.toDouble ( parseFile ( fileName ) );
    }


    @Override
    public float parseFloat ( String value ) {
        return Conversions.toFloat ( parse ( value ) );
    }

    @Override
    public float parseFloat ( InputStream value ) {
        return Conversions.toFloat ( parse (  value ) );
    }

    @Override
    public float parseFloat ( byte[] value ) {
        return Conversions.toFloat ( parse (  value ) );
    }

    @Override
    public float parseFloat ( char[] value ) {
        return Conversions.toFloat ( parse (  value ) );
    }

    @Override
    public float parseFloat ( CharSequence value ) {
        return Conversions.toFloat ( parse (  value ) );
    }

    @Override
    public float parseFloat ( byte[] value, Charset charset ) {
        return Conversions.toFloat ( parse (  value, charset ) );
    }

    @Override
    public float parseFloat ( InputStream value, Charset charset ) {
        return Conversions.toFloat ( parse (  value, charset ) );
    }

    @Override
    public float parseFloatFromFile ( String fileName ) {
        return Conversions.toFloat ( parseFile ( fileName ) );
    }




    @Override
    public BigDecimal parseBigDecimal ( String value ) {
        return Conversions.toBigDecimal ( parse ( value ) );
    }

    @Override
    public BigDecimal parseBigDecimal ( InputStream value ) {
        return Conversions.toBigDecimal ( parse ( value ) );
    }

    @Override
    public BigDecimal parseBigDecimal ( byte[] value ) {
        return Conversions.toBigDecimal ( parse ( value ) );
    }

    @Override
    public BigDecimal parseBigDecimal ( char[] value ) {
        return Conversions.toBigDecimal ( parse ( value ) );
    }

    @Override
    public BigDecimal parseBigDecimal ( CharSequence value ) {
        return Conversions.toBigDecimal ( parse ( value ) );
    }

    @Override
    public BigDecimal parseBigDecimal ( byte[] value, Charset charset ) {
        return Conversions.toBigDecimal ( parse ( value, charset ) );
    }

    @Override
    public BigDecimal parseBigDecimal ( InputStream value, Charset charset ) {
        return Conversions.toBigDecimal ( parse ( value, charset ) );
    }

    @Override
    public BigDecimal parseBigDecimalFromFile ( String fileName ) {
        return Conversions.toBigDecimal ( parseFile ( fileName ) );
    }




    @Override
    public BigInteger parseBigInteger ( String value ) {
        return Conversions.toBigInteger ( parse ( value ) );
    }

    @Override
    public BigInteger parseBigInteger ( InputStream value ) {
        return Conversions.toBigInteger ( parse ( value ) );
    }

    @Override
    public BigInteger parseBigInteger ( byte[] value ) {
        return Conversions.toBigInteger ( parse ( value ) );
    }

    @Override
    public BigInteger parseBigInteger ( char[] value ) {
        return Conversions.toBigInteger ( parse ( value ) );
    }

    @Override
    public BigInteger parseBigInteger ( CharSequence value ) {
        return Conversions.toBigInteger ( parse ( value ) );
    }

    @Override
    public BigInteger parseBigInteger ( byte[] value, Charset charset ) {
        return Conversions.toBigInteger ( parse ( value, charset ) );
    }

    @Override
    public BigInteger parseBigInteger ( InputStream value, Charset charset ) {
        return Conversions.toBigInteger ( parse ( value, charset ) );
    }

    @Override
    public BigInteger parseBigIntegerFile ( String fileName ) {
        return Conversions.toBigInteger ( parseFile ( fileName ) );
    }





    @Override
    public Object parseDirect ( byte[] value ) {
          builder.addAsUTF( value );
          return parse(builder.readForRecycle() );
    }


    @Override
    public <T> T parseDirect( Class<T> type, byte[] value ) {
          builder.addAsUTF( value );
          return parse ( type, builder.readForRecycle () );
    }




    @Override
    public  <T> T parseAsStream( Class<T> type, byte[] value ) {
        return this.parse ( type, new ByteArrayInputStream ( value ) );
    }


    @Override
    public  <T> T parse( Class<T> type, CharSequence charSequence ) {
        return parse ( type, charSequence.toString () );
    }

    @Override
    public <T> T parse( Class<T> type, char[] chars ) {
        return convert(type, parse(chars));
    }

    @Override
    public Object parseAsStream ( byte[] value ) {
        return this.parse(  new ByteArrayInputStream( value ) );
    }

    @Override
    public Object parseFile ( String fileName ) {
        try {
            Path filePath = IO.path ( fileName );
            long size = Files.size ( filePath );
            size = size > 2_000_000_000 ? 1_000_000 : size;
            Reader reader = Files.newBufferedReader ( IO.path ( fileName ), charset);
            fileInputBuf = IO.read( reader, fileInputBuf, (int)size );
            return parse(  fileInputBuf.readForRecycle() );
        } catch ( IOException ex ) {
            return Exceptions.handle ( Object.class, fileName, ex );
        }

    }

    @Override
    public void close() {

    }


    private CharBuf fileInputBuf;

    @Override
    public  <T> T parse( Class<T> type, Reader reader ) {

        fileInputBuf = IO.read( reader, fileInputBuf, bufSize );
        return parse( type, fileInputBuf.readForRecycle() );

    }


    @Override
    public  <T> T parse( Class<T> type, InputStream input ) {
        fileInputBuf = IO.read( input, fileInputBuf, charset, bufSize );
        return parse( type, fileInputBuf.readForRecycle() );
    }


    @Override
    public  <T> T parse( Class<T> type, InputStream input, Charset charset ) {
        fileInputBuf = IO.read( input, fileInputBuf, charset, 256 );
        return parse( type, fileInputBuf.readForRecycle() );
    }


    @Override
    public <T> T parseFile( Class<T> type, String fileName ) {

        try {
            Path filePath = IO.path ( fileName );
            long size = Files.size ( filePath );
            size = size > 2_000_000_000 ? 1_000_000 : size;
            Reader reader = Files.newBufferedReader ( IO.path ( fileName ), charset);
            fileInputBuf = IO.read( reader, fileInputBuf, (int)size );
            return parse( type, fileInputBuf.readForRecycle() );
        } catch ( IOException ex ) {
            return Exceptions.handle ( type, fileName, ex );
        }
    }





    @Override
    public Map<String, Object> parseMap ( char[] value ) {
        return (Map<String, Object>) parse(value);
    }

    @Override
    public Map<String, Object> parseMap ( byte[] value ) {
        return (Map<String, Object>) parse(value);
    }

    @Override
    public Map<String, Object> parseMap ( byte[] value, Charset charset ) {
        return (Map<String, Object>) parse(value, charset);
    }

    @Override
    public Map<String, Object> parseMap ( InputStream value, Charset charset ) {
        return (Map<String, Object>) parse(value, charset);
    }

    @Override
    public Map<String, Object> parseMap ( CharSequence value ) {
        return (Map<String, Object>) parse(value);
    }

    @Override
    public Map<String, Object> parseMap ( InputStream value ) {
        return (Map<String, Object>) parse(value);
    }

    @Override
    public Map<String, Object> parseMap ( Reader value ) {
        return (Map<String, Object>) parse ( value );
    }

    @Override
    public Map<String, Object> parseMapFromFile ( String file ) {
        return (Map<String, Object>) parseFile(file);
    }




    @Override
    public Object parse( String jsonString ) {
        return parser.parse( jsonString );
    }

    @Override
    public Object parse( byte[] bytes ) {
        return parser.parse( bytes );
    }

    @Override
    public Object parse( byte[] bytes, Charset charset ) {
        return parser.parse( bytes, charset );
    }

    @Override
    public Object parse( CharSequence charSequence ) {
        return parser.parse( charSequence );
    }


    @Override
    public Object parse(  char[] chars ){
        return parser.parse( chars );
    }

    @Override
    public Object parse(  Reader reader ){
        return parser.parse( reader );
    }

    @Override
    public Object parse(  InputStream input ){
        return parser.parse( input );
    }

    @Override
    public Object parse(  InputStream input, Charset charset ){
        return parser.parse( input, charset );
    }


}
