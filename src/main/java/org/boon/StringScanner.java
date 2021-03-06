package org.boon;

import org.boon.core.reflection.FastStringUtils;
import org.boon.primitive.CharScanner;
import org.boon.primitive.Chr;


public class StringScanner {


    public static boolean isDigits( String input ) {
        return CharScanner.isDigits( FastStringUtils.toCharArray( input ) );
    }

    public static String[] split( final String string,
                                  final char split ) {

        char[][] comps = CharScanner.split( FastStringUtils.toCharArray( string ), split );

        return Str.fromCharArrayOfArrayToStringArray( comps );

    }

    public static String[] splitByChars( final String string,
                                         final char... delimiters ) {

        char[][] comps = CharScanner.splitByChars( FastStringUtils.toCharArray( string ), delimiters );

        return Str.fromCharArrayOfArrayToStringArray( comps );

    }

    public static String[] splitByDelimiters( final String string,
                                              final String delimiters ) {

        char[][] comps = CharScanner.splitByChars( FastStringUtils.toCharArray( string ), delimiters.toCharArray() );

        return Str.fromCharArrayOfArrayToStringArray( comps );

    }


    public static String[] splitByCharsNoneEmpty( final String string, final char... delimiters ) {

        char[][] comps = CharScanner.splitByCharsNoneEmpty( FastStringUtils.toCharArray( string ), delimiters );
        return Str.fromCharArrayOfArrayToStringArray( comps );
    }

    public static String removeChars( final String string, final char... delimiters ) {
        char[][] comps = CharScanner.splitByCharsNoneEmpty( FastStringUtils.toCharArray( string ), delimiters );
        return new String(Chr.add ( comps ));
    }

    public static String[] splitByCharsNoneEmpty( final String string, int start, int end, final char... delimiters ) {
        Exceptions.requireNonNull( string );

        char[][] comps = CharScanner.splitByCharsNoneEmpty( FastStringUtils.toCharArray( string ), start, end, delimiters );
        return Str.fromCharArrayOfArrayToStringArray( comps );
    }

}
